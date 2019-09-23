package com.changgou.order.consumer;

import com.alibaba.fastjson.JSON;
import com.changgou.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 支付消息监听器
 * @author Steven
 * @version 1.0
 * @description com.changgou.order.consumer
 * @date 2019-9-16
 */
@Component
public class OrderPayMessageListener {
    @Autowired
    private OrderService orderService;

    //监听队列为:queue.order的消息
    @RabbitListener(queues = "${mq.pay.queue.order}")
    public void payListener(String json){
        //1.把消息转换Map
        Map<String,String> map = JSON.parseObject(json, Map.class);
        //2.读取支付状态--处理逻辑
        String return_code = map.get("return_code");
        //如果返回状态码为成功
        if("success".equalsIgnoreCase(return_code)){
            String out_trade_no = map.get("out_trade_no");  //订单号
            String result_code = map.get("result_code");  //业务结果
            String transaction_id = map.get("transaction_id");  //交易流水号-微信
            //如果成功支付
            if("success".equalsIgnoreCase(result_code)){
                System.out.println("订单支付成功，订单号为：" + out_trade_no);
                //修改订单状态为已支付
                orderService.updateStatus(out_trade_no,transaction_id);
            }else{
                //删除订单-还原库存
                System.out.println("订单支付失败，订单号为：" + out_trade_no);
                orderService.deleteOrder(out_trade_no);
            }
        }

    }
}
