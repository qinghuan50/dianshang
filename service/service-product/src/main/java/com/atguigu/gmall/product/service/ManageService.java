package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 平台属性的业务接口层
 */
public interface ManageService {

    /**
     * 平台属性一级分类
     *
     * @return
     */
    List<BaseCategory1> findOneAll();

    /**
     * 平台属性二级分类
     *
     * @return
     */
    List<BaseCategory2> findTwoAll(Long id);

    /**
     * 平台属性三级分类
     *
     * @return
     */
    List<BaseCategory3> findThreeAll(Long id);

    /**
     * 查询分类下所对应的数据
     *
     * @param id1 一级分类
     * @param id2 二级分类
     * @param id3 三级分类
     */
    List<BaseAttrInfo> attrInfoList(Long id1, Long id2, Long id3);

    /**
     * 添加分类详情
     *
     * @param baseAttrInfo
     * @return
     */
    BaseAttrInfo saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 修改分类详情的数据回显
     *
     * @param id
     * @return
     */
    List<BaseAttrValue> getAttrValueList(Long id);

    /**
     * 查询所有的品牌
     *
     * @return
     */
    List<BaseTrademark> getTrademarkList();

    /**
     * 查询所有的销售属性
     *
     * @return
     */
    List<BaseSaleAttr> baseSaleAttrList();

    /**
     * 新增商品spu属性
     *
     * @return
     */
    SpuInfo saveSpuInfo(SpuInfo spuInfo);

    /**
     * 分页条件查询所有的spu
     * @param page
     * @param size
     * @return
     */
    IPage<SpuInfo> findSpuInfoPage(Long page, Long size, Long category3Id);

    /**
     * 在spu添加sku中查询所有的销售属性
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> spuSaleAttrList(Long spuId);

    /**
     * 在spu添加sku中查询图片
     * @param spuId
     * @return
     */
    List<SpuImage> spuImageList(Long spuId);

    /**
     * 新增spu中的sku属性
     * @param skuInfo
     * @return
     */
    SkuInfo saveSkuInfo(SkuInfo skuInfo);

    /**
     * 分页查询sku销售属性
     * @param page
     * @param size
     * @return
     */
    IPage<SkuInfo> findSkuInfoAll(Long page, Long size);


    /**
     * 上架或者下架
     * @param skuId
     * @param status
     */
    void onOrCanceCale(Long skuId,Short status);
}
