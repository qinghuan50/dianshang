package com.atguigu.gmall.list.service;

/**
 * @Author: wujijun
 * @Description: es商品数据的业务接口层
 * @Date Created in 2022-01-11-23:30
 */
public interface ListService {

    /**
     * @ClassName ListService
     * @Description 将商品的数据存入es中
     * @Author wujijun
     * @Date 2022/1/11 23:31
     * @Param []
     * @Return void
     */
    void addSkuEs(Long skuId);

    /**
     * @ClassName ListService
     * @Description 将商品数据从es中删除
     * @Author wujijun
     * @Date 2022/1/11 23:31
     * @Param []
     * @Return void
     */
    void delSkuEs(Long skuId);

    /**
     * @ClassName ListService
     * @Description 修改商品的热度值
     * @Author wujijun
     * @Date 2022/1/12 17:36
     * @Param []
     * @Return void
     */
    void addHotScore(Long skuId);
}
