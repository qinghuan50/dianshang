package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    @Resource
    BaseTradeMarkMapper baseTradeMarkMapper;

    @Resource
    BaseSaleAttrMapper baseSaleAttrMapper;

    @Resource
    SpuInfoMapper spuInfoMapper;

    @Resource
    SpuImageMapper spuImageMapper;

    @Resource
    SpuSaleAttrMapper spuSaleAttrMapper;

    @Resource
    SpuSaleAttrValueMapper spuSaleAttrValueMapper;

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

    /**
     * 查询所有的品牌
     * @return
     */
    @Override
    public List<BaseTrademark> getTrademarkList() {

        return baseTradeMarkMapper.selectList(null);
    }

    /**
     * 查询所有的销售属性
     * @return
     */
    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);
    }

    /**
     * 新增商品spu属性
     * @param spuInfo
     * @return
     */
    @Override
    public SpuInfo saveSpuInfo(SpuInfo spuInfo) {
        //校验表单
        if (spuInfo == null) {
            throw new RuntimeException("参数错误！");
        }

        //判断是否有ID
        if (spuInfo.getId() == null) {
            //没有ID则为新增
            spuInfoMapper.insert(spuInfo);

            //如果为新增，就添加图片spu_image
            List<SpuImage> spuImages = addSpuImageList(spuInfo.getSpuImageList(), spuInfo.getId());

            //把带有id新的结果覆盖之前的数据
            spuInfo.setSpuImageList(spuImages);

            //新增销售属性；spu_sale_attr和spu_sale_attr_value；
            List<SpuSaleAttr> spuSaleAttrs = addSpuSaleAttr(spuInfo.getSpuSaleAttrList(), spuInfo.getId());

            //把带有id新的结果覆盖之前的数据
            spuInfo.setSpuSaleAttrList(spuSaleAttrs);

            //返回结果
            return spuInfo;
        }else {

            //有ID则为修改
            spuInfoMapper.updateById(spuInfo);
            //如果为修改，就删除SpuInfo表中的spuSaleAttrList和spuImageList关联的表数据
            //删除图片
            spuImageMapper.delete(new LambdaQueryWrapper<SpuImage>()
                    .eq(SpuImage::getSpuId,spuInfo.getId()));

            //删除商品属性名
            spuSaleAttrMapper.delete(new LambdaQueryWrapper<SpuSaleAttr>()
                    .eq(SpuSaleAttr::getSpuId,spuInfo.getId()));

            //删除商品属性名
            spuSaleAttrValueMapper.delete(new LambdaQueryWrapper<SpuSaleAttrValue>()
                    .eq(SpuSaleAttrValue::getSpuId,spuInfo.getId()));
        }
        //出现问题，则返回空值
        return null;
    }

    /**
     * 分页条件查询所有的spu
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<SpuInfo> findSpuInfoPage(Long page, Long size, Long category3Id) {
        //参数校验
        //分类ID
        if (category3Id == null) {
            throw new RuntimeException("参数错误！");
        }

        //判断页码
        if (page == null) {
            //默认是第一页
            page = 1L;
        }

        //判断每页显示的数量
        if (size == null) {
            //默认每页显示10条
            size = 10L;
        }

        //分页条件查询
        IPage<SpuInfo> spuInfoIPage = spuInfoMapper.selectPage(new Page<SpuInfo>(page, size),
                new LambdaQueryWrapper<SpuInfo>()
                        .eq(SpuInfo::getCategory3Id, category3Id));

        //返回结果
        return spuInfoIPage;
    }

    /**
     * 新增销售属性
     * @param spuSaleAttrList
     * @param id
     * @return
     */
    private List<SpuSaleAttr> addSpuSaleAttr(List<SpuSaleAttr> spuSaleAttrList, Long id) {

        List<SpuSaleAttr> saleAttrList = spuSaleAttrList.stream().map(spuSaleAttr -> {
            //设置spuInfo的id
            spuSaleAttr.setSpuId(id);
            //新增销售属性名称的表
            spuSaleAttrMapper.insert(spuSaleAttr);
            //设置销售属性值的表
            List<SpuSaleAttrValue> spuSaleAttrValueList = addSpuSaleAttrValue(spuSaleAttr);
            //有数据的替换没有数据的，新的替换旧的
            spuSaleAttr.setSpuSaleAttrValueList(spuSaleAttrValueList);
            //返回结果
            return spuSaleAttr;
        }).collect(Collectors.toList());
        //返回结果
        return saleAttrList;
    }

    /**
     * 新增销售属性值
     * @param spuSaleAttr
     * @return
     */
    private List<SpuSaleAttrValue> addSpuSaleAttrValue(SpuSaleAttr spuSaleAttr) {

        //获取销售列表
        List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();

        //新增销售值的数据
        List<SpuSaleAttrValue> saleAttrValues = spuSaleAttrValueList.stream().map(spuSaleAttrValue -> {
            //设置id
            spuSaleAttrValue.setSpuId(spuSaleAttr.getSpuId());
            //设置名字
            spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
            //新增
            spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            //返回结果
            return spuSaleAttrValue;
        }).collect(Collectors.toList());
        //返回结果
        return saleAttrValues;
    }

    /**
     * 新增图片
     * @param spuImageList
     * @param id
     * @return
     */
    private List<SpuImage> addSpuImageList(List<SpuImage> spuImageList, Long id) {

        List<SpuImage> imageList = spuImageList.stream().map(spuImage -> {
            //把id设置进去
            spuImage.setSpuId(id);
            //新增图片
            spuImageMapper.insert(spuImage);

            return spuImage;
        }).collect(Collectors.toList());

        return imageList;
    }


}



