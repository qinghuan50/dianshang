package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import org.apache.commons.configuration.tree.ViewNode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 平台属性的业务接口层
 */
public interface ManageService {

    /**
     * 平台属性一级分类
     * @return
     */
    List<BaseCategory1> findOneAll();

    /**
     * 平台属性二级分类
     * @return
     */
    List<BaseCategory2> findTwoAll(Long id);

    /**
     * 平台属性三级分类
     * @return
     */
    List<BaseCategory3> findThreeAll(Long id);

    /**
     * 查询分类下所对应的数据
     * @param id1   一级分类
     * @param id2   二级分类
     * @param id3   三级分类
     */
    List<BaseAttrInfo> attrInfoList(Long id1, Long id2, Long id3);

    /**
     * 添加分类详情
     * @param baseAttrInfo
     * @return
     */
    BaseAttrInfo saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 修改分类详情的数据回显
     * @param id
     * @return
     */
    List<BaseAttrValue> getAttrValueList(Long id);
}
