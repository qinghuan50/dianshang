package com.atguigu.gmall.order.intercepter;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @Author: wujijun
 * @Description: 自定义拦截器
 * @Date Created in 2022-01-18-22:48
 */
@Component
public class OrederIntercepter implements RequestInterceptor {

    /**
     * @ClassName OrederIntercepter
     * @Description feign调用都要经过这
     * @Author wujijun
     * @Date 2022/1/18 22:50
     * @Param [requestTemplate]
     * @Return void
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //从主线程中获取请求体对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        //判断是否有请求体
        if (requestAttributes != null) {
            //从请求体偶去request
            HttpServletRequest request = requestAttributes.getRequest();
            //从request中获取所有的请求头信息
            Enumeration<String> headerNames = request.getHeaderNames();
            //遍历请求头
            while (headerNames.hasMoreElements()) {
                //获取每一个请求头的名字
                String key = headerNames.nextElement();
                //根据请求头的名字获取参数值
                String value = request.getHeader(key);
                //把令牌存入feign调用的请求头中
                requestTemplate.header(key, value);
            }
        }
    }
}
