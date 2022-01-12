package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.model.list.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: wujijun
 * @Description: 创建搜索微服务的表述层
 * @Date Created in 2022-01-11-22:53
 */
@RestController
@RequestMapping("/api/list")
public class ListController {

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    ListService listService;

    /**
     * @ClassName ListController
     * @Description 创建索引（数据库）或者类型（表）
     * @Author wujijun
     * @Date 2022/1/11 22:55
     * @Param []
     * @Return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/createIndexAndTypes")
    public Result createIndexAndTypes(){
        //创建索引
        elasticsearchRestTemplate.createIndex(Goods.class);
        //创建映射
        elasticsearchRestTemplate.putMapping(Goods.class);
        //返回结果
        return Result.ok();
    }

    /**
     * @ClassName ListController
     * @Description 商品上架，把商品信息存入es中
     * @Author wujijun
     * @Date 2022/1/12 12:02
     * @Param [skuId]
     * @Return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/add/{skuId}")
    public Result add(@PathVariable("skuId") Long skuId){
        listService.addSkuEs(skuId);
        return Result.ok();
    }

    /**
     * @ClassName ListController
     * @Description 商品下架，从es中把商品信息删除
     * @Author wujijun
     * @Date 2022/1/12 12:03
     * @Param [skuId]
     * @Return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/del/{skuId}")
    public Result del(@PathVariable("skuId") Long skuId){
        listService.delSkuEs(skuId);
        return Result.ok();
    }

    /**
     * @ClassName ListController
     * @Description 更新商品的热度值
     * @Author wujijun
     * @Date 2022/1/12 17:46
     * @Param [skuId]
     * @Return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/addHotScore/{skuId}")
    public Result addHotScore(@PathVariable("skuId") Long skuId){
        listService.addHotScore(skuId);
        return Result.ok();
    }
}
