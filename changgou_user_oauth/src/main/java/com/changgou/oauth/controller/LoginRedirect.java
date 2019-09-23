package com.changgou.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Steven
 * @version 1.0
 * @description com.changgou.oauth.controller
 * @date 2019-9-15
 */
@Controller
@RequestMapping("oauth")
public class LoginRedirect {

    @RequestMapping("login")
    public String login(@RequestParam(value = "FROM",required = false) String from, Model model){
        model.addAttribute("from", from);
        return "login";
    }
}
