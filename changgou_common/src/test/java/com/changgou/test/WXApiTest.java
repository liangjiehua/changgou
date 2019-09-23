package com.changgou.test;

import com.github.wxpay.sdk.WXPayUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Steven
 * @version 1.0
 * @description com.changgou.test
 * @date 2019-9-16
 */
public class WXApiTest {

    @Test
    public void testWx() throws Exception {
        String nonceStr = WXPayUtil.generateNonceStr();
        System.out.println(nonceStr);
        //把map转成xml
        Map map = new HashMap();
        map.put("id", "001");
        map.put("name", "风清扬");
        String xml = WXPayUtil.generateSignedXml(map, "jslfajslfjsla");
        System.out.println(xml);
        Map<String, String> map1 = WXPayUtil.xmlToMap(xml);
        System.out.println(map1);
    }
}
