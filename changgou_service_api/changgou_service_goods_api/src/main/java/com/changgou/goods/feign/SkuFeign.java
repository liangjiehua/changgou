package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Steven
 * @version 1.0
 * @description com.changgou.goods.feign
 * @date 2019-9-6
 */
@FeignClient(name = "goods")
@RequestMapping("/sku")
public interface SkuFeign {

    @GetMapping("/status/{status}")
    public Result<List<Sku>> findByStatus(@PathVariable(value = "status") String status);

    @GetMapping("/{id}")
    public Result<Sku> findById(@PathVariable(value = "id") Long id);

    /***
     * 库存递减
     * @param username 注意，由于我们此方法由order调起，所以此处的参数可以从service-order传入
     * @return
     */
    @PostMapping(value = "/decr/count/{username}")
    public Result decrCount(@PathVariable(value = "username") String username);
}
