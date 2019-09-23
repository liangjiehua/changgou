package com.changgou.pay.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeixinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.Result;
import entity.StatusCode;
import entity.TokenDecode;
import org.apache.commons.io.IOUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Steven
 * @version 1.0
 * @description com.changgou.pay.controller
 * @date 2019-9-16
 */
@RestController
@RequestMapping("/weixin/pay")
public class WeixinPayController {
    @Autowired
    private WeixinPayService weixinPayService;

    /**
     * 生成微信支付二维码
     * * @param paramMap {
     *          out_trade_no 订单号,
     *          total_fee 金额(分),
     *          exchange 交换机,
     *          routingKey 路由Key
     * }
     *
     * @return
     */
    @RequestMapping(value = "/create/native")
    public Result<Map> createNative(@RequestParam Map<String,String> paramMap){
        //paramMap.put("username", TokenDecode.getUserInfo().get("username"));
        paramMap.put("username", "zhangsan");
        Map map = weixinPayService.createNative(paramMap);
        return new Result<Map>(true, StatusCode.OK, "创建二维码成功！", map);
    }

    @RequestMapping("/status/query")
    public Result<Map> queryPayStatus(String out_trade_no){
        /*Map map = null;
        while (true){
            map = weixinPayService.queryPayStatus(out_trade_no);

            if(true){
                break;
            }

            //3秒发起一次查询
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
        Map map = weixinPayService.queryPayStatus(out_trade_no);
        return new Result<Map>(true, StatusCode.OK, "查询二维码成功！", map);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Value("${mq.pay.exchange.order}")
    private String exchange;
    @Value("${mq.pay.queue.order}")
    private String queue;
    @Value("${mq.pay.routing.key}")
    private String routing;

    /***
     * 支付回调
     * 支付完成后，微信会把相关支付结果及用户信息通过数据流的形式发送给商户，
     * 商户需要接收处理，并按文档规范返回应答
     * @param request
     * @return
     */
    @RequestMapping(value = "/notify/url")
    public String notifyUrl(HttpServletRequest request){
        try {
            //1.读取输入流
            ServletInputStream inputStream = request.getInputStream();
            //2.使用Apache IOUtils把输入转换成字符
            String xml = IOUtils.toString(inputStream, "UTF-8");
            //3.解析结果-xml
            Map<String, String> map = WXPayUtil.xmlToMap(xml);
            System.out.println(map);

            //读取附加参数
            String attach = map.get("attach");
            Map<String,String> attachMap = JSON.parseObject(attach, Map.class);
            exchange = attachMap.get("exchange");
            routing = attachMap.get("routingKey");
            //把消息发送了mq中
            rabbitTemplate.convertAndSend(exchange, routing,JSON.toJSONString(map));

            //3.包装响应数据设置
            Map respMap = new HashMap();
            respMap.put("return_code","SUCCESS");
            respMap.put("return_msg","OK");
            //返回微信需要的参数
            return WXPayUtil.mapToXml(respMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("createSeckillQueue")
    public String createSeckillQueue(){
        rabbitTemplate.convertAndSend(exchange, "queue.seckillorder", "{'flag':'true'}");
        return "ok";
    }

}
