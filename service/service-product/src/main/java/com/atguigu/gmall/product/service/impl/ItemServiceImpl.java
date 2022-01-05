package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author: wujijun
 * @Description: 获取商品详情信息接口的实现类
 * @Date Created in 2022-01-05-15:41
 * @Modified By:
 */
@Service
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
                .eq(SkuImage::getSkuId,skuId));
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
        return spuSaleAttrMapper.findSaleAttrBySkuIdAndSpuId(spuId,skuId);
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
            hashMap.put(skuValues,skuId);
        });
        return hashMap;
    }


}
