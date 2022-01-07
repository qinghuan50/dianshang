package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author: wujijun
 * @Description: 获取商品详情信息接口
 * @Date Created in 2022-01-05-15:39
 * @Modified By:
 */
public interface ItemService {

    /**
     * @ClassName ItemService
     * @Description 查询商品名字和默认图片；
     * @Author wujijun
     * @Date 2022/1/5 15:40
     * @Param [skuId]
     * @Return com.atguigu.gmall.model.product.SkuInfo
     */
    SkuInfo getSkuInfo(Long skuId);

    /**
     * @ClassName ItemService
     * @Description 查询商品的所有的图片
     * @Author wujijun
     * @Date 2022/1/5 17:03
     * @Param [skuId]
     * @Return java.util.List<com.atguigu.gmall.model.product.SkuImage>
     */
    List<SkuImage> getSkuImages(Long skuId);

    /**
     * @ClassName ItemService
     * @Description 查询商品价格
     * @Author wujijun
     * @Date 2022/1/5 18:14
     * @Param [skuId]
     * @Return java.math.BigDecimal
     */
    BigDecimal getPrice(Long skuId);

    /**
     * @ClassName ItemService
     * @Description 查询一二三级分类的信息
     * @Author wujijun
     * @Date 2022/1/5 18:26
     * @Param [skuId]
     * @Return com.atguigu.gmall.model.product.BaseCategoryView
     */
    BaseCategoryView getBaseCategory(Long c3Id);

    /**
     * @ClassName ItemService
     * @Description 查询商品详情中销售属性信息，并标识当前页面是那种类型的销售属性
     * @Author wujijun
     * @Date 2022/1/5 20:01
     * @Param [spuId, skuId]
     * @Return java.util.List<java.util.Map>
     */
    List<SpuSaleAttr> findSaleAttrBySkuIdAndSpuId(Long spuId, Long skuId);

    /**
     * @ClassName ItemService
     * @Description 根据spu查询sku的键值对
     * @Author wujijun
     * @Date 2022/1/5 20:27
     * @Param [spuId]
     * @Return Map
     */
    Map getSaleAttrValue(Long spuId);

    /**
     * @ClassName ItemService
     * @Description 通过skuInfo查询缓存或者数据库
     * @Author wujijun
     * @Date 2022/1/7 19:38
     * @Param []
     * @Return com.atguigu.gmall.model.product.SkuInfo
     */
    SkuInfo findSkuInfoFromRedisOrDb(Long skuId);




}
