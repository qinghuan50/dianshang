package com.atguigu.gmall.order.util;


/**
 * 本地线程类
 */
public class GmallThreadLocalUtils {

    //初始化一个静态常量的对象（任何地方都可以使用）
    private final static ThreadLocal<String> ThreadLocal = new ThreadLocal<>();

   /**
    * @ClassName GmallThreadLocalUtils
    * @Description 获取当前线程中的用户名
    * @Author wujijun
    * @Date 2022/1/18 0:23
    * @Param []
    * @Return java.lang.String
    */
    public static String getUserName( ){
        return ThreadLocal.get();
    }

   /**
    * @ClassName GmallThreadLocalUtils
    * @Description 设置当前线程的用户名
    * @Author wujijun
    * @Date 2022/1/18 0:22
    * @Param [username]
    * @Return void
    */
    public static void setUserName(String username){
        ThreadLocal.set(username);
    }

}