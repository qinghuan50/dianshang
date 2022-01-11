package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.feign.ProductFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author: wujijun
 * @Description: 获取商品详情信息的接口，远程调用feign
 * @Date Created in 2022-01-05-15:29
 * @Modified By:
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ProductFeign productFeign;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    /**
     * @param skuId
     * @ClassName itemService
     * @Description 获取商品详情信息接口的实现类
     * @Author wujijun
     * @Date 2022/1/5 15:28
     * @Param [skuId]
     * @Return java.util.Map<java.lang.String, java.lang.Object>
     */
    @Override
    public Map<String, Object> getSkuItem(Long skuId) {

        //校验参数
        if (skuId == null) {
            throw new RuntimeException("参数错误！");
        }

        //创建一个返回的map
        Map<String, Object> map = new HashMap<>();

        //优化：将之前的串行化变成并行化，全都由子线程运行，不占用main主线程
        CompletableFuture<SkuInfo> future1 = CompletableFuture.supplyAsync(() -> {
            //查询商品名字和默认图片；
            SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
            //返回任务结果
            return skuInfo;
        },threadPoolExecutor);
        try {
            //拿到任务一执行后的结果
            SkuInfo skuInfo = future1.get();

            //判断skuInfo
            if (skuInfo == null || skuInfo.getId() == null) {
                throw new RuntimeException("该商品不存在！");
            }
            //查询到的数据封装到map
            map.put("skuInfo", skuInfo);

            CompletableFuture<Void> future2 = future1.thenRunAsync(() -> {
                //查询图片列表；
                List<SkuImage> skuImageList = productFeign.getSkuImages(skuId);
                //查询到的数据封装到map
                map.put("skuImageList", skuImageList);
            },threadPoolExecutor);

            CompletableFuture<Void> future3 = future1.thenRunAsync(() -> {
                //查询商品价格；虽然skuInfo表中已经查出，但实际业务中，价格会保存在另外一张表中
                BigDecimal price = productFeign.getPrice(skuId);
                //查询到的数据封装到map
                map.put("price", price);
            },threadPoolExecutor);

            CompletableFuture<Void> future4 = future1.thenRunAsync(() -> {
                //查询商品三分类；
                BaseCategoryView baseCategory =
                        productFeign.getBaseCategory(skuInfo.getCategory3Id());
                //查询到的数据封装到map
                map.put("baseCategory", baseCategory);
            },threadPoolExecutor);

            CompletableFuture<Void> future5 = future1.thenRunAsync(() -> {
                //查询销售具体详情
                List<SpuSaleAttr> saleAttrList =
                        productFeign.findSaleAttrBySkuIdAndSpuId(skuInfo.getSpuId(), skuId);
                //查询到的数据封装到map
                map.put("saleAttrList", saleAttrList);
            },threadPoolExecutor);

            CompletableFuture<Void> future6 = future1.thenRunAsync(() -> {
                //生成前端用于跳转的键值对
                Map saleAttrValue = productFeign.getSaleAttrValue(skuInfo.getSpuId());
                //查询到的数据封装到map
                map.put("saleAttrValue", saleAttrValue);
            },threadPoolExecutor);
            //阻塞线程，等所有的子线程完成任务后再执行
            CompletableFuture.allOf(future2, future3, future4, future5, future6).join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回结果
        return map;
    }
}
