package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * BaseCategory1表述层，用来处理前端请求
 */
@RestController
@RequestMapping("api/BaseCategory1")
public class BaseCategory1Controller {

    @Autowired
    BaseCategory1Service baseCategory1Service;

    @GetMapping("/getById/{id}")
    public Result getById(@PathVariable("id") Long id){
        /**
         * 把查询到的结果返回
         */
        return Result.ok(baseCategory1Service.getById(id));
    }

    /**
     * 查询所有
     * @return 返回的是一个集合
     */
    @GetMapping("/getAll")
    public List<BaseCategory1> getAll(){

        return baseCategory1Service.getAll();
    }


    /**
     * 新增数据
     * @param baseCategory1
     * @return
     */
    @PostMapping("/add")
    public Result<Integer> insert(@RequestBody BaseCategory1 baseCategory1){

        return Result.ok(baseCategory1Service.add(baseCategory1));
    }

    /**
     * 根据id更新数据
     * @param baseCategory1
     * @return
     */
    @PutMapping("/updateById")
    public Result<Integer> updateById(@RequestBody BaseCategory1 baseCategory1 ){
        return Result.ok(baseCategory1Service.updateById(baseCategory1));
    }

    /**
     * 根据ID删除
     * @param id
     * @return
     */
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable("id") Long id){
        return Result.ok(baseCategory1Service.deleteById(id));
    }

    /**
     * 通过条件查询
     * @param baseCategory1
     * @return
     */
    @PostMapping("/selectByIdOrName")
    public Result selectByIdOrName(@RequestBody BaseCategory1 baseCategory1){
        return Result.ok(baseCategory1Service.selectByIdOrName(baseCategory1));
    }

    /**
     * 通过分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/paging/{page}/{size}")
    public Result paging(@PathVariable("page") Integer page,
                       @PathVariable("size") Integer size){

        return Result.ok(baseCategory1Service.paging(page,size));
    }

    /**
     * 通过条件分页查询
     * @param baseCategory1
     * @return
     */
    @PostMapping("/ConditionalPaging/{page}/{size}")
    public Result selectByIdOrName(@RequestBody BaseCategory1 baseCategory1,
                         @PathVariable("page") Integer page,
                         @PathVariable("size") Integer size){
        return Result.ok(baseCategory1Service.ConditionalPaging(page, size, baseCategory1));
    }

}
