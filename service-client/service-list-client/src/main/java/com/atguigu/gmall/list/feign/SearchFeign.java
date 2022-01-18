package com.atguigu.gmall.list.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @Author: wujijun
 * @Description: 商品搜索的feign接口
 * @Date Created in 2022-01-16-16:30
 */
@FeignClient(name = "service-list", path = "/api/search")
public interface SearchFeign {

    /**
     * @ClassName ElasticSearchController
     * @Description 通过首页查询商品
     * @Author wujijun
     * @Date 2022/1/12 19:45
     * @Param [searchData]
     * @Return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping
    Map<String, Object> resultMap(@RequestParam Map<String, String> searchData);
}
