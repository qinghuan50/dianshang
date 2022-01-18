package com.atguigu.gmall.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author: wujijun
 * @Description: 商品详情页的接口，feign调用
 * @Date Created in 2022-01-05-15:43
 * @Modified By:
 */
@RestController
@RequestMapping("/api/item")
public class ItemController {

    @Autowired
    ItemService itemService;

    /**
     * @ClassName ItemController
     * @Description 查询商品名字和默认图片
     * @Author wujijun
     * @Date 2022/1/5 15:47
     * @Param [skuId]
     * @Return com.atguigu.gmall.model.product.SkuInfo
     */
    @GmallCache(prefix = "getSkuInfo:")
    @GetMapping("/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId){
        return itemService.getSkuInfo(skuId);
    }

    /**
     * @ClassName ItemController
     * @Description 查询商品的所有的图片
     * @Author wujijun
     * @Date 2022/1/5 18:06
     * @Param [skuId]
     * @Return java.util.List<com.atguigu.gmall.model.product.SkuImage>
     */
    @GmallCache(prefix = "getSkuImages:")
    @GetMapping("/getSkuImages/{skuId}")
    public List<SkuImage> getSkuImages(@PathVariable("skuId") Long skuId){
        return itemService.getSkuImages(skuId);
    }

    /**
     * @ClassName ItemController
     * @Description 查询商品价格
     * @Author wujijun
     * @Date 2022/1/5 18:46
     * @Param [skuId]
     * @Return java.math.BigDecimal
     */
    @GmallCache(prefix = "getPrice:")
    @GetMapping("/getPrice/{skuId}")
    public BigDecimal getPrice(@PathVariable("skuId") Long skuId) {
        return itemService.getPrice(skuId);
    }

    /**
     * @ClassName ItemController
     * @Description 查询商品的一二三级分类
     * @Author wujijun
     * @Date 2022/1/5 18:48
     * @Param [c3Id]
     * @Return com.atguigu.gmall.model.product.BaseCategoryView
     */
    @GmallCache(prefix = "getBaseCategory:")
    @GetMapping("/getBaseCategory/{c3Id}")
    public BaseCategoryView getBaseCategory(@PathVariable("c3Id") Long c3Id){
        return itemService.getBaseCategory(c3Id);
    }

    /**
     * @ClassName ItemController
     * @Description 查询商品详情中销售属性信息，并标识当前页面是那种类型的销售属性
     * @Author wujijun
     * @Date 2022/1/5 20:13
     * @Param [spuId, skuId]
     * @Return java.util.List<com.atguigu.gmall.model.product.SpuSaleAttr>
     */
    @GmallCache(prefix = "findSaleAttrBySkuIdAndSpuId:")
    @GetMapping("/findSaleAttrBySkuIdAndSpuId/{spuId}/{skuId}")
    public List<SpuSaleAttr> findSaleAttrBySkuIdAndSpuId(@PathVariable("spuId") Long spuId,
                                                         @PathVariable("skuId") Long skuId){
        return itemService.findSaleAttrBySkuIdAndSpuId(spuId,skuId);
    }

    /**
     * @ClassName ItemController
     * @Description 根据spu查询sku的键值对
     * @Author wujijun
     * @Date 2022/1/5 20:47
     * @Param [spuId]
     * @Return java.util.Map
     */
    @GmallCache(prefix = "getSaleAttrValue:")
    @GetMapping("/getSaleAttrValue/{spuId}")
    public Map getSaleAttrValue(@PathVariable("spuId") Long spuId){
        return itemService.getSaleAttrValue(spuId);
    }

    /**
     * @ClassName ItemController
     * @Description 查询首页的所有分类信息
     * @Author wujijun
     * @Date 2022/1/11 22:21
     * @Param []
     * @Return com.alibaba.fastjson.JSONObject
     */
    @GetMapping("/getIndexCategory")
    @GmallCache(prefix = "getIndexCategory:")
    public Result getIndexCategory(){
        return Result.ok(itemService.getIndexCategory());
    }

  /**
   * @ClassName ItemController
   * @Description 根据品牌的ID查询品牌信息
   * @Author wujijun
   * @Date 2022/1/12 11:13
   * @Param [id]
   * @Return com.atguigu.gmall.model.product.BaseTrademark
   */
    @GetMapping("/getBaseTrademark/{id}")
    public BaseTrademark getBaseTrademark(@PathVariable("id") Long id){
        return itemService.getBaseTrademark(id);
    }

    /**
     * @ClassName ItemController
     * @Description 查询商品的平台属性
     * @Author wujijun
     * @Date 2022/1/12 11:47
     * @Param [skuId]
     * @Return com.atguigu.gmall.model.product.BaseAttrInfo
     */
    @GetMapping("/selectSkuInfoBySkuId/{skuId}")
    public List<BaseAttrInfo> selectSkuInfoBySkuId(@PathVariable("skuId")Long skuId){
        return itemService.selectSkuInfoBySkuId(skuId);
    }

    /**
     * @ClassName ItemController
     * @Description 生成订单后删除订单中的商品库存
     * @Author wujijun
     * @Date 2022/1/19 0:30
     * @Param [orderDetails]
     * @Return java.lang.Boolean
     */
    @GetMapping("/delCountStock")
    public Boolean delCountStock(@RequestParam Map<String, Object> orderDetails){
        return itemService.delCountStock(orderDetails);
    }


}
