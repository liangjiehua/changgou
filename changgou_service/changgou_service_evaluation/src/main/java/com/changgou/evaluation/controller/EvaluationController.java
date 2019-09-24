package com.changgou.evaluation.controller;

import com.changgou.evaluation.pojo.Evaluation;
import com.changgou.evaluation.service.EvaluationService;
import entity.Result;
import entity.StatusCode;
import entity.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/evaluation")
public class EvaluationController {
    @Autowired
    private EvaluationService evaluationService;

    @PostMapping("/add")
    public Result add(@RequestBody Evaluation evaluation){
        String username = TokenDecode.getUserInfo().get("username");
        evaluation.setUsername(username);
        evaluation.setReceiptTime(new Date());
        evaluation.setEvaluationTime(new Date());
        evaluation.setStatus("1");
        evaluationService.add(evaluation);
        return new Result(true, StatusCode.OK,"评价成功");
    }

    @GetMapping("/{skuId}")
    public Result findBySkuId(@PathVariable("skuId") Long skuId){
        Evaluation evaluation = evaluationService.findBySkuId(skuId);
        return new Result(true,StatusCode.OK,"查询成功",evaluation);
    }
}
