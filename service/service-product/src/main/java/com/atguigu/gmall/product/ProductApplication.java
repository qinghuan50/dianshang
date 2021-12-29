package com.atguigu.gmall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * 管理微服务的启动类
 * @SpringBootApplication：
 *  1、扫描配置类文件
 *  2、包扫描（扫描启动类的包以及子包）
 * @EnableDiscoveryClient：自动配置/自动装备
 *  1、初始化
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.atguigu.gmall")
public class ProductApplication {

    /**
     *
     * @param args：启动微服务的时候传递jvm的参数
     */
    public static void main(String[] args) {
        /**
         * run方法的作用：创建ioc容器
         */
        SpringApplication.run(ProductApplication.class,args);
    }
}
