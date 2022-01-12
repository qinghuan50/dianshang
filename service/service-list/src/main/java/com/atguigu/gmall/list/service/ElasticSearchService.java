package com.atguigu.gmall.list.service;

import java.util.Map;

/**
 * @Author: wujijun
 * @Description: 首页搜索
 * @Date Created in 2022-01-12-18:48
 */
public interface ElasticSearchService {

    /**
     * @ClassName ElasticSearchService
     * @Description 从首页进商品详情页
     * @Author wujijun
     * @Date 2022/1/12 18:50
     * @Param [searchData]
     * @Return java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String, Object> search(Map<String, String> searchData);
}
