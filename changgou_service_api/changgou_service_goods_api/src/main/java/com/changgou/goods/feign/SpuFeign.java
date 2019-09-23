package com.changgou.goods.feign;

import com.changgou.goods.pojo.Spu;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Steven
 * @version 1.0
 * @description com.changgou.goods.feign
 * @date 2019-9-14
 */
@FeignClient(name = "goods")
@RequestMapping("spu")
public interface SpuFeign {
    @GetMapping("/{id}")
    public Result<Spu> findById(@PathVariable(value = "id") Long id);
}
