package com.changgou.evaluation.controller;

import com.entity.Result;
import com.entity.StatusCode;
import com.changgou.evaluation.pojo.Evaluation;
import com.changgou.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/evaluation")
public class EvaluationController {
    @Autowired
    private EvaluationService evaluationService;

    @PostMapping("/add")
    public Result add(@RequestBody Evaluation evaluation){
        evaluationService.add(evaluation);
        return new Result(true, StatusCode.OK,"评价成功");
    }

    @GetMapping("/{skuId}")
    public Result findBySkuId(@PathVariable("skuId") Long skuId){
        Evaluation evaluation = evaluationService.findBySkuId(skuId);
        return new Result(true,StatusCode.OK,"查询成功",evaluation);
    }
}
