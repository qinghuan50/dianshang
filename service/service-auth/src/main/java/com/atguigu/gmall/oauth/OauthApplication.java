package com.atguigu.gmall.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

/**
 * 认证微服务
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.atguigu.gmall")
public class OauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthApplication.class, args);
    }

    /**
     * @ClassName OauthApplication
     * @Description 初始化RestTemplate对象
     * @Author wujijun
     * @Date 2022/1/16 22:20
     * @Param []
     * @Return org.springframework.web.client.RestTemplate
     */
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
