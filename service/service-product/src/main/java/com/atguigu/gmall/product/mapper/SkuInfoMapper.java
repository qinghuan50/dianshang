package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * SkuInfo属性的mapper映射
 */
@Mapper
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    /**
     * @ClassName SkuInfoMapper
     * @Description 使用乐观锁，扣减库存
     * @Author wujijun
     * @Date 2022/1/19 0:20
     * @Param [skuId, num]
     * @Return int
     */
    @Update("UPDATE sku_info SET stock = stock - #{num} WHERE id = #{skuId} AND stock >= #{num};")
    int delCountStock(@Param("skuId") Long skuId, @Param("num") Integer num);

    /**
     * @ClassName SkuInfoMapper
     * @Description 取消订单，回滚库存
     * @Author wujijun
     * @Date 2022/1/20 23:52
     * @Param [skuId, num]
     * @Return int
     */
    @Update("UPDATE sku_info SET stock = stock + #{num} WHERE id = #{skuId};")
    int rollBackStock(@Param("skuId") Long skuId, @Param("num") Integer num);
}
