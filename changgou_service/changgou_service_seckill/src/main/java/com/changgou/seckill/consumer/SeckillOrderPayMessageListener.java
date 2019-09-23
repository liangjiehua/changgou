package com.changgou.seckill.consumer;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.service.SeckillOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 秒杀支付消息监听器
 * @author Steven
 * @version 1.0
 * @description com.changgou.order.consumer
 * @date 2019-9-16
 */
@Component
public class SeckillOrderPayMessageListener {

    @Autowired
    private SeckillOrderService seckillOrderService;

    //监听队列为:queue.order的消息
    @RabbitListener(queues = "${mq.pay.queue.seckillorder}")
    public void payListener(String json){
        //1.把消息转换Map
        Map<String,String> map = JSON.parseObject(json, Map.class);
        System.out.println("秒杀接收到消息，内容为:" + map);
        //2.读取支付状态--处理逻辑
        String return_code = map.get("return_code");
        //如果返回状态码为成功
        if("success".equalsIgnoreCase(return_code)){
            String out_trade_no = map.get("out_trade_no");  //订单号
            String result_code = map.get("result_code");  //业务结果
            String transaction_id = map.get("transaction_id");  //交易流水号-微信
            //读取附加参数
            Map<String,String> attachMap = JSON.parseObject(map.get("attach"), Map.class);
            String username = attachMap.get("username");
            //如果成功支付
            if("success".equalsIgnoreCase(result_code)){
                System.out.println("订单支付成功，订单号为：" + out_trade_no);
                //修改订单状态为已支付
                seckillOrderService.updatePayStatus(out_trade_no,transaction_id,username);
            }else{
                //删除订单-还原库存
                System.out.println("订单支付失败，订单号为：" + out_trade_no);
                seckillOrderService.closeOrder(username);
            }
        }

    }
}
