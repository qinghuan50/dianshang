package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product")
public class ManageController {

    @Autowired
    ManageService manageService;

    /**
     * 查询所有的一级分类
     * @return
     */
    @GetMapping("/getCategory1")
    public Result getCategory1(){
        return Result.ok(manageService.findOneAll());
    }

    /**
     * 查询所有的二级分类
     * @return
     */
    @GetMapping("/getCategory2/{id}")
    public Result getCategory2(@PathVariable("id") Long id){
        return Result.ok(manageService.findTwoAll(id));
    }

    /**
     * 查询所有的三级分类
     * @return
     */
    @GetMapping("/getCategory3/{id}")
    public Result attrInfoList(@PathVariable("id") Long id){

        return Result.ok(manageService.findThreeAll(id));
    }

    /**
     * 查询分类下所对应的数据
     * @param id1   一级分类
     * @param id2   二级分类
     * @param id3   三级分类
     * @return
     */
    @GetMapping("/attrInfoList/{id1}/{id2}/{id3}")
    public Result attrInfoList(@PathVariable("id1") Long id1,
                               @PathVariable("id2") Long id2,
                               @PathVariable("id3") Long id3){

        return Result.ok(manageService.attrInfoList(id1,id2,id3));
    }

    /**
     * 添加分类中的属性
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        return Result.ok(manageService.saveAttrInfo(baseAttrInfo));
    }

    /**
     * 修改平台属性时的数据回显
     * @param id 分类级别的id
     * @return
     */
    @GetMapping("/getAttrValueList/{id}")
    public Result getAttrValueList(@PathVariable("id") Long id){
        return Result.ok(manageService.getAttrValueList(id));
    }


}
