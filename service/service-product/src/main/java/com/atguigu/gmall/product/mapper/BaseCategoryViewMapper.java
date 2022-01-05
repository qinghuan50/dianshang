package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: wujijun
 * @Description: 查询一二三级分类信息
 * @Date Created in 2022-01-05-18:44
 * @Modified By:
 */
@Mapper
public interface BaseCategoryViewMapper extends BaseMapper<BaseCategoryView> {
}
