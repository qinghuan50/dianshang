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

        //查询商品名字和默认图片；
        SkuInfo skuInfo = productFeign.getSkuInfo(skuId);

        //判断skuInfo
        if (skuInfo == null || skuInfo.getId() == null) {
            throw new RuntimeException("该商品不存在！");
        }
        //查询到的数据封装到map
        map.put("skuInfo",skuInfo);

        //查询图片列表；
        List<SkuImage> skuImageList = productFeign.getSkuImages(skuId);

        //查询到的数据封装到map
        map.put("skuImageList",skuImageList);

        //查询商品价格；虽然skuInfo表中已经查出，但实际业务中，价格会保存在另外一张表中
        BigDecimal price = productFeign.getPrice(skuId);

        //查询到的数据封装到map
        map.put("price",price);

        //查询商品三分类；
        BaseCategoryView baseCategory =
                productFeign.getBaseCategory(skuInfo.getCategory3Id());

        //查询到的数据封装到map
        map.put("baseCategory",baseCategory);

        //查询销售具体详情
        List<SpuSaleAttr> saleAttrList =
                productFeign.findSaleAttrBySkuIdAndSpuId(skuInfo.getSpuId(), skuId);

        //查询到的数据封装到map
        map.put("saleAttrList",saleAttrList);

        //生成前端用于跳转的键值对
        Map saleAttrValue = productFeign.getSaleAttrValue(skuInfo.getSpuId());

        //查询到的数据封装到map
        map.put("saleAttrValue",saleAttrValue);

        //返回结果
        return map;
    }
}
