package com.changgou.oauth.interceptor;

import com.changgou.oauth.util.JwtToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 当我们调用所有的Feign之前会先执行我们这个拦截器的apply方法
 * @author Steven
 * @version 1.0
 * @description com.changgou.oauth.interceptor
 * @date 2019-9-14
 */
@Configuration
public class FeignOauth2RequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //创建管理员信息
        String token = "bearer " + JwtToken.adminJwt();
        //把令牌信息设置到头信息中
        requestTemplate.header("Authorization", token);

        //使用RequestContextHolder工具获取request相关变量
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            //取出request
            HttpServletRequest request = attributes.getRequest();
            //获取所有头文件信息的key
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    //头文件的key
                    String name = headerNames.nextElement();
                    //头文件的value
                    String values = request.getHeader(name);
                    //将令牌数据添加到头文件中
                    requestTemplate.header(name, values);
                }
            }
        }
    }
}
