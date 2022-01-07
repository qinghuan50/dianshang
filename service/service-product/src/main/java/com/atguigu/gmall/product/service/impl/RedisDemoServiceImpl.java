package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.service.RedisDemoService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: wujijun
 * @Description: redis测试
 * @Date Created in 2022-01-07-16:11
 */
@Service
public class RedisDemoServiceImpl implements RedisDemoService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    /**解决redis集群的问题
     * @ClassName RedisDemo
     * @Description 测试redis
     * @Author wujijun
     * @Date 2022/1/7 16:11
     * @Param []
     * @Return java.lang.String
     */
    @Override
    public void TestRedis() {
        //生成UUID解决误删
        String uuid = UUID.randomUUID().toString().replace("-","");
        //通过setnx来设置分布式所
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid,10,TimeUnit.SECONDS);
        //判断是否拿到了锁
        if (lock) {
            //拿到了锁去redis拿数据
            Integer o = (Integer)redisTemplate.opsForValue().get("qinghuan");
            //判断redis中没有该数据
            if (o == null) {
                return;
            }
            //redis中有数据
            redisTemplate.opsForValue().set("qinghuan", ++o, 5*60, TimeUnit.SECONDS);
            //使用lua脚本，解决时间差
            DefaultRedisScript<Long> lua = new DefaultRedisScript<>();
            //设置lua的脚本
            lua.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
            //设置返回的类型
            lua.setResultType(Long.class);
            //执行lua脚本删除锁
            redisTemplate.execute(lua, Arrays.asList("lock"),uuid);

//            //获取锁的值
//            String lockValue = (String)redisTemplate.opsForValue().get("lock");
//            //判断是不是自己的加锁时的值
//            if (uuid.equals(lockValue)) {
//                //释放锁
//                redisTemplate.delete("lock");
//            }
        }else {
            //没有拿到锁
            try {
                //睡1秒
                Thread.sleep(1000);
                //自己调自己，递归
                TestRedis();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @ClassName RedisDemoService
     * @Description 测试redisson
     * @Author wujijun
     * @Date 2022/1/7 19:03
     * @Param []
     * @Return void
     */
    @Override
    public void TestRedisson() {
       //获取锁
        RLock lock = redissonClient.getLock("lock");
        //尝试加锁
        try {
            //第一个参数，多长时间内重试获取锁，第二个参数锁的过期时间
            if (lock.tryLock(10,10, TimeUnit.SECONDS)) {
                try {
                    //加锁成功获取redis中的值
                    Integer value = (Integer)redisTemplate.opsForValue().get("qinghuan");
                    //判断
                    if (value != null) {
                        //设置redis的值
                        redisTemplate.opsForValue().set("qinghuan", ++value, 5*60, TimeUnit.SECONDS);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //释放锁
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**本地锁（只能解决单点，不能解决集群问题）
     * @ClassName RedisDemo
     * @Description 测试redis
     * @Author wujijun
     * @Date 2022/1/7 16:11
     * @Param []
     * @Return java.lang.String
     */
//    @Override：
//    public synchronized void TestRedis() {
//        //从redis中获取值
//        Integer o = (Integer)redisTemplate.opsForValue().get("qinghuan");
//        //判断value是否为空
//        if (o != null) {
//            //把值存入redis，并设置过期时间
//            redisTemplate.opsForValue().set("qinghuan", ++ o ,5*60, TimeUnit.SECONDS);
//        }
//    }

}
