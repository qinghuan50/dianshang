package com.atguigu.gmall.oauth.service.impl;

import com.atguigu.gmall.oauth.service.LoginService;
import com.atguigu.gmall.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

/**
 * @Author: wujijun
 * @Description: 用户登录的实现类
 * @Date Created in 2022-01-16-22:14
 */
@Service
public class LoginServiceImpl implements LoginService {

    //发送rest的请求
    @Autowired
    RestTemplate restTemplate;

    @Value("${auth.clientId}")
    private String clientId;

    @Value("${auth.clientSecret}")
    private String clientSecret;

    @Autowired
    LoadBalancerClient loadBalancerClient;

    /**
     * @param username
     * @param password
     * @ClassName LoginService
     * @Description 用户登录
     * @Author wujijun
     * @Date 2022/1/16 22:13
     * @Param [username, password]
     * @Return void
     * @return
     */
    @Override
    public AuthToken login(String username, String password) {
        //判断用户的用户名和密码是否为空
        if (StringUtils.isEmpty(username) && StringUtils.isEmpty(password)) {
            throw new RuntimeException("用户名和密码不能为空！");
        }
        //动态获取登录的地址(通过负载均衡的方式获取服务的实例)
        ServiceInstance choose = loadBalancerClient.choose("service-oauth");
        //获取服务的地址
        String url = choose.getUri().toString() + "/oauth/token";
        //获取登录的地址
//        String url = "http://localhost:9001/oauth/token";

        //声明一个请求体body
        MultiValueMap<String, String> body = new HttpHeaders();
        //设置校验的类型
        body.add("grant_type", "password");
        //设置用户名
        body.add("username", username);
        //设置用户密码
        body.add("password", password);

        //设置请求头
        MultiValueMap<String, String> headers = new HttpHeaders();
        //设置请求头参数
        headers.add("Authorization",getHeadParm());

        //初始化请求参数
        HttpEntity httpEntity = new HttpEntity(body, headers);

        //发送请求;1、请求地址，2、请求方式，3、请求参数，4、返回的类型
        ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        //获取结果
        Map<String, String> resultMap = exchange.getBody();
        //初始化返回的对象
        AuthToken authToken = new AuthToken();
        //获取令牌
        String accessToken = resultMap.get("access_token");
        //把获取的结果存入返回的对象中
        authToken.setAccessToken(accessToken);
        //获取刷新令牌
        String refreshToken = resultMap.get("refresh_token");
        //把获取的结果存入返回的对象中
        authToken.setRefreshToken(refreshToken);
        //获取唯一标识
        String jti = resultMap.get("jti");
        //把获取的结果存入返回的对象中
        authToken.setJti(jti);
        //返回结果的对象
        return authToken;
    }

    /**
     * @ClassName LoginServiceImpl
     * @Description 获取加密后的客户端登录名和密码
     * @Author wujijun
     * @Date 2022/1/16 22:35
     * @Param []
     * @Return java.lang.String
     */
    private String getHeadParm() {
        //加密后返回
        byte[] bytes = Base64.getEncoder().encode((clientId + ":" + clientSecret).getBytes());
        //返回加密结果
        return "Basic " + new String(bytes);
    }
}
