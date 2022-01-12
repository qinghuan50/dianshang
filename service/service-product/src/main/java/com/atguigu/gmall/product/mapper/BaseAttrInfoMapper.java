package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 平台属性名称
 */
@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {


    List<BaseAttrInfo> attrInfoList(@Param("id1") Long id1,
                                    @Param("id2") Long id2,
                                    @Param("id3") Long id3);


    /**
     * @ClassName BaseAttrInfoMapper
     * @Description 查询商品的平台属性
     * @Author wujijun
     * @Date 2022/1/12 11:42
     * @Param [skuId]
     * @Return java.util.List<com.atguigu.gmall.model.product.BaseAttrInfo>
     */
    List<BaseAttrInfo> selectSkuInfoBySkuId(@Param("skuId") Long skuId);

}
