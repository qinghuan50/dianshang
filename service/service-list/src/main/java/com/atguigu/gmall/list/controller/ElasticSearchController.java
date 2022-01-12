package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.list.service.ElasticSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: wujijun
 * @Description: 首页搜索
 * @Date Created in 2022-01-12-19:42
 */
@RestController
@RequestMapping("/api/search")
public class ElasticSearchController {

    @Autowired
    ElasticSearchService elasticSearchService;

    /**
     * @ClassName ElasticSearchController
     * @Description 通过首页查询商品
     * @Author wujijun
     * @Date 2022/1/12 19:45
     * @Param [searchData]
     * @Return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping
    public Map<String, Object> resultMap(@RequestParam Map<String, String> searchData){
        return elasticSearchService.search(searchData);
    }

}
