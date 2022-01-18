package com.atguigu.gmall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: wujijun
 * @Description: 购物车的启动类
 * @Date Created in 2022-01-17-20:55
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.atguigu.gmall")
@EnableFeignClients("com.atguigu.gmall.product.feign")
@ServletComponentScan("com.atguigu.gmall.cart.filter")
public class CartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class,args);
    }
}
