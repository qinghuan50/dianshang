package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.list.feign.SearchFeign;
import com.atguigu.gmall.web.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @Author: wujijun
 * @Description: 前端搜索查询的表述层
 * @Date Created in 2022-01-16-16:16
 */
@Controller
@RequestMapping("/api/search")
public class searchController {

    @Autowired
    SearchFeign searchFeign;

    /**
     * @ClassName searchController
     * @Description 打开搜索页面
     * @Author wujijun
     * @Date 2022/1/16 16:24
     * @Param [searchData, model]
     * @Return java.lang.String
     */
    @GetMapping
    public String search(@RequestParam Map<String, String> searchData, Model model){
        //调取微服务查询数据
        Map<String, Object> resultMap = searchFeign.resultMap(searchData);
        //将数据存入model中
        model.addAllAttributes(resultMap);
        //将查询的条件放入搜索框中去
        model.addAttribute("searchData", searchData);
        //获取url地址
        String url = getUrl(searchData);
        //把url地址放进model中
        model.addAttribute("url", url);
        //获取该查询条件下查询到的所有的数据
        Object total = resultMap.get("total");
        //获取总页数
        Integer pageNum = getPageNum(searchData.get("page"));
        //初始化分页的对象
        Page page = new Page<>(
                Long.valueOf(total.toString()),
                pageNum,
                100);
        //把分页信息放入model
        model.addAttribute("page",page);
        //返回搜索的页面
        return "list";
    }

    /**
     * @ClassName searchController
     * @Description 获取当前查询条件下被分成的总页数
     * @Author wujijun
     * @Date 2022/1/16 19:56
     * @Param [page]
     * @Return java.lang.Integer
     */
    private Integer getPageNum(String page) {
        try {
            int i = Integer.parseInt(page);
            //判断是否为负数,若为负数显示第一页,不为负数显示指定页
            return i>0?i:1;
        }catch (Exception e){
            //默认显示第一页
            return 1;
        }
    }

    /**
     * @ClassName searchController
     * @Description 拼接查询条件的url
     * @Author wujijun
     * @Date 2022/1/16 17:38
     * @Param [searchData]
     * @Return java.lang.String
     */
    private String getUrl(Map<String, String> searchData){
        //初始化URL
        String url = "/api/search?";
        //遍历取出所有的查询条件
        for (Map.Entry<String, String> stringStringEntry : searchData.entrySet()) {
            //获取查询条件的键，名字
            String key = stringStringEntry.getKey();
            //获取查询条件的值
            String value = stringStringEntry.getValue();
            //把排序的字段不加入url中，分页
            if (!key.equals("sortField") && key.equals("sortRule") && key.equals("page")) {
                //拼接url
                url = url + key + "=" + value + "&";
            }
        }
        //去掉最后的&
        return url.substring(0, url.length() - 1);
    }

}
