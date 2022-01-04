package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 销售属性名字的mapper映射
 */
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    /**
     * 在spu添加sku中查询所有的销售属性
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> spuSaleAttrList(@Param("spuId") Long spuId);
}
