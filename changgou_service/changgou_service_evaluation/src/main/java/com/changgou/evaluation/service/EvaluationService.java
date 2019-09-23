package com.changgou.evaluation.service;

import com.changgou.evaluation.pojo.Evaluation;

public interface EvaluationService {

    void add(Evaluation evaluation);

    Evaluation findBySkuId(Long skuId);
}
