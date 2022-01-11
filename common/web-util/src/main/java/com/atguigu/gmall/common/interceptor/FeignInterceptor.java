package com.atguigu.gmall.common.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName FeignInterceptor
 * @Description 全局拦截器
 * @Author wujijun
 * @Date 2022/1/11 16:50
 * @Param
 * @Return
 */

@Component
public class FeignInterceptor implements RequestInterceptor {

    public void apply(RequestTemplate requestTemplate){
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

//            System.out.println(request.getHeader("userTempId"));
//            System.out.println(request.getHeader("userId"));
            requestTemplate.header("userTempId", request.getHeader("userTempId"));
            requestTemplate.header("userId", request.getHeader("userId"));

            //
    }

}
