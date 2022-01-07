package com.atguigu.gmall.common.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class GmallCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;


    /**
     * @ClassName GmallCacheAspect
     * @Description  AOP切面编程-注解注入 开启动态代理,调用 point.proceed()方法进行增强
     * @Author wujijun
     * @Date 2022/1/7 20:54
     * @Param [point] 切点
     * @Return java.lang.Object
     */
    @Around("@annotation(com.atguigu.gmall.common.cache.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point){
        //初始化返回结果
        Object result = null;
        try {
            //从切点上获取方法的参数
            Object[] args = point.getArgs();
            //获取方法的签名信息
            MethodSignature signature = (MethodSignature) point.getSignature();
            //通过方法的签名信息获取方法上的注解
            //Method类的java.lang.reflect.Method.getAnnotation(Class 注记类)
            // 方法返回指定类型的方法对象的注解(如果存在)，则将其作为参数传递给参数，否则为null
            GmallCache gmallCache = signature.getMethod().getAnnotation(GmallCache.class);
            // 拿到前缀 ；方法名加参数
            String prefix = gmallCache.prefix();
            //拼接key
            String key = prefix+Arrays.asList(args).toString();

            // 获取缓存数据
            result = cacheHit(signature, key);
            if (result != null){
                // 缓存有数据
                return result;
            }
            // 初始化分布式锁
            RLock lock = redissonClient.getLock(key + ":lock");
            boolean flag = lock.tryLock(100, 100, TimeUnit.SECONDS);
            if (flag){
               try {
                   try {
                       //执行方法（查询数据库返回结果）
                       result = point.proceed(point.getArgs());
                       // 判断数据库中是否有该数据
                       if (null==result){
                           //数据库没有该数据，创建一个空的数据，防止缓存穿透

                           //通过方法签名获取返回类型
                           Class returnType = signature.getReturnType();
                           //通过反射获取被代理类的类型
                           Object o = returnType.newInstance();
                           // 把结果放入缓存，并设置过期时间
                           this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(o), 5*60,TimeUnit.SECONDS);
                           return o;
                       }
                   } catch (Throwable throwable) {
                       throwable.printStackTrace();
                   }
                   // 数据库中有该数据，把查询到的结果放入缓存
                   this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(result), 24*60*60, TimeUnit.SECONDS);
                   //返回查询到的结果
                   return result;
               }catch (Exception e){
                   e.printStackTrace();
               }finally {
                   // 释放锁
                   lock.unlock();
               }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //boolean flag = lock.tryLock(10L, 10L, TimeUnit.SECONDS);
        return result;
    }
    /**
     * @ClassName GmallCacheAspect
     * @Description 从缓存中获取数据
     * @Author wujijun
     * @Date 2022/1/7 21:02
     * @Param [signature, key]
     * @Return java.lang.Object
     */
    private Object cacheHit(MethodSignature signature, String key) {
        // 1. 查询缓存
        String cache = (String)redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(cache)) {
            // 有，则反序列化，直接返回
            Class returnType = signature.getReturnType(); // 获取方法返回类型
            // 不能使用parseArray<cache, T>，因为不知道List<T>中的泛型
            return JSONObject.parseObject(cache, returnType);
        }
        //没有缓存则返回空值
        return null;
    }

}
