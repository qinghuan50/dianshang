package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.gateway.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * @Author: wujijun
 * @Description: 自定义的网关的过滤器
 * @Date Created in 2022-01-17-0:17
 */
@Component
public class GmallFilter implements GlobalFilter, Ordered {

   @Autowired
   private StringRedisTemplate stringRedisTemplate;

    /**
     * @ClassName GmallFilter
     * @Description 自己定义的过滤方法
     * @Author wujijun
     * @Date 2022/1/17 0:19
     * @Param [exchange, chain]
     * @Return reactor.core.publisher.Mono<java.lang.Void>
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取request
        ServerHttpRequest request = exchange.getRequest();
        //获取response
        ServerHttpResponse response = exchange.getResponse();
        //url中获取令牌
        String token = request.getQueryParams().getFirst("token");
        //判断url中是否包含令牌
        if (StringUtils.isEmpty(token)) {
            //url中没有令牌，则从请求头中查询
            token = request.getHeaders().getFirst("token");
            //继续判断，看请求头中是否含有令牌
            if (StringUtils.isEmpty(token)) {
                //请求头中没有，则从cookie中获取令牌
                MultiValueMap<String, HttpCookie> cookies = request.getCookies();
                //判断是否有cookie
                if (cookies != null || !cookies.isEmpty()) {
                    //cookie不为空再从中获取token令牌
                    HttpCookie httpCookie = cookies.getFirst("token");
                    token = httpCookie.getValue();
                }
            }
        }
        //如果全部都查完，都没有cookie，则反馈一个状态给用户
        if (StringUtils.isEmpty(token)) {
            //给用户返回一个状态码
            response.setStatusCode(HttpStatus.FOUND);
            //引导用户去登录界面
//            request.getRequestDispatcher("/login.html").forward(request,response);
            return response.setComplete();
        }
        //获取本次登录的ip地址
        String gatwayIpAddress = IpUtil.getGatwayIpAddress(request);
        //通过网关的ip在缓存中查询token
        String redisToken = stringRedisTemplate.opsForValue().get(gatwayIpAddress);
        //防止缓存中没有值，报空指针
        if (StringUtils.isEmpty(redisToken)) {
            //用户第一次使用该ip地址登录
            //返回客户端一个状态码
            response.setStatusCode(HttpStatus.FOUND);
            //直接拒绝访问，或者重定向到登录页面
            return response.setComplete();
        }
        //防止token被盗用,判断缓存中存入的令牌和登录的令牌是否一致
        if (!token.equals(redisToken)) {
            //用户修改了令牌
            //返回客户端一个状态码
            response.setStatusCode(HttpStatus.FOUND);
            //直接拒绝访问，或者重定向到登录页面
            return response.setComplete();
        }
        //把token存入请求头中
        request.mutate().header("Authorization", "bearer " + token);
        //如果有令牌，则放行
        return chain.filter(exchange);
    }

    /**
     * @ClassName GmallFilter
     * @Description 过滤器的执行顺序（优先级）
     * @Author wujijun
     * @Date 2022/1/17 0:18
     * @Param []
     * @Return int
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
