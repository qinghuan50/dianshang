package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author: wujijun
 * @Description: 获取商品详情信息接口的实现类
 * @Date Created in 2022-01-05-15:41
 * @Modified By:
 */
@Service
@Log4j2
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
                        if (skuInfo == null || skuInfo.getId() == null) {
                            //new一个空的skuInfo
                            skuInfo = new SkuInfo();
                            //将虚假的数据存入缓存中，防止缓存穿透
                            redisTemplate.opsForValue().set("sku:" + skuId + ":info", skuInfo, 5 * 60, TimeUnit.SECONDS);
                        } else {
                            //数据库中有数据则存入缓存中
                            redisTemplate.opsForValue().set("sku:" + skuId + ":info", skuInfo, 24 * 60 * 60, TimeUnit.SECONDS);
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

}
