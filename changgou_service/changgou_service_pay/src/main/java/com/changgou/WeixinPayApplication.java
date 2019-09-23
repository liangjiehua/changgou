package com.changgou;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableEurekaClient
public class WeixinPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeixinPayApplication.class,args);
    }

    @Autowired
    private Environment env;

    //创建交换机
    @Bean
    public DirectExchange orderExchange(){
        return new DirectExchange(env.getProperty("mq.pay.exchange.order"), true, false);
    }

    //创建队列
    @Bean
    public Queue orderQueue(){
        return new Queue(env.getProperty("mq.pay.queue.order"));
    }

    //绑定交换机与队列
    @Bean
    public Binding bindingExchangeQueue(@Qualifier("orderExchange") DirectExchange orderExchange,
                                        Queue orderQueue){
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(env.getProperty("mq.pay.routing.key"));
    }

    //创建队列
    @Bean
    public Queue seckillQueue(){
        return new Queue(env.getProperty("mq.pay.queue.seckillorder"));
    }

    //绑定交换机与队列
    @Bean
    public Binding bindingExchangeSeckillQueue(DirectExchange orderExchange,
                                        Queue seckillQueue){
        return BindingBuilder.bind(seckillQueue).to(orderExchange).with(env.getProperty("mq.pay.routing.seckillkey"));
    }
}
