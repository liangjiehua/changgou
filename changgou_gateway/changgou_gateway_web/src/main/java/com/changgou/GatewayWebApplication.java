package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableEurekaClient
public class GatewayWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayWebApplication.class,args);
    }

    @Bean(name = "ipKeyResolver")
    public KeyResolver getKeyResolver(){
        return new KeyResolver() {
            /**
             * 实现限流规则
             * @param exchange
             * @return
             */
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                //获取请求客户端的ip
                String ip = exchange.getRequest().getRemoteAddress().getHostString();
                //使用ip限流
                return Mono.just(ip);
            }
        };
    }
}
