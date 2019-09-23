package com.changgou.evaluation.dao;

import com.changgou.evaluation.pojo.Evaluation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface EvaluationMapper extends Mapper<Evaluation> {

    @Select("select * from tb_evaluation where sku_id = #{skuId}")
    Evaluation findBySkuId(@Param("skuId") Long skuId);
}
