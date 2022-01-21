package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: wujijun
 * @Description: 获取商品详情信息接口的实现类
 * @Date Created in 2022-01-05-15:41
 * @Modified By:
 */
@Service
@Log4j2
@Transactional(rollbackFor = Exception.class)
public class ItemServiceImpl implements ItemService {

    @Resource
    SkuInfoMapper skuInfoMapper;

    @Resource
    SkuImageMapper skuImageMapper;

    @Resource
    BaseCategoryViewMapper baseCategoryViewMapper;

    @Resource
    SpuSaleAttrMapper spuSaleAttrMapper;

    @Resource
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Resource
    RedissonClient redissonClient;

    @Resource
    RedisTemplate redisTemplate;

    @Resource
    BaseTradeMarkMapper baseTradeMarkMapper;

    @Resource
    BaseAttrInfoMapper baseAttrInfoMapper;


    /**
     * @param skuId
     * @ClassName ItemService
     * @Description 查询商品名字和默认图片；
     * @Author wujijun
     * @Date 2022/1/5 15:40
     * @Param [skuId]
     * @Return com.atguigu.gmall.model.product.SkuInfo
     */
    @Override
    public SkuInfo getSkuInfo(Long skuId) {

        return skuInfoMapper.selectById(skuId);
    }

    /**
     * @param skuId
     * @ClassName ItemService
     * @Description 查询商品的所有的图片
     * @Author wujijun
     * @Date 2022/1/5 17:03
     * @Param [skuId]
     * @Return java.util.List<com.atguigu.gmall.model.product.SkuImage>
     */
    @Override
    public List<SkuImage> getSkuImages(Long skuId) {
        return skuImageMapper.selectList(new LambdaQueryWrapper<SkuImage>()
                .eq(SkuImage::getSkuId, skuId));
    }

    /**
     * @param skuId
     * @ClassName ItemService
     * @Description 查询商品价格
     * @Author wujijun
     * @Date 2022/1/5 18:14
     * @Param [skuId]
     * @Return java.math.BigDecimal
     */
    @Override
    public BigDecimal getPrice(Long skuId) {
        return skuInfoMapper.selectById(skuId).getPrice();
    }

    /**
     * @param c3Id
     * @ClassName ItemService
     * @Description 查询一二三级分类的信息
     * @Author wujijun
     * @Date 2022/1/5 18:26
     * @Param [skuId]
     * @Return com.atguigu.gmall.model.product.BaseCategoryView
     */
    @Override
    public BaseCategoryView getBaseCategory(Long c3Id) {
        return baseCategoryViewMapper.selectById(c3Id);
    }

    /**
     * @param spuId
     * @param skuId
     * @ClassName ItemService
     * @Description 查询商品详情中销售属性信息，并标识当前页面是那种类型的销售属性
     * @Author wujijun
     * @Date 2022/1/5 20:01
     * @Param [spuId, skuId]
     * @Return java.util.List<java.util.Map>
     */
    @Override
    public List<SpuSaleAttr> findSaleAttrBySkuIdAndSpuId(Long spuId, Long skuId) {
        return spuSaleAttrMapper.findSaleAttrBySkuIdAndSpuId(spuId, skuId);
    }

    /**
     * @param spuId
     * @ClassName ItemService
     * @Description 根据spu查询sku的键值对
     * @Author wujijun
     * @Date 2022/1/5 20:27
     * @Param [spuId]
     * @Return Map
     */
    @Override
    public Map getSaleAttrValue(Long spuId) {
        //查询list中所有的键值对
        List<Map> maps = skuSaleAttrValueMapper.getSaleAttrValue(spuId);

        //创建返回结果的map
        Map hashMap = new ConcurrentHashMap();

        //因为流式编程是并发的，所以要用线程安全的map
        maps.stream().forEach(map -> {
            //获取sku的值
            Object skuId = map.get("sku_id");
            //获取values的值
            Object skuValues = map.get("sku_values");
            //把获取到的值加入新的map中
            hashMap.put(skuValues, skuId);
        });
        return hashMap;
    }

    /**
     * @ClassName ItemService
     * @Description 通过skuInfo查询缓存或者数据库
     * @Author wujijun
     * @Date 2022/1/7 19:38
     * @Param []
     * @Return com.atguigu.gmall.model.product.SkuInfo
     */
    @Override
    public SkuInfo findSkuInfoFromRedisOrDb(Long skuId) {
        //校验参数
        if (skuId == null) {
            throw new RuntimeException("参数错误！");
        }
        //从redis获取skuInfo的信息
        Object o = redisTemplate.opsForValue().get("sku:" + skuId + ":info");
        //判断
        if (o == null) {
            //若缓存没有数据，则加锁
            RLock lock = redissonClient.getLock("sku" + skuId + "lock");
            try {
                //尝试获取锁
                try {
                    if (lock.tryLock(100, 100, TimeUnit.SECONDS)) {
                        //获取到锁，查询数据库
                        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
                        //判断数据库中是否有该数据
                        if (skuInfo != null ) {
                            //数据库中有数据则存入缓存中
                            redisTemplate.opsForValue().set("sku:" + skuId + ":info", skuInfo, 24 * 60 * 60, TimeUnit.SECONDS);
                        } else {
                            //new一个空的skuInfo
                            skuInfo = new SkuInfo();
                            //将虚假的数据存入缓存中，防止缓存穿透
                            redisTemplate.opsForValue().set("sku:" + skuId + ":info", skuInfo, 5 * 60, TimeUnit.SECONDS);
                        }
                        //返回结果
                        return skuInfo;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    log.error("查询数据时出现了异常！");
                } finally {
                    //释放锁
                    lock.unlock();
                }
            }catch (Exception e){
                e.printStackTrace();
                log.error("获取锁时发生了异常！");
            }
        }
        //缓存中有数据，则直接返回
        return (SkuInfo) o;
    }

    /**
     * @ClassName ItemService
     * @Description 获取首页分类信息
     * @Author wujijun
     * @Date 2022/1/11 20:29
     * @Param []
     * @Return void
     * @return
     */
    @Override
    public List<JSONObject> getIndexCategory() {
        //查询所有的一二三级的分类信息
        List<BaseCategoryView> baseCategoryViews = baseCategoryViewMapper.selectList(null);
        //一级分类进行分桶
        Map<Long, List<BaseCategoryView>> categoryViewMap1 =
                baseCategoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        //把所有的一级分类进行封装
        List<JSONObject> category1JsonList = new ArrayList<>();
        //遍历一级分类，拿到所有的二级分类
       for (Map.Entry<Long, List<BaseCategoryView>> category1Entry : categoryViewMap1.entrySet()){
           //把一级分类下所有的数据进行封装
           JSONObject jsonObject1 = new JSONObject();
           //获取一级分类的id
           Long category1Id = category1Entry.getKey();
           //把一级分类的id封装
           jsonObject1.put("categoryId",category1Id);
           //一级分类下所有的二级分类和三级分类的信息
           List<BaseCategoryView> categoryViewList2 = category1Entry.getValue();
           //把二级分类的名字进行封装
           jsonObject1.put("categoryName",categoryViewList2.get(0).getCategory1Name());

           //因为二级分类也是有多个，所以需要用list去接收
           List<JSONObject> category2JsonList = new ArrayList<>();
           //二级分类进行分桶
           Map<Long, List<BaseCategoryView>> categoryViewMap2 =
                   categoryViewList2.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
           //遍历二级分类，拿到所有三级分类的信息
           for (Map.Entry<Long, List<BaseCategoryView>> category2Entry : categoryViewMap2.entrySet()) {
               //把二级分类也进行封装
               JSONObject jsonObject2 = new JSONObject();
               //拿到二级分类的id
               Long category2Id = category2Entry.getKey();
               //把二级分类的id设置进jsonObject
               jsonObject2.put("categoryId", category2Id);
               //获取二级分类所对应的三级分类信息
               List<BaseCategoryView> categoryViewList3 = category2Entry.getValue();
               //把二级分类的名字设置进jsonObject
               jsonObject2.put("categoryName", categoryViewList3.get(0).getCategory2Name());

               //从三级分类中拿数据，封装为一个list<jsonObject>-->categoryId和categoryName
               List<JSONObject> category3JsonList = categoryViewList3.stream().map(baseCategoryView -> {
                   //创建返回结果的数据类型
                   JSONObject jsonObject3 = new JSONObject();
                   //获取三级分类的id
                   Long category3Id = baseCategoryView.getCategory3Id();
                   //获取三级分类的名字
                   String category3Name = baseCategoryView.getCategory3Name();
                   //把获取的数据封装到返回的结果类型中
                   jsonObject3.put("categoryId", category3Id);
                   jsonObject3.put("categoryName", category3Name);
                   //返回结果
                   return jsonObject3;
               }).collect(Collectors.toList());
               //保存一个二级分类对应多个三级分类的数据
               jsonObject2.put("childCategoryJsonList",category3JsonList);
               //保存多个二级分类对应不同的三级分类的数据
               category2JsonList.add(jsonObject2);
           }
            //当前一级分类下所有的二级分类已经封装完成
           jsonObject1.put("childCategoryJsonList",category2JsonList);
           //保存一级分类下所有二级分类的数据
           category1JsonList.add(jsonObject1);
       }
       //返回封装好的一级分类
        return category1JsonList;
    }

    /**
     * @ClassName ItemService
     * @Description 根据品牌的ID查询品牌信息
     * @Author wujijun
     * @Date 2022/1/12 11:06
     * @Param []
     * @Return com.atguigu.gmall.model.product.BaseTrademark
     */
    @Override
    public BaseTrademark getBaseTrademark(Long id) {
        return baseTradeMarkMapper.selectById(id);
    }

    /**
     * @param skuId
     * @ClassName ItemService
     * @Description 查询商品的平台属性
     * @Author wujijun
     * @Date 2022/1/12 11:43
     * @Param [skuId]
     * @Return java.util.List<com.atguigu.gmall.model.product.BaseAttrInfo>
     */
    @Override
    public List<BaseAttrInfo> selectSkuInfoBySkuId(Long skuId) {
        return baseAttrInfoMapper.selectSkuInfoBySkuId(skuId);
    }

    /**
     * @param orderDetails
     * @ClassName ItemService
     * @Description 生成订单后删除库存
     * @Author wujijun
     * @Date 2022/1/18 23:59
     * @Param [orderDetails]
     * @Return java.lang.Boolean
     */
    @Override
    public Boolean delCountStock(Map<String, Object> orderDetails) {
        //检验参数
        if (orderDetails == null || orderDetails.isEmpty()) {
            return false;
        }
        //遍历orderDetails，根据新增的订单详情删减库存
        for (Map.Entry<String, Object> entry : orderDetails.entrySet()) {
            //获取商品的id
            Long skuId = Long.parseLong(entry.getKey());
            //获取商品的数量
            int skuNum = Integer.parseInt(entry.getValue().toString());
            //扣减库存
            int i = skuInfoMapper.delCountStock(skuId, skuNum);
            //判断是否扣减库存成功（有库存扣减失败或者没有库存扣减失败）
            if (i <= 0){
                throw new RuntimeException("扣减库存失败！");
            }

//            //查询商品的信息----存在时间差，会导致超卖
//            SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
//            //判断是否有该商品
//            if (skuInfo == null || skuInfo.getId() == null) {
//                return false;
//            }
//            //扣减库存
//            skuNum = skuInfo.getStock() - skuNum;
//            //判断库存
//            if (skuNum < 0) {
//                return false;
//            }
//            //把最新的库存设置进去
//            skuInfo.setStock(skuNum);
//            //更新库存
//            int i = skuInfoMapper.updateById(skuInfo);
//            //判断更新成功
//            if (i <= 0) {
//                return false;
//            }
        }
        //返回结果
        return null;
    }

    /**
     * @param orderDetails
     * @ClassName ItemService
     * @Description 取消订单，回滚库存
     * @Author wujijun
     * @Date 2022/1/20 23:50
     * @Param [orderDetails]
     * @Return java.lang.Boolean
     */
    @Override
    public Boolean rollBackStock(Map<String, Object> orderDetails) {
        //遍历需要回滚的商品
        for (Map.Entry<String, Object> entry : orderDetails.entrySet()) {
            //获取商品的id
            Long skuId = Long.parseLong(entry.getKey());
            //获取商品的数量
            int skuNum = Integer.parseInt(entry.getValue().toString());
            //回滚库存
            int i = skuInfoMapper.rollBackStock(skuId, skuNum);
            //判断是否回滚成功
            if (i <= 0) {
                throw new RuntimeException("库存回滚失败！");
            }
        }
        return true;
    }


}
