package com.atguigu.gmall.list.dao;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: wujijun
 * @Description: es商品数据的映射
 * @Date Created in 2022-01-11-23:28
 */
@Repository
public interface GoodsDao extends ElasticsearchRepository<Goods, Long> {

}
