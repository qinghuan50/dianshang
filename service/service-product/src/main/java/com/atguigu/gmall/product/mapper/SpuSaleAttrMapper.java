package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 销售属性名字的mapper映射
 */
@Mapper
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    /**
     * 在spu添加sku中查询所有的销售属性
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> spuSaleAttrList(@Param("spuId") Long spuId);

    /**
     * @ClassName SpuSaleAttrMapper
     * @Description 查询商品详情中销售属性信息，并标识当前页面是那种类型的销售属性
     * @Author wujijun
     * @Date 2022/1/5 20:04
     * @Param [spuId, skuId]
     * @Return java.util.List<com.atguigu.gmall.model.product.SpuSaleAttr>
     */
    List<SpuSaleAttr> findSaleAttrBySkuIdAndSpuId(@Param("spuId") Long spuId,
                                                  @Param("skuId") Long skuId);
}
