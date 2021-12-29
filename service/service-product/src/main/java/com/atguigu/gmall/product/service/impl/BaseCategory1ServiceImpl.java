package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * BaseCategory1业务层，处理表述层的业务逻辑
 */
@Service
public class BaseCategory1ServiceImpl implements BaseCategory1Service {

    @Resource
    BaseCategory1Mapper baseCategory1Mapper;

    /**
     * 通过id查询
     *
     * @param id 商品id
     * @return
     */
    public BaseCategory1 getById(Long id) {
        return baseCategory1Mapper.selectById(id);
    }

    /**
     * 查询所有
     * 查询所有的数据，所以是条件为空
     *
     * @return
     */
    @Override
    public List<BaseCategory1> getAll() {
        return baseCategory1Mapper.selectList(null);
    }

    /**
     * 新增数据
     *
     * @param baseCategory1
     */
    @Override
    public Integer add(BaseCategory1 baseCategory1) {

        return baseCategory1Mapper.insert(baseCategory1);
    }

    /**
     * 根据ID更新数据
     *
     * @param baseCategory1
     */
    @Override
    public Integer updateById(BaseCategory1 baseCategory1) {

        return baseCategory1Mapper.updateById(baseCategory1);
    }

    /**
     * 根据ID删除数据
     *
     * @param id
     * @return
     */
    @Override
    public Integer deleteById(Long id) {

        return baseCategory1Mapper.deleteById(id);
    }

    /**
     * 根据条件查询
     *
     * @param baseCategory1
     * @return
     */
    @Override
    public List<BaseCategory1> selectByIdOrName(BaseCategory1 baseCategory1) {

        //判断是否有查询条件，没有直接查询所有
        if (baseCategory1 == null) {
            getAll();
        }

        //拿到返回的条件结果进行查询
        LambdaQueryWrapper<BaseCategory1> LambdaQueryWrapper = buildQueryParam(baseCategory1);

        return baseCategory1Mapper.selectList(LambdaQueryWrapper);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseCategory1> paging(Integer page, Integer size) {
        return baseCategory1Mapper.selectPage(new Page<>(page, size), null);
    }

    /**
     * 通过分页条件查询
     *
     * @param page
     * @param size
     * @param baseCategory1
     * @return
     */
    @Override
    public IPage<BaseCategory1> ConditionalPaging(Integer page,
                                       Integer size,
                                       BaseCategory1 baseCategory1) {
        //构建条件
        LambdaQueryWrapper<BaseCategory1> wrapper = buildQueryParam(baseCategory1);
        //分页查询
        return baseCategory1Mapper.selectPage(new Page<>(page, size), wrapper);
    }

    /**
     * 条件查询
     * @param baseCategory1
     * @return
     */
    private LambdaQueryWrapper<BaseCategory1> buildQueryParam(BaseCategory1 baseCategory1) {
        //创建LambdaQueryWrapper对象
        LambdaQueryWrapper<BaseCategory1> wrapper = new LambdaQueryWrapper<>();

        //id不为空
        if (baseCategory1.getId() != null) {
            wrapper.eq(BaseCategory1::getId, baseCategory1.getId());
        }

        //name不为空
        if (StringUtils.isNotEmpty(baseCategory1.getName())) {
            wrapper.like(BaseCategory1::getName, baseCategory1.getName());
        }

        //返回条件的结果
        return wrapper;
    }

}
