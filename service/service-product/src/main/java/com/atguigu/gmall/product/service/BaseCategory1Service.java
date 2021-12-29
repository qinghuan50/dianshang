package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface BaseCategory1Service {

    /**
     * 通过id查询
     * @param id 商品id
     * @return
     */
    BaseCategory1 getById(Long id);

    /**
     * 查询所有
     * @return
     */
    List<BaseCategory1> getAll();

    /**
     * 根据ID新增数据
     * @param baseCategory1
     */
    Integer add(BaseCategory1 baseCategory1);

    /**
     * 更新数据
     * @param baseCategory1
     */
    Integer updateById(BaseCategory1 baseCategory1);


    /**
     * 根据ID删除数据
     * @param id
     * @return
     */
    Integer deleteById(Long id);

    /**
     * 根据条件查询
     * @param baseCategory1
     * @return
     */
    List<BaseCategory1> selectByIdOrName(BaseCategory1 baseCategory1);

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    IPage<BaseCategory1> paging(Integer page,Integer size);

    /**
     * 通过分页条件查询
     * @param page
     * @param size
     * @param baseCategory1
     * @return
     */
    IPage<BaseCategory1> ConditionalPaging(Integer page,Integer size,BaseCategory1 baseCategory1);


}
