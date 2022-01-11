package com.atguigu.gmall.web.controller;

import com.atguiug.gmall.item.feign.ItemFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @Author: wujijun
 * @Description: web接收前端请求
 * @Date Created in 2022-01-11-17:03
 */
@Controller
@RequestMapping("/page/item")
public class WebController {

    @Autowired
    ItemFeign itemFeign;

    @Autowired
    TemplateEngine templateEngine;

    /**
     * @ClassName WebController
     * @Description 打开某一个商品的详情页
     * @Author wujijun
     * @Date 2022/1/11 17:05
     * @Param [skuId]
     * @Return java.lang.String
     */
    @GetMapping("/{skuId}")
    public String item(@PathVariable("skuId") Long skuId,
                       Model model){

        //通过feign远程调用，查询商品详情页所有的信息
        Map<String, Object> skuItem = itemFeign.getSkuItem(skuId);

        //把查询到的数据存入model中，让网页动态获取数据
        model.addAllAttributes(skuItem);

        return "item";
    }

    /**
     * @ClassName WebController
     * @Description 创建页面
     * @Author wujijun
     * @Date 2022/1/11 19:14
     * @Param []
     * @Return java.lang.String
     */
    @GetMapping("/create/{skuId}")
    @ResponseBody
    public String createSkuInfo(@PathVariable("skuId") Long skuId) throws Exception {

        //查看sku的信息
        Map<String, Object> skuItem = itemFeign.getSkuItem(skuId);

        //初始化Context，类似于model
        Context context = new Context();
        context.setVariables(skuItem);

        //初始化一个文件，文件路径 + 文件名
        File file = new File("F:\\", skuId + ".html");
        //初始化打印输出流
        PrintWriter writer = new PrintWriter(file, "UTF-8");

        //生成静态页面
        templateEngine.process("item", context, writer);

        //关闭流
        writer.close();

        return "创建新的页面成功！";
    }


}
