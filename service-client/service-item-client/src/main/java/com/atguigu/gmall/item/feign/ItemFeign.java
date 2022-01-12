package com.atguigu.gmall.item.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @Author: wujijun
 * @Description: 通过feign远程调用item查询数据
 * @Date Created in 2022-01-11-17:17
 */
@FeignClient(name = "service-item", path = "/api/item")
public interface ItemFeign {

    /**
     * @ClassName ItemFeign
     * @Description 获取商品详情页所有的数据
     * @Author wujijun
     * @Date 2022/1/11 17:20
     * @Param [skuId]
     * @Return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping("/getSkuItem/{skuId}")
    Map<String, Object> getSkuItem(@PathVariable("skuId") Long skuId);
}
