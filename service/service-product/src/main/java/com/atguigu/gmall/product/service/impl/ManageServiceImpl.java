package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 平台属性的业务实现层
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ManageServiceImpl implements ManageService {

    @Resource
    BaseCategory1Mapper baseCategory1Mapper;

    @Resource
    BaseCategory2Mapper baseCategory2Mapper;

    @Resource
    BaseCategory3Mapper baseCategory3Mapper;

    @Resource
    BaseAttrInfoMapper baseAttrInfoMapper;

    @Resource
    BaseAttrValueMapper baseAttrValueMapper;

    /**
     * 查询平台属性的一级分类
     *
     * @return
     */
    @Override
    public List<BaseCategory1> findOneAll() {

        return baseCategory1Mapper.selectList(null);
    }

    /**
     * 查询平台属性二级分类
     *
     * @param id 一级分类的ID
     * @return
     */
    @Override
    public List<BaseCategory2> findTwoAll(Long id) {

        return baseCategory2Mapper.selectList(new LambdaQueryWrapper<BaseCategory2>()
                .eq(BaseCategory2::getCategory1Id, id));
    }

    /**
     * 平台属性三级分类
     *
     * @param id 二级分类的ID
     * @return
     */
    @Override
    public List<BaseCategory3> findThreeAll(Long id) {
        return baseCategory3Mapper.selectList(new LambdaQueryWrapper<BaseCategory3>()
                .eq(BaseCategory3::getCategory2Id, id));
    }

    /**
     * 查询分类下所对应的数据
     *
     * @param id1 一级分类
     * @param id2 二级分类
     * @param id3 三级分类
     */
    @Override
    public List<BaseAttrInfo> attrInfoList(Long id1, Long id2, Long id3) {

        return baseAttrInfoMapper.attrInfoList(id1, id2, id3);
    }

    /**
     * 添加和修改分类详情
     *
     * @param baseAttrInfo
     * @return
     */
    @Override
    public BaseAttrInfo saveAttrInfo(BaseAttrInfo baseAttrInfo) {

        //校验属性
        if (baseAttrInfo == null) {
            throw new RuntimeException("参数错误！");
        }

        //根据传过来的id判断是新增还是修改
        if (baseAttrInfo.getId() != null) {
            //修改操作
            int updateResult = baseAttrInfoMapper.updateById(baseAttrInfo);
            //判断时候修改成功
            if (updateResult <= 0) {
                throw new RuntimeException("修改数据失败，请重试！");
            }
            //把之前旧的数据删掉，重新添加
            int deleteResult = baseAttrValueMapper.delete(new LambdaQueryWrapper<BaseAttrValue>()
                    .eq(BaseAttrValue::getAttrId, baseAttrInfo.getId()));
            //判断时候修改成功
            if (deleteResult < 0) {
                throw new RuntimeException("修改数据失败，请重试！");
            }
        } else {
            //新增操作
            //新增平台数据名称表中的数据
            int insertResult = baseAttrInfoMapper.insert(baseAttrInfo);

            //判断是否新增成功
            if (insertResult <= 0) {
                throw new RuntimeException("添加数据失败，请重试！");
            }
        }


        //获取attrValueList的值
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();

        //设置BaseAttrValue的值
        List<BaseAttrValue> valueList = attrValueList.stream().map(baseAttrValue -> {
            //设置BaseAttrValue中arrtId，主外键关联
            baseAttrValue.setAttrId(baseAttrInfo.getId());
            //设置BaseAttrValue中valueName的值；
            int result = baseAttrValueMapper.insert(baseAttrValue);
            //判断是否成功执行
            if (result <= 0) {
                throw new RuntimeException("添加数据失败，请重试！");
            }
            //返回执行的结果
            return baseAttrValue;
        }).collect(Collectors.toList());

        //设置baseAttrInfo中AttrValueList的值
        baseAttrInfo.setAttrValueList(valueList);

        return baseAttrInfo;
    }

    /**
     * 修改分类详情时的数据回显
     *
     * @param id
     * @return
     */
    @Override
    public List<BaseAttrValue> getAttrValueList(Long id) {
        //数据回显
        return baseAttrValueMapper.selectList(new LambdaQueryWrapper<BaseAttrValue>()
                .eq(BaseAttrValue::getAttrId, id));
    }
}


