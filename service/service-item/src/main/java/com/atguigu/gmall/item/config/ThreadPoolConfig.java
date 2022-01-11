package com.atguigu.gmall.item.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ThreadPoolConfig
 * @Description 自定义的线程池的配置类
 * @Author wujijun
 * @Date 2022/1/11 9:34
 * @Param
 * @Return
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * @ClassName ThreadPoolConfig
     * @Description 自定义的线程池
     * @Author wujijun
     * @Date 2022/1/11 9:34
     * @Param []
     * @Return java.util.concurrent.ThreadPoolExecutor
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        /**
         * 核心线程数：不会被回收
         * 拥有最多线程数(最大线程数):核心线程数 + 非核心线程数 = 最大线程数
         * 表示空闲线程的存活时间：非核心线程
         * 存活时间单位
         * 用于缓存任务的阻塞队列
         * 省略：
         *  threadFactory：指定创建线程的工厂
         *  handler：表示当workQueue已满，且池中的线程数达到maximumPoolSize时，线程池拒绝添加新任务时采取的策略。
         *  四种拒绝策略：
         *      1、默认策略：不执行任务，直接抛异常；AbortPolicy
         *      2、谁调用谁执行；CallerRunsPolicy
         *      3、不执行也不抛异常；DiscardPolicy
         *      4、将阻塞队列等待时间最长的出列，再将新任务塞进去，DiscardOldestPolicy
         * 自定义拒绝策略只需要实现RejectedExecutionHandler一个接口；
         */
        return new ThreadPoolExecutor(50,
                500,
                30,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10000));
        /**
         * 1、线程池初始化时，线程池中线程数量为0个；（核心线程和非核心线程都没有）
         * 2、有任务时，会创建核心线程；
         * 3、当任务超过核心线程数时，任务会被放在阻塞队列中；
         * 4、当核心线程和阻塞队列都满了的情况下，才会创建非核心线程；
         * 5、当核心线程、阻塞队列和非核心线程都满了，就会执行拒绝策略；
         * 6、当所有任务都执行完，并且超过空闲线程的存活时间时，非核心线程会被回收，而核心线程不会被回收
         */
        
    }
}