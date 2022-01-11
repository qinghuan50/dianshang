package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: wujijun
 * @Description:
 * @Date Created in 2022-01-05-16:19
 * @Modified By:
 */
@RestController
@RequestMapping("/api/item")
public class ItemController {

    @Autowired
    ItemService itemService;

    /**
     * @ClassName ItemController
     * @Description 接收前端请求，返回前端结果
     * @Author wujijun
     * @Date 2022/1/5 16:27
     * @Param [skuId]
     * @Return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping("/getSkuItem/{skuId}")
    public Map<String, Object> getSkuItem(@PathVariable("skuId") Long skuId) {

        return itemService.getSkuItem(skuId);
    }

}



