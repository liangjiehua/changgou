package com.chnaggou.user.safe.service;


import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 用户安全中心业务层接口
 */
public interface UserSafeService {

    /**
     * 登录后修改密码
     * @param username 用户名（主键）
     * @param pw （密码）
     * @param cpw （确认密码）
     */
    boolean changepw(String username, String pw , String cpw);

    /**
     * 重置密码
     * @param map
     */
    void restPw(Map<String, String> map);

    /**
     * 发送验证码
     * @param phone
     * @param request
     * @return
     */
    String sendCode(String phone, HttpServletRequest request);
}
