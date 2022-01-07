package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.RedisDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: wujijun
 * @Description: 测试redis
 * @Date Created in 2022-01-07-16:19
 */
@RestController
@RequestMapping("/api/product")
public class RedisDemoController {

    @Autowired
    RedisDemoService redisDemoService;

    @GetMapping("/redis")
    public Result getRedis(){
        redisDemoService.TestRedisson();
        return Result.ok();
    }
}

