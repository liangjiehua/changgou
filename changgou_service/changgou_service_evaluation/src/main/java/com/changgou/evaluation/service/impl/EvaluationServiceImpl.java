package com.changgou.evaluation.service.impl;

import com.changgou.evaluation.dao.EvaluationMapper;
import com.changgou.evaluation.pojo.Evaluation;
import com.changgou.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EvaluationServiceImpl implements EvaluationService {
    @Autowired
    private EvaluationMapper evaluationMapper;

    @Override
    public void add(Evaluation evaluation) {
        evaluationMapper.insert(evaluation);
    }

    @Override
    public Evaluation findBySkuId(Long skuId) {
        Evaluation evaluation1 = new Evaluation();
        evaluation1.setSkuId(skuId);
        Evaluation evaluation = evaluationMapper.selectOne(evaluation1);
        return evaluation;
    }
}
