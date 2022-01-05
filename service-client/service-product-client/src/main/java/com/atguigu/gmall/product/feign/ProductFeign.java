package com.atguigu.gmall.product.feign;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author: wujijun
 * @Description: 调用service-product中的接口
 * @Date Created in 2022-01-05-16:08
 * @Modified By:
 */
@FeignClient(name = "service-product",path = "/api/item")
public interface ProductFeign {

    /**
     * @ClassName ProductFeign
     * @Description 通过feign调用，查询商品名字和默认图片
     * @Author wujijun
     * @Date 2022/1/5 16:10
     * @Param [skuId]
     * @Return com.atguigu.gmall.model.product.SkuInfo
     */
    @GetMapping("/getSkuInfo/{skuId}")
    SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * @ClassName ProductFeign
     * @Description 查询商品的所有的图片
     * @Author wujijun
     * @Date 2022/1/5 18:07
     * @Param [skuId]
     * @Return java.util.List<com.atguigu.gmall.model.product.SkuImage>
     */
    @GetMapping("/getSkuImages/{skuId}")
    List<SkuImage> getSkuImages(@PathVariable("skuId") Long skuId);

    /**
     * @ClassName ProductFeign
     * @Description 查询商品价格
     * @Author wujijun
     * @Date 2022/1/5 18:49
     * @Param [skuId]
     * @Return java.math.BigDecimal
     */
    @GetMapping("/getPrice/{skuId}")
    BigDecimal getPrice(@PathVariable("skuId") Long skuId);

    /**
     * @ClassName ProductFeign
     * @Description 查询商品一二三级分类
     * @Author wujijun
     * @Date 2022/1/5 18:49
     * @Param [c3Id]
     * @Return com.atguigu.gmall.model.product.BaseCategoryView
     */
    @GetMapping("/getBaseCategory/{c3Id}")
    BaseCategoryView getBaseCategory(@PathVariable("c3Id") Long c3Id);

    /**
     * @ClassName ProductFeign
     * @Description 查询商品详情中销售属性信息，并标识当前页面是那种类型的销售属性
     * @Author wujijun
     * @Date 2022/1/5 20:14
     * @Param [spuId, skuId]
     * @Return java.util.List<com.atguigu.gmall.model.product.SpuSaleAttr>
     */
    @GetMapping("/findSaleAttrBySkuIdAndSpuId/{spuId}/{skuId}")
    List<SpuSaleAttr> findSaleAttrBySkuIdAndSpuId(@PathVariable("spuId") Long spuId,
                                                         @PathVariable("skuId") Long skuId);

    /**
     * @ClassName ProductFeign
     * @Description 根据spu查询sku的键值对
     * @Author wujijun
     * @Date 2022/1/5 20:48
     * @Param [spuId]
     * @Return java.util.Map
     */
    @GetMapping("/getSaleAttrValue/{spuId}")
    public Map getSaleAttrValue(@PathVariable("spuId") Long spuId);
}
