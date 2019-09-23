package com.changgou.evaluation.feign;

import com.entity.Result;
import com.changgou.evaluation.pojo.Evaluation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "evaluation")
@RequestMapping("/evaluation")
public interface EvaluationFeign {

    @PostMapping("/add")
    Result add(@RequestBody Evaluation evaluation);

    @GetMapping("/{skuId}")
    Result findBySkuId(@PathVariable("skuId")Long skuId);
}
