package com.atguigu.gmall.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: wujijun
 * @Description: 商品详情的启动类
 * @Date Created in 2022-01-05-15:22
 * @Modified By:
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableFeignClients("com.atguigu.gmall.product.feign")
@EnableDiscoveryClient
@ComponentScan("com.atguigu.gmall")
public class ItemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemApplication.class, args);
    }

}
