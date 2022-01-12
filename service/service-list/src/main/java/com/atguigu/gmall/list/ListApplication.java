package com.atguigu.gmall.list;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @Author: wujijun
 * @Description: es搜索的启动类
 * @Date Created in 2022-01-11-22:48
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@ComponentScan("com.atguigu.gmall")
@EnableFeignClients("com.atguigu.gmall.product.feign")
@EnableElasticsearchRepositories("com.atguigu.gmall.list.dao")
public class ListApplication {

    public static void main(String[] args) {
        SpringApplication.run(ListApplication.class,args);
    }
}
