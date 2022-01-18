package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.service.ElasticSearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchResponseAttrVo;
import com.atguigu.gmall.model.list.SearchResponseTmVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
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
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
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
            //设置高亮的数据
            Map<String, HighlightField> highlightFields = next.getHighlightFields();
            //防止查询的结果没有数据
            if (highlightFields != null && !highlightFields.isEmpty()) {
                //获取查询域所有的高亮数据
                Text[] titles = highlightFields.get("title").getFragments();
                //防止没有结果出现空指针
                if (titles != null && titles.length > 0) {
                    //有数据
                    String title = "";
                    //循环取出所有的结果
                    for (Text text : titles) {
                        //把所有取出来
                        title += text;
                    }
                    //把高亮的数据替换原来的数据
                    goods.setTitle(title);
                }
            }
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
        //该条件下查询出来的所有结果
        resultMap.put("total", hits.totalHits);
        //返回结果
        return resultMap;
    }

    /**
     * @return
     * @ClassName ElasticSearchServiceImpl
     * @Description 解析平台聚合结果
     * @Author wujijun
     * @Date 2022/1/12 21:36
     * @Param [aggregations]
     * @Return void
     */
    private List<SearchResponseAttrVo> getAggAttrResult(Aggregations aggregations) {
        //通过别名获取所有的平台属性的结果
        ParsedNested aggAttrsNested = aggregations.get("aggAttrs");
        //获取平台属性中ID信息
        ParsedLongTerms aggAttrId = aggAttrsNested.getAggregations().get("aggAttrId");
        //获取平台属性id对应的数据
        List<SearchResponseAttrVo> searchResponseAttrVoList = aggAttrId.getBuckets().stream().map(agg -> {
            //初始化返回结果对象
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            //获取平台属性的id
            long attrId = ((Terms.Bucket) agg).getKeyAsNumber().longValue();
            //把数据封装到返回的对象中
            searchResponseAttrVo.setAttrId(attrId);
            //获取平台属性的名字
            ParsedStringTerms aggAttrName = ((Terms.Bucket) agg).getAggregations().get("aggAttrName");
            //获取平台名字的列表
            List<? extends Terms.Bucket> nameBuckets = aggAttrName.getBuckets();
            //判断是否有数据
            if (nameBuckets != null && !nameBuckets.isEmpty()) {
                //拿到第一个名字
                String attrName = nameBuckets.get(0).getKeyAsString();
                //把数据封装到返回的对象中
                searchResponseAttrVo.setAttrName(attrName);
            }
            //获取平台属性的值
            ParsedStringTerms aggAttrValue =((Terms.Bucket) agg).getAggregations().get("aggAttrValue");
            //获取平台名字对应的值的列表
            List<? extends Terms.Bucket> valueBuckets = aggAttrValue.getBuckets(); // TODO: 2022/1/16
            //判断是否含有数据
            if (valueBuckets != null && !valueBuckets.isEmpty()) {
                List<String> valueList = valueBuckets.stream().map(att -> {
                    //获取平台属性值的信息
                    String valueName = ((Terms.Bucket) att).getKeyAsString();
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
            if (nameBuckets != null && !nameBuckets.isEmpty()) {
                //不为空时,获取第一个品牌的名字
                String tmName = nameBuckets.get(0).getKeyAsString();
                //把获取的名字存入对象中
                searchResponseTmVo.setTmName(tmName);
            }
            //获取子聚合的品牌logo的图片
            ParsedStringTerms aggTmLogoUrl = bucket.getAggregations().get("aggTmLogoUrl");
            List<? extends Terms.Bucket> logoUrlBuckets = aggTmLogoUrl.getBuckets();
            if (logoUrlBuckets != null && !nameBuckets.isEmpty()) {
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
        //构建一个条件查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //遍历取出所有的查询条件
        //关键字查询（搜索框内输入的）
        String keyWords = searchData.get("keywords");
        //判断是否是从搜索框中进入的商品列表
        if (!StringUtils.isEmpty(keyWords)) {
            //matchQuery和string查询，前者不止支持字符串还支持数字，同时也支持分词
            builder.query(QueryBuilders.matchQuery("title", keyWords));
        }
        //设置品牌查询
        String tradeMake = searchData.get("tradeMake");
        //判断查询条件中是否包含该参数
        if (!StringUtils.isEmpty(tradeMake)) {
            //取出查询的条件
            String[] split = tradeMake.split(":");
            //组装查询的条件
            boolQueryBuilder.must(QueryBuilders.termQuery("tmId", split[0]));
        }
        //设置平台属性
        for (Map.Entry<String, String> stringStringEntry : searchData.entrySet()) {
            //一个平台属性的查询条件
            BoolQueryBuilder nestedBoolQueryBuilder = QueryBuilders.boolQuery();
            //查询条件中包含attr_就是平台属性
            if (stringStringEntry.getKey().startsWith("attr_")) {
                //拿到平台属性的key
                String key = stringStringEntry.getKey();
                //拿到平台属性的值
                String value = stringStringEntry.getValue();
                //把id和中文属性切割开
                String[] split = value.split(":");
                //组装一个平台属性的查询条件
                nestedBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", split[0]));
                nestedBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrValue", split[1]));
            }
            //组装查询条件;String path查询的类型, QueryBuilder query查询条件, ScoreMode scoreMode权重
            boolQueryBuilder.must(QueryBuilders.nestedQuery("attrs", nestedBoolQueryBuilder, ScoreMode.None));
        }
        //设置价格查询
        String price = searchData.get("price");
        if (!StringUtils.isEmpty(price)) {
            //把查询条件中的中文去掉
            price = price.replace("元", "").replace("以上", "");
            //切分价格最大最小值
            String[] split = price.split("-");
            //取最左边的价格
            boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gte(split[0]));
            //判断右边是否有值
            if (split.length > 1) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lt(split[1]));
            }
        }
        //设置查询的条件，包含条件查询
        builder.query(boolQueryBuilder);
        //设置聚合查询的条件；.subAggregation子聚合
        builder.aggregation(
                AggregationBuilders.terms("aggTmId").field("tmId")
                        .subAggregation(AggregationBuilders.terms("aggTmName").field("tmName"))
                        .subAggregation(AggregationBuilders.terms("aggTmLogoUrl").field("tmLogoUrl"))
                        .size(100)
        );
        //设置平台属性的条件
        builder.aggregation(
                AggregationBuilders.nested("aggAttrs", "attrs")
                        .subAggregation(AggregationBuilders.terms("aggAttrId").field("attrs.attrId")
                                .subAggregation(AggregationBuilders.terms("aggAttrName").field("attrs.attrName")
                                        .subAggregation(AggregationBuilders.terms("aggAttrValue").field("attrs.attrValue")
                                        ).size(100)
                                )
                        )
        );
        //设置排序，以及排序规则
        String sortField = searchData.get("sortField");
        String sortRule = searchData.get("sortRule");
        //判断是否需要排序
        if (!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)) {
            //设置排序规则
            builder.sort(sortRule, SortOrder.valueOf(sortRule));
        }else {
            //没有设置排序规则，默认价格升序
            builder.sort("price", SortOrder.ASC);
        }
        //设置每页显示的条数
        builder.size(100);
        //分页设置，切换当前页
        String page = searchData.get("page");
        //计算用户切换的页码数
        Integer pageNum = getPageNum(page);
        //切换当前页显示的内容
        builder.from((pageNum - 1)*100);
        //初始化对象
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //设置需要高亮的域
        highlightBuilder.field("title");
        //设置高亮的头部标签
        highlightBuilder.preTags("<font style=color:red>");
        //设置高亮的尾部标签
        highlightBuilder.postTags("</font>");
        //设置高亮的数据
        builder.highlighter(highlightBuilder);
        //设置查询的条件
        searchRequest.source(builder);
        //返回对象
        return searchRequest;
    }

    /**
     * @ClassName ElasticSearchServiceImpl
     * @Description 计算切换的页码
     * @Author wujijun
     * @Date 2022/1/16 15:47
     * @Param [page]
     * @Return int
     */
    private Integer getPageNum(String page) {
        try {
            //把字符串类型转换成数字类型
            int i = Integer.parseInt(page);
            //判断用户输入的页码是否符合规则，如果是负数，则显示第一页
            return i>0?i:1;
        } catch (NumberFormatException e) {
            //默认显示第一页。防止用户输入非法字符
            return 1;
        }
    }
}
