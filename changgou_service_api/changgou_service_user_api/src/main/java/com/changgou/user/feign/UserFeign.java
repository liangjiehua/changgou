package com.changgou.user.feign;

import com.changgou.user.pojo.User;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Steven
 * @version 1.0
 * @description com.changgou.user.feign
 * @date 2019-9-14
 */
@FeignClient(name = "user")
@RequestMapping("user")
public interface UserFeign {

    @GetMapping("load/{id}")
    public Result<User> findById(@PathVariable(value = "id") String id);

    @GetMapping(value = "/points/add")
    public Result addPoints(@RequestParam(value = "points") Integer points);

    /**
     * 修改密码
     * @param nickname
     * @param pw
     * @param
     * @return
     */
    @GetMapping("/changepw/{nickname}/{pw}")
    Result changePassword(@PathVariable("nickname") String nickname,@PathVariable("pw") String pw);


    /**
     * 根据手机号查找用户
     * @param phone
     * @return
     */
    @GetMapping("/changepw/{phone}")
    Result<User> findByPhone(@PathVariable("phone") String phone);


    /**
     * 重置密码
     * @param username
     * @param pw
     * @return
     */
    @RequestMapping("/restPw/{username}/{pw}")
    Result changePw(@PathVariable("username") String username,@PathVariable("pw") String pw);
}
