package com.changgou.search.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Steven
 * @version 1.0
 * @description com.changgou.search.feign
 * @date 2019-9-9
 */
@FeignClient(name = "search")
@RequestMapping("search")
public interface SkuFeign {
    /**
     * 搜索商品
     *
     * @return
     */
    @GetMapping
    public Map search(@RequestParam(required = false) Map searchMap);
}
