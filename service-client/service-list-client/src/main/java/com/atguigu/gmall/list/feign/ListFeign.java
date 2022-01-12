package com.atguigu.gmall.list.feign;

import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: wujijun
 * @Description: 商品搜索feign接口的调用
 * @Date Created in 2022-01-12-12:23
 */
@FeignClient(name = "service-list", path = "/api/list")
public interface ListFeign {


    /**
     * @ClassName ListFeign
     * @Description 商品上架，把商品信息存入es中
     * @Author wujijun
     * @Date 2022/1/12 12:27
     * @Param [skuId]
     * @Return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/add/{skuId}")
    Result add(@PathVariable("skuId") Long skuId);

    /**
     * @ClassName ListFeign
     * @Description 商品下架，从es中把商品信息删除
     * @Author wujijun
     * @Date 2022/1/12 12:27
     * @Param [skuId]
     * @Return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/del/{skuId}")
    Result del(@PathVariable("skuId") Long skuId);
}
