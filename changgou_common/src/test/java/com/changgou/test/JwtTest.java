package com.changgou.test;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Steven
 * @version 1.0
 * @description com.changgou.test
 * @date 2019-9-11
 */
public class JwtTest {

    @Test
    public void testCreateJwt(){
        //1、创建Jwt构建器-jwtBuilder = Jwts.builder()
        JwtBuilder jwtBuilder = Jwts.builder();
        //2、设置唯一编号-setId
        jwtBuilder.setId("007");
        //3、设置主题，可以是JSON数据-setSubject()
        jwtBuilder.setSubject("这是一个测试主题");
        //4、设置签发日期-setIssuedAt
        jwtBuilder.setIssuedAt(new Date());
        //5、设置签发人-setIssuer
        jwtBuilder.setIssuer("itheima.steven");

        //设置有效时间-30秒
        /*Date exp = new Date(System.currentTimeMillis() + 30000);
        jwtBuilder.setExpiration(exp);*/

        //自定义claims
        Map<String, Object> user = new HashMap<>();
        user.put("name", "steven");
        user.put("age", "18");
        user.put("address", "深圳市.黑马程序员");
        //此处要用add,表示追加内容
        jwtBuilder.addClaims(user);

        //6、设置签证-signWith(使用加密算法,密钥)
        jwtBuilder.signWith(SignatureAlgorithm.HS256, "www.ithema.steven.key");
        //7、生成令牌-compact()
        String token = jwtBuilder.compact();
        //8、输出结果
        System.out.println(token);
    }

    @Test
    public void testParseJwt(){
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIwMDciLCJzdWIiOiLov5nmmK_kuIDkuKrmtYvor5XkuLvpopgiLCJpYXQiOjE1NjgxOTU5OTMsImlzcyI6Iml0aGVpbWEuc3RldmVuIiwiYWRkcmVzcyI6Iua3seWcs-W4gi7pu5HpqaznqIvluo_lkZgiLCJuYW1lIjoic3RldmVuIiwiYWdlIjoiMTgifQ.s5EyfSPA2oN-p8qM-7LYKOVPNB6uikkCpYJZDB7Nxlo";
        JwtParser jwtParser = Jwts.parser();
        //设置密钥
        jwtParser.setSigningKey("www.ithema.steven.key");
        //解密
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        //输出结果
        System.out.println(claims);
    }
}
