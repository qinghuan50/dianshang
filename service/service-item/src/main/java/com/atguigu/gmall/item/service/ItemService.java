package com.atguigu.gmall.item.service;

import java.util.Map;

/**
 * @Author: wujijun
 * @Description: 商品详情的接口
 * @Date Created in 2022-01-05-15:21
 * @Modified By:
 */
public interface ItemService {

    /**
     * @ClassName itemService
     * @Description 获取商品详情信息
     * @Author wujijun
     * @Date 2022/1/5 15:28
     * @Param [skuId]
     * @Return java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String, Object> getSkuItem(Long skuId);

}
