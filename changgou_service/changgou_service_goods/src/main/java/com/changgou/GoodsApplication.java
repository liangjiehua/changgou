package com.changgou;

import entity.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author Steven
 * @version 1.0
 * @description com.changgou.goods
 * @date 2019-8-31
 */
@SpringBootApplication
@EnableEurekaClient
//启动通用Mapper，需要指定dao包在哪
@MapperScan(basePackages = "com.changgou.goods.dao")
public class GoodsApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodsApplication.class, args);
    }

    @Bean
    public IdWorker getIdWorker(){
        return new IdWorker(0,0);
    }
}
