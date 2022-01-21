package com.atguigu.gmall.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.cart.feign.CartFeign;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.PaymentType;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.util.GmallThreadLocalUtils;
import com.atguigu.gmall.product.feign.ProductFeign;
import com.atguigu.gmall.pay.fegn.PayFeign;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: wujijun
 * @Description: 订单的业务接口实现类
 * @Date Created in 2022-01-18-21:31
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderInfoServiceImpl implements OrderInfoService {

    @Resource
    private OrderInfoMapper orderInfoMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private CartFeign cartFeign;

    @Autowired
    private ProductFeign productFeign;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PayFeign payFeign;

    /**
     * @param orderInfo
     * @return
     * @ClassName orderInfoService
     * @Description 新增订单(要解决分布式事务 ， 因为操作的不是的同一个微服务)
     * @Author wujijun
     * @Date 2022/1/18 21:31
     * @Param [orderInfo]
     * @Return void
     */
    @Override
    public String addOrder(OrderInfo orderInfo) {
        //校验参数
        if (orderInfo == null) {
            throw new RuntimeException("参数错误，创建订单失败！");
        }
        //获取当前登录的用户名
        String username = GmallThreadLocalUtils.getUserName();
        //重复提交订单
        Long increment = stringRedisTemplate.opsForValue().increment("User_Cart_Lock_" + username, 1);
        //如果increment大于1，则在生成订单前，还有一个请求生成了该笔订单
        if (increment > 1) {
            throw new RuntimeException("新增订单失败，在此之前已生成一个相同订单");
        }
        try {
            //查询该笔订单中购物车中的商品的总数量，总价格以及购商品列表（防止通过前端页面修改数据）
            Map<String, Object> cartForOrder = cartFeign.getCartForOrder();
            //获取商品总数量
            int totalNum = Integer.parseInt(cartForOrder.get("totalNum").toString());
            //获取商品的总价格
            double totalMoney = Double.parseDouble(cartForOrder.get("totalMoney").toString());
            //获取商品列表
            List cartInfoList = (List) cartForOrder.get("cartInfoList");
            //补全订单对象中的属性
            orderInfo.setTotalAmount(new BigDecimal(totalMoney));
            orderInfo.setOrderStatus(OrderStatus.UNPAID.getComment());
            orderInfo.setUserId("wujijun");
            orderInfo.setCreateTime(new Date());
            //三十分钟后失效
            orderInfo.setExpireTime(new Date(System.currentTimeMillis() + (30 * 60 * 1000)));
            orderInfo.setProcessStatus(ProcessStatus.UNPAID.getComment());
            //新增订单，拿到订单号
            int insert = orderInfoMapper.insert(orderInfo);
            //判断是否新增订单成功
            if (insert <= 0) {
                throw new RuntimeException("新增订单失败！");
            }
            //通过商品列表和订单号，新增订单详情
            Map<String, Object> orderDetails = addOrderDetail(cartInfoList, orderInfo.getId());
            //扣库存
            productFeign.delCountStock(orderDetails);
            //生成订单后，请求购物车数据
            cartFeign.removeCart();
            //超时未付款--30分钟后自动过期
            rabbitTemplate.convertAndSend("normal_exchange",
                    "order.timeout",
                    orderInfo.getId() + "",
                    new MessagePostProcessor() {
                        @Override
                        public Message postProcessMessage(Message message) throws AmqpException {
                            //获取消息的属性
                            MessageProperties messageProperties = message.getMessageProperties();
                            //设置消息的过期时间(半小时过期)，测试可以设置时间短点（10s）
                            messageProperties.setExpiration("10000");
                            return message;
                        }
                    });
            //调用支付微服务的接口，获取支付的二维码
            String wxPayUrl = payFeign.getWxPayUrl("订单的描述：wujijun",
                    orderInfo.getId() + "",
                    totalMoney * 100 + "");
            //返回二维码的地址
            return wxPayUrl;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("生成订单失败！");
        } finally {
            //删除缓存中设置的重读提交的数据（标识位）
            stringRedisTemplate.delete("User_Cart_Lock_" + username);
        }
    }

    /**
     * @param orderId
     * @param status
     * @ClassName OrderInfoService
     * @Description 取消订单
     * @Author wujijun
     * @Date 2022/1/20 22:55
     * @Param [orderId, status；主动取消，超时取消]
     * @Return void
     */
    @Override
    public void cancelOrder(Long orderId, String status) {
        //校验参数
        if (orderId == null) {
            return;
        }
        //查询订单信息
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        //判断该订单是否存在
        if (orderInfo == null || orderInfo.getId() == null) {
            return;
        }
        //防止用户一边取消一边付款，所以要先关闭交易，才能取消
        if (payFeign.closePay(orderId + "")) {
            //判断订单状态（未付款），解决幂等性问题
            if (!orderInfo.getOrderStatus().equals(OrderStatus.UNPAID.getComment())) {
                return;
            }
            //修改订单的状态（已付款）
            orderInfo.setOrderStatus(status);
            orderInfo.setProcessStatus(status);
            //保存到数据库
            orderInfoMapper.updateById(orderInfo);
            //回滚库存，先查订单详情
            restoreInventory(orderId);
        }
    }

    /**
     * @ClassName OrderInfoServiceImpl
     * @Description 修改订单的支付状态
     * @Author wujijun
     * @Date 2022/1/21 22:53
     * @Param [map, payWay]
     * @Return void
     */
    @Override
    public void updateOrderStatus(Map<String, String> map, String payWay) {
        String tradeNo = "";
        //获取支付的渠道（微信 or 支付宝）
        if (payWay.equals(PaymentType.WEIXIN.getComment())) {
            //微信支付的渠道
            if (map.get("return_code").equals("SUCCESS")
                    && map.get("result_code").equals("SUCCESS")) {
                //判断是否支付成功，成功后获取支付的交易号
                tradeNo = map.get("transaction_id");
            }
        } else if (payWay.equals(PaymentType.ALIPAY.getComment())) {
            //支付宝支付的渠道
            if (map.get("trade_status").equals("TRADE_SUCCESS")) {
                //获取支付宝中的交易号
                tradeNo = map.get("trade_no");
            }
        }
        //获取订单号
        String orderId = map.get(" out_trade_no");
        //通过订单号查询订单
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        //判断状态是不是未支付的，防止幂等性问题
        if (orderInfo.getOrderStatus().equals(OrderStatus.UNPAID.getComment())) {
            //修改订单状态
            orderInfo.setOrderStatus(OrderStatus.PAID.getComment());
            //修改进度状态
            orderInfo.setProcessStatus(OrderStatus.PAID.getComment());
            //设置第三方的支付信息
            orderInfo.setOutTradeNo(tradeNo);
            orderInfo.setTradeBody(JSONObject.toJSONString(map));
            //修改订单
            orderInfoMapper.updateById(orderInfo);
        }

    }

    /**
     * @ClassName OrderInfoServiceImpl
     * @Description 回滚库存
     * @Author wujijun
     * @Date 2022/1/20 23:45
     * @Param [orderId]
     * @Return void
     */
    private void restoreInventory(Long orderId) {
        List<OrderDetail> orderDetails = orderDetailMapper.selectList(
                new LambdaQueryWrapper<OrderDetail>().eq(OrderDetail::getOrderId, orderId));
        //需要回滚库存的map
        Map<String, Object> params = new ConcurrentHashMap<>();
        //遍历订单详情，回滚数据
        orderDetails.stream().forEach(orderDetail -> {
            //获取商品id
            Long skuId = orderDetail.getSkuId();
            //获取商品数量
            Integer skuNum = orderDetail.getSkuNum();
            //保存需要回滚的信息
            params.put(skuId + "", skuNum);
        });
        //库存回滚
        productFeign.rollBackStock(params);
    }

    /**
     * @return
     * @ClassName orderInfoServiceImpl
     * @Description 通过订单id新增订单详情
     * @Author wujijun
     * @Date 2022/1/18 22:13
     * @Param [cartInfoList, id]
     * @Return void
     */
    private Map<String, Object> addOrderDetail(List cartInfoList, Long id) {
        //创建一个用来扣减库存的对象
        Map<String, Object> params = new ConcurrentHashMap<>();
        //用流式编程新增订单详情
        cartInfoList.stream().forEach(object -> {
            //序列化
            String jsonString = JSONObject.toJSONString(object);
            //反序列化
            CartInfo cartInfo = JSONObject.parseObject(jsonString, CartInfo.class);
            //初始化一个返回的结果对象
            OrderDetail orderDetail = new OrderDetail();
            //设置参数
            orderDetail.setOrderId(id);
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setOrderPrice(cartInfo.getSkuPrice());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            // 新增订单详情
            int insert = orderDetailMapper.insert(orderDetail);
            //判断订单详情是不是新增成功
            if (insert <= 0) {
                throw new RuntimeException("新增订单详情失败！");
            }
            params.put(cartInfo.getSkuId() + "", cartInfo.getSkuNum());
        });
        //把新增的商品详情返回
        return params;
    }


}
