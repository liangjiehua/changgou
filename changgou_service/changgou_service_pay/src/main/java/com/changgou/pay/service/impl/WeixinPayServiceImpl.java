package com.changgou.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeixinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Steven
 * @version 1.0
 * @description com.changgou.pay.service.impl
 * @date 2019-9-16
 */
@Service
public class WeixinPayServiceImpl implements WeixinPayService {
    @Value("${weixin.appid}")
    private String appid;
    @Value("${weixin.partner}")
    private String partner;
    @Value("${weixin.notifyurl}")
    private String notifyurl;
    @Value("${weixin.partnerkey}")
    private String partnerkey;

    @Override
    public Map createNative(Map<String,String> param) {
        Map map = new HashMap();
        try {
            //1.组装下单接口需要的参数
            String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("appid", appid);  //公众号id
            paramMap.put("mch_id", partner);  //商户号
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());  //随机字符串
            paramMap.put("body", "畅购");  //商品描述，我们扫码后，在微信页面看到的信息
            paramMap.put("out_trade_no", param.get("out_trade_no"));  //商户订单号
            paramMap.put("total_fee", param.get("total_fee"));  //支付金额
            paramMap.put("spbill_create_ip", "127.0.0.1");  //终端ip
            paramMap.put("notify_url", notifyurl);  //回调地址
            paramMap.put("trade_type", "NATIVE");  //交易类型 NATIVE 扫码支付

            //附加参数--attach:{}
            Map<String, String> attachMap = new HashMap<>();
            attachMap.put("exchange", param.get("exchange"));
            attachMap.put("routingKey", param.get("routingKey"));
            attachMap.put("username", param.get("username"));
            paramMap.put("attach", JSON.toJSONString(attachMap));

            //2.把参数转xml-生成带签名的xml
            String xmlParam = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            System.out.println("正在调起微信统一下单接口，入参为：" + xmlParam);
            //3.通过HttpClient发起请求，得到xml
            HttpClient client = new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            String xmlResult = client.getContent();
            System.out.println("调起微信统一下单接口成功，响应参数为：" + xmlResult);
            //4.解析xml为Map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlResult);
            //包装返回参数
            map.put("code_url", resultMap.get("code_url"));
            map.put("out_trade_no", param.get("out_trade_no"));
            map.put("total_fee", param.get("total_fee"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        try {
            //1.组装下单接口需要的参数
            String url = "https://api.mch.weixin.qq.com/pay/orderquery";
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("appid", appid);  //公众号id
            paramMap.put("mch_id", partner);  //商户号
            paramMap.put("out_trade_no", out_trade_no);  //商户订单号
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());  //随机字符串
            //2.把参数转xml-生成带签名的xml
            String xmlParam = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            System.out.println("正在调起微信查询订单接口，入参为：" + xmlParam);
            //3.通过HttpClient发起请求，得到xml
            HttpClient client = new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            String xmlResult = client.getContent();
            System.out.println("调起微信查询订单接口成功，响应参数为：" + xmlResult);
            //4.解析xml为Map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlResult);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
