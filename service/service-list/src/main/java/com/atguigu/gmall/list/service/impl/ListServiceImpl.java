package com.atguigu.gmall.list.service.impl;

import com.atguigu.gmall.list.dao.GoodsDao;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.feign.ProductFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author: wujijun
 * @Description: es商品的数据的业务实现层
 * @Date Created in 2022-01-11-23:33
 */
@Service
public class ListServiceImpl implements ListService {

    @Autowired
    GoodsDao goodsDao;

    @Autowired
    ProductFeign productFeign;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * @param skuId
     * @ClassName ListService
     * @Description 将商品的数据存入es中
     * @Author wujijun
     * @Date 2022/1/11 23:31
     * @Param []
     * @Return void
     */
    @Override
    public void addSkuEs(Long skuId) {
        //初始化es文档对象
        Goods goods = new Goods();
        //将查询到的数据设置到goods中
        SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
        //设置goods中每个属性
        goods.setId(skuInfo.getId());
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setTitle(skuInfo.getSkuName());
        goods.setPrice(productFeign.getPrice(skuId).doubleValue());
        goods.setCreateTime(new Date());
        //查询品牌的信息
        BaseTrademark baseTrademark = productFeign.getBaseTrademark(skuId);
        goods.setTmId(baseTrademark.getId());
        goods.setTmName(baseTrademark.getTmName());
        goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        //查询一二三级分类
        BaseCategoryView baseCategory = productFeign.getBaseCategory(skuId);
        goods.setCategory1Id(baseCategory.getCategory1Id());
        goods.setCategory1Name(baseCategory.getCategory1Name());
        goods.setCategory2Id(baseCategory.getCategory2Id());
        goods.setCategory2Name(baseCategory.getCategory2Name());
        goods.setCategory3Id(baseCategory.getCategory3Id());
        goods.setCategory3Name(baseCategory.getCategory3Name());
        //查询平台属性，把数据封装到SearchAttr中
        List<BaseAttrInfo> baseAttrInfoList = productFeign.selectSkuInfoBySkuId(skuId);
        //流式编程取值
        List<SearchAttr> searchAttrs = baseAttrInfoList.stream().map(baseAttrInfo -> {
            //初始化返回的对象
            SearchAttr searchAttr = new SearchAttr();
            //设置值
            searchAttr.setAttrId(baseAttrInfo.getId());
            searchAttr.setAttrName(baseAttrInfo.getAttrName());
            searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());
            //返回结果
            return searchAttr;
        }).collect(Collectors.toList());
        //把没有数据的替换掉
        goods.setAttrs(searchAttrs);
        //把商品信息存入es中
        goodsDao.save(goods);
    }

    /**
     * @param skuId
     * @ClassName ListService
     * @Description 将商品数据从es中删除
     * @Author wujijun
     * @Date 2022/1/11 23:31
     * @Param []
     * @Return void
     */
    @Override
    public void delSkuEs(Long skuId) {
        goodsDao.deleteById(skuId);
    }

    /**
     * @ClassName ListService
     * @Description 修改商品的热度值
     * @Author wujijun
     * @Date 2022/1/12 17:36
     * @Param []
     * @Return void
     */
    @Override
    public void addHotScore(Long skuId) {
        //获取商品的数据
        Optional<Goods> optional = goodsDao.findById(skuId);
        //判断该商品是否存在
        if (!optional.isPresent()) {
            //商品不存在直接退出
            return;
        }
        //把热度信息存入redis
//        Long increment = redisTemplate.opsForValue().increment("Goods:" + skuId, 1);
        //两者的区别，去重，zSet支持排序，都能解决多线程问题
        Double incrementScore = redisTemplate.opsForZSet().incrementScore("GoodsHotScore", "Goods" + skuId, 1);
        //每十次更新下数据
        if (incrementScore % 10 == 0) {
            //商品存在时，取出商品的数据
            Goods goods = optional.get();
            //设置商品的热度值
            goods.setHotScore(incrementScore.longValue());
            //更新到es中
            goodsDao.save(goods);
        }
    }
}
