package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * skuSaleAttrValue的销售属性mapper映射
 */
@Mapper
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {

    /**
     * @ClassName SkuSaleAttrValueMapper
     * @Description  根据spu查询sku的键值对
     * @Author wujijun
     * @Date 2022/1/5 20:28
     * @Param [spuId]
     * @Return void
     */
    @Select("SELECT sku_id,GROUP_CONCAT(sale_attr_value_id ORDER BY sale_attr_value_id separator  '|') " +
            "sku_values FROM sku_sale_attr_value WHERE spu_id= #{spuId} GROUP BY sku_id")
    List<Map> getSaleAttrValue(@Param("spuId") Long spuId);
}
