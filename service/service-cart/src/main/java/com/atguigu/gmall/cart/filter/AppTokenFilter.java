package com.atguigu.gmall.cart.filter;

import com.atguigu.gmall.cart.util.GmallThreadLocalUtils;
import com.atguigu.gmall.cart.util.TokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @ClassName AppTokenFilter
 * @Description 过滤器，用来获取令牌中的用户的名字
 * @Author wujijun
 * @Date 2022/1/17 23:55
 * @Param
 * @Return
 */
@Order(1)
@WebFilter(filterName = "appTokenFilter", urlPatterns = "/*")
public class AppTokenFilter extends GenericFilterBean {

    /**
     * @ClassName AppTokenFilter
     * @Description 购物车的过滤器
     * @Author wujijun
     * @Date 2022/1/17 23:56
     * @Param [servletRequest, servletResponse, filterChain]
     * @Return void
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //更改请求头的类型
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        //从请求头中获取令牌
        String token = request.getHeader("Authorization").replace("bearer ", "");
        //从令牌中取出用户的名字
        Map<String, String> map = TokenUtil.dcodeToken(token);
        //判断stringMap是否有数据
        if (map != null && !map.isEmpty()) {
            //从载荷中获取用户名
            String username = map.get("username");
            if (!StringUtils.isEmpty(username)) {
                //从载荷中获取到了用户名，线程本地化保存用户名
                GmallThreadLocalUtils.setUserName(username);
            }
        }
        //放行
        filterChain.doFilter(servletRequest, servletResponse);
    }
}