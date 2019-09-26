package com.chnaggou.user.safe.controller;


import com.chnaggou.user.safe.service.UserSafeService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/userSafe")
public class UserSafeController {

    @Autowired
    private UserSafeService userSafeService;


    @RequestMapping("/center")
    public String center(){
        return "seckillsetting-safe";
    }


    /**
     * 登录状态下修改密码
     *
     * @param pw
     * @param cpw
     */
    @RequestMapping("/changepw")
    @ResponseBody
    public Result success(String username, String pw, String cpw){
        boolean res = userSafeService.changepw(username, pw, cpw);
        return new Result(res, StatusCode.OK,"密码修改成功",res);
    }

    /**
     * 返回找回密码页面
     * @return
     */
    @RequestMapping("/rest")
    public String toFindPw(){
        return "find_password";
    }


    /**
     * 重置密码
     * @param map
     * @param request
     * @return
     */
    @RequestMapping("/restPw")
    @ResponseBody
    public Result restPw(@RequestBody Map<String,String> map, HttpServletRequest request){
        HttpSession session = request.getSession();
        String sCode = (String) session.getAttribute(map.get("phone"));
        map.put("sCode",sCode);
        //销毁session
        session.setMaxInactiveInterval(0);
        try {
            userSafeService.restPw(map);
            return new Result(true,StatusCode.OK,"密码修改成功");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false,StatusCode.ERROR,e.getMessage());
        }
    }

    /**
     * 发送验证码
     * @param phone
     * @param request
     * @return
     */
    @RequestMapping("/code")
    @ResponseBody
    public Result sendCode(String phone,HttpServletRequest request){
        try {
            String code = userSafeService.sendCode(phone,request);
            if (code != null){
                return new Result(true,StatusCode.OK,"验证码发送成功");
            }else {
                return new Result(false,StatusCode.ERROR,"验证码发送失败，请稍后再试！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,StatusCode.ERROR,e.getMessage());
        }
    }


    @RequestMapping("/register")
    public String register(){
        return "register";
    }
}
