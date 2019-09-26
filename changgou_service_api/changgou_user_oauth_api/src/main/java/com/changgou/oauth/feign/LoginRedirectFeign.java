package com.changgou.oauth.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-auth")
@RequestMapping("/oauth")
public interface LoginRedirectFeign {


    /**
     * 跳转登录页面
     * @param
     * @param
     * @return
     */
    @RequestMapping("tologin")
    public String tologin();
}
