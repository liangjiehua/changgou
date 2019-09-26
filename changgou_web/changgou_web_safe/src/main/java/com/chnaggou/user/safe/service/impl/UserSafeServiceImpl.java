package com.chnaggou.user.safe.service.impl;

import com.aliyuncs.exceptions.ClientException;
import com.changgou.user.feign.UserFeign;
import com.changgou.user.pojo.User;
import com.chnaggou.user.safe.service.UserSafeService;
import entity.Result;
import entity.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;


@Service
public class UserSafeServiceImpl implements UserSafeService {

    @Autowired
    private UserFeign userFeign;


    /**
     * 登录后修改密码
     *
     * @param nickname 用户名（主键）
     * @param pw       （密码）
     * @param cpw      （确认密码）
     */
    @Override
    public boolean changepw(String nickname, String pw, String cpw) {

        if (! StringUtils.isEmpty(nickname) && !StringUtils.isEmpty(pw) && !StringUtils.isEmpty(pw)){
            if ( pw.equals(cpw)){
                //调用UserFeign修改密码
                Result result = userFeign.changePassword(nickname, pw);

                return (int)result.getData() > 0;
            }
        }
        return false;
    }


    /**
     * 重置密码
     * @param map
     */
    @Override
    public void restPw(Map<String, String> map) {
        //校验验证码
        String sCode = map.get("sCode");
        if (StringUtils.isEmpty(sCode) || !sCode.equals(map.get("code"))){
            throw new RuntimeException("验证码输入有误，请重新输入");
        }else {
            String phone = map.get("phone");
            Result<User> result = userFeign.findByPhone(phone);
            if (result != null){
                String username = result.getData().getUsername();
                if (!StringUtils.isEmpty(username)){
                    //修改密码
                    userFeign.changePw(username,map.get("pw"));
                }else {
                    throw new RuntimeException("改手机号未绑定任何账户，请注册！");
                }
            }
        }
    }




    /**
     * 发送验证码
     * @param phone
     * @param request
     * @return
     */
    @Override
    public String sendCode(String phone, HttpServletRequest request) {
        //根据手机号查询数据库，为空的话非会员，先注册
        Result<User> result = userFeign.findByPhone(phone);
        if (result.getData() == null || result.getData().getUsername() == null){
            throw new RuntimeException("该号码尚未注册，请先注册！");
        }
        try {
            String code = SMSUtils.sendMessage(phone);
            HttpSession session = request.getSession();
            session.setAttribute(phone,code);
            session.setMaxInactiveInterval(10*60);
            System.out.println("发送的验证码为：" + code);
            return code;
        } catch (ClientException e) {
            e.printStackTrace();
            return null;
        }
    }

}
