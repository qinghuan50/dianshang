package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.constant.ProductConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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

    /**
     * 查询所有的品牌列表
     * @return
     */
    @GetMapping("/baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        return Result.ok(manageService.getTrademarkList());
    }

    /**
     * 查询销售属性列表
     * @return
     */
    @GetMapping("/baseSaleAttrList")
    public Result baseSaleAttrList(){
        return Result.ok(manageService.baseSaleAttrList());
    }

    /**
     * 新增spu商品属性
     * @param spuInfo
     * @return
     */
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){

        return Result.ok(manageService.saveSpuInfo(spuInfo));
    }

    /**
     * 分页条件查询所有的spu属性
     * @param page
     * @param size
     * @param category3Id
     * @return
     */
    @GetMapping("/{page}/{size}")
    public Result findSpuInfoPage(@PathVariable("page") Long page,
                                  @PathVariable("size") Long size,
                                  @RequestParam Long category3Id){

        return Result.ok(manageService.findSpuInfoPage(page,size,category3Id));
    }

    /**
     * 在spu添加sku中查询销售属性
     * @param spuId
     * @return
     */
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable("spuId") Long spuId){
        return Result.ok(manageService.spuSaleAttrList(spuId));
    }

    /**
     * 在spu添加sku中查询所有的图片
     * @param spuId
     * @return
     */
    @GetMapping("/spuImageList/{spuId}")
    public Result spuImageList(@PathVariable("spuId") Long spuId){
        return Result.ok(manageService.spuImageList(spuId));
    }

    /**
     * 在spu中新增sku信息
     * @param skuInfo
     * @return
     */
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);

        return Result.ok();
    }

    /**
     * 分页查询sku销售属性
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list/{page}/{size}")
    public Result list(@PathVariable("page") Long page,
                       @PathVariable("size") Long size){

        return Result.ok(manageService.findSkuInfoAll(page,size));
    }

    /**
     * 商品上架：将商品的信息存入elasticsearch中
     * @param skuId
     * @return
     */
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId){
        manageService.onOrCanceCale(skuId, ProductConst.SKUINFO_STATUS_ONSALE);
        return Result.ok();
    }

    /**
     * 商品下架：将商品的信息从elasticsearch删除
     * @param skuId
     * @return
     */
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId){
        manageService.onOrCanceCale(skuId, ProductConst.SKUINFO_STATUS_CANCESALE);
        return Result.ok();
    }

}
