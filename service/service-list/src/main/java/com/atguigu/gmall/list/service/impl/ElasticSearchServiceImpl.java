package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.service.ElasticSearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchResponseAttrVo;
import com.atguigu.gmall.model.list.SearchResponseTmVo;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: wujijun
 * @Description: 首页搜索
 * @Date Created in 2022-01-12-18:58
 */
@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    /**
     * @param searchData
     * @ClassName ElasticSearchService
     * @Description 从首页进商品详情页
     * @Author wujijun
     * @Date 2022/1/12 18:50
     * @Param [searchData]
     * @Return java.util.Map<java.lang.String, java.lang.Object>
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchData) {
        //搜索条件
        SearchRequest searchRequest = buildQueryParms(searchData);

        try {
            //执行查询；SearchRequest searchRequest, RequestOptions options
            SearchResponse searchResponse =
                    restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //返回查询的结果
            return getResult(searchResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @ClassName ElasticSearchServiceImpl
     * @Description 解析查询出来的数据
     * @Author wujijun
     * @Date 2022/1/12 19:37
     * @Param [searchResponse]
     * @Return java.util.Map<java.lang.String, java.lang.Object>
     */
    private Map<String, Object> getResult(SearchResponse searchResponse) {
        //初始化返回的数据类型
        Map<String, Object> resultMap = new HashMap<>();
        //初始化返回的数据对象
        List<Goods> goodsList = new ArrayList<>();
        //获取所有的数据
        SearchHits hits = searchResponse.getHits();
        //获取数据的迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        //遍历数据
        while (iterator.hasNext()) {
            //获取每一条数据
            SearchHit next = iterator.next();
            //把数据转换成json类型的字符串
            String sourceAsString = next.getSourceAsString();
            //字符串转换成对象，反序列化
            Goods goods = JSONObject.parseObject(sourceAsString, Goods.class);
            //保存商品的数据
            goodsList.add(goods);
        }
        //保存所有的商品数据
        resultMap.put("goodsList", goodsList);
        //所有的聚合查询的结果
        Aggregations aggregations = searchResponse.getAggregations();
        //解析品牌的聚合查询结果
        List<SearchResponseTmVo> searchResponseTmVoList = getAddResult(aggregations);
        //把解析出来的结果添加到要返回的对象中去
        resultMap.put("searchResponseTmVoList", searchResponseTmVoList);
        //解析平台聚合结果
        List<SearchResponseAttrVo> searchResponseAttrVoList = getAggAttrResult(aggregations);
        //把解析出来的结果添加到要返回的对象中去
        resultMap.put("searchResponseAttrVoList", searchResponseAttrVoList);
        //返回结果
        return resultMap;
    }

    /**
     * @ClassName ElasticSearchServiceImpl
     * @Description 解析平台聚合结果
     * @Author wujijun
     * @Date 2022/1/12 21:36
     * @Param [aggregations]
     * @Return void
     * @return
     */
    private List<SearchResponseAttrVo> getAggAttrResult(Aggregations aggregations) {
        //通过别名获取所有的平台属性的结果
        ParsedNested aggAttrNested = aggregations.get("aggAttr");
        //获取平台属性中ID信息
        ParsedLongTerms aggAttrId = aggAttrNested.getAggregations().get("aggAttrId");
        //初始化返回结果对象
        SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
        //获取平台属性id对应的数据
        List<SearchResponseAttrVo> searchResponseAttrVoList = aggAttrId.getBuckets().stream().map(agg -> {
            //获取平台属性的id
            long attrId = agg.getKeyAsNumber().longValue();
            //把数据封装到返回的对象中
            searchResponseAttrVo.setAttrId(attrId);
            //获取平台属性的名字
            ParsedStringTerms aggAttrName = agg.getAggregations().get("aggAttrName");
            //获取平台名字的列表
            List<? extends Terms.Bucket> nameBuckets = aggAttrName.getBuckets();
            //判断是否有数据
            if (nameBuckets != null || !nameBuckets.isEmpty()) {
                //拿到第一个名字
                String attrName = nameBuckets.get(0).getKeyAsString();
                //把数据封装到返回的对象中
                searchResponseAttrVo.setAttrName(attrName);
            }
            //获取平台属性的值
            ParsedStringTerms aggAttrValue = agg.getAggregations().get("aggAttrValue");
            //获取平台名字对应的值的列表
            List<? extends Terms.Bucket> valueBuckets = aggAttrValue.getBuckets();
            //判断是否含有数据
            if (valueBuckets != null || !valueBuckets.isEmpty()) {
                List<String> valueList = valueBuckets.stream().map(value -> {
                    //获取平台属性值的信息
                    String valueName = value.getKeyAsString();
                    //返回结果
                    return valueName;
                }).collect(Collectors.toList());
                //把数据封装到返回的对象中
                searchResponseAttrVo.setAttrValueList(valueList);
            }
            return searchResponseAttrVo;
        }).collect(Collectors.toList());
        //返回最终的结果
        return searchResponseAttrVoList;
    }

    /**
     * @ClassName ElasticSearchServiceImpl
     * @Description 解析品牌的聚合查询结果
     * @Author wujijun
     * @Date 2022/1/12 20:46
     * @Param [aggregations]
     * @Return java.util.List<com.atguigu.gmall.model.list.SearchResponseTmVo>
     */
    private List<SearchResponseTmVo> getAddResult(Aggregations aggregations) {
        //通过别名获取聚合查询的品牌结果
        ParsedLongTerms aggTmId = aggregations.get("aggTmId");
        //获取所有品牌中的数据
        List<SearchResponseTmVo> searchResponseTmVoList = aggTmId.getBuckets().stream().map(bucket -> {
            //初始化一个存品牌的对象
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            //获取品牌的id
            long tmId = bucket.getKeyAsNumber().longValue();
            //把获取的id存入对象中
            searchResponseTmVo.setTmId(tmId);
            //获取子聚合的品牌名字
            ParsedStringTerms aggTmName = bucket.getAggregations().get("aggTmName");
            //获取品牌的信息
            List<? extends Terms.Bucket> nameBuckets = aggTmName.getBuckets();
            if (nameBuckets != null || !nameBuckets.isEmpty()) {
                //不为空时,获取第一个品牌的名字
                String tmName = nameBuckets.get(0).getKeyAsString();
                //把获取的名字存入对象中
                searchResponseTmVo.setTmName(tmName);
            }
            //获取子聚合的品牌logo的图片
            ParsedStringTerms aggTmLogoUrl = bucket.getAggregations().get("aggTmLogoUrl");
            List<? extends Terms.Bucket> logoUrlBuckets = aggTmLogoUrl.getBuckets();
            if (logoUrlBuckets != null || !nameBuckets.isEmpty()) {
                //不为空时，获取第一个品牌的图片
                String tmLogoUrl = logoUrlBuckets.get(0).getKeyAsString();
                //把获取的图片存入对象中
                searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
            }
            //返回封装好的对象
            return searchResponseTmVo;
        }).collect(Collectors.toList());
        //返回结果
        return searchResponseTmVoList;
    }

    /**
     * @ClassName ElasticSearchServiceImpl
     * @Description 构建查询的条件
     * @Author wujijun
     * @Date 2022/1/12 19:16
     * @Param [searchData]
     * @Return org.elasticsearch.action.search.SearchRequest
     */
    private SearchRequest buildQueryParms(Map<String, String> searchData) {
        //初始化搜索条件的对象
        SearchRequest searchRequest = new SearchRequest("goods-wjj");
        //初始化条件构造器
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //遍历取出所有的查询条件
        //关键字查询（搜索框内输入的）
        String keyWords = searchData.get("keywords");
        //判断是否是从搜索框中进入的商品列表
        if (StringUtils.isEmpty(keyWords)) {
            //matchQuery和string查询，前者不止支持字符串还支持数字，同时也支持分词
            builder.query(QueryBuilders.matchQuery("title", keyWords));
        }
        //设置聚合查询的条件；.subAggregation子聚合
        builder.aggregation(
                AggregationBuilders.terms("aggTmId").field("tmId")
                        .subAggregation(AggregationBuilders.terms("aggTmName").field("tmName"))
                        .subAggregation(AggregationBuilders.terms("aggTmLogoUrl").field("tmLogoUrl"))
        );
        //设置平台属性的条件
        builder.aggregation(
                AggregationBuilders.nested("aggAttr", "attrs")
                        .subAggregation(AggregationBuilders.terms("aggAttrId").field("attrs.attrId")
                                .subAggregation(AggregationBuilders.terms("aggAttrName").field("attrs.attrName")
                                        .subAggregation(AggregationBuilders.terms("aggAttrValue").field("attrs.attrValue")
                                        )
                                )
                        )
        );
        //设置查询条件
        searchRequest.source();
        //返回对象
        return searchRequest;
    }
}
