package com.atguigu.gmall.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: wujijun
 * @Description: web的启动类
 * @Date Created in 2022-01-11-17:00
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("com.atguiug.gmall.item.feign")
@ComponentScan("com.atguigu.gmall")
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class,args);
    }
}
