package com.atguigu.gmall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: wujijun
 * @Description: 用户服务的启动类
 * @Date Created in 2022-01-16-23:41
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.atguigu.gmall")
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class,args);
    }
}
