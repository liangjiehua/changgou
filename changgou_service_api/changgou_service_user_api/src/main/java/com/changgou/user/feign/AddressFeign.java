package com.changgou.user.feign;

import com.changgou.user.pojo.Address;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Steven
 * @version 1.0
 * @description com.changgou.user.feign
 * @date 2019-9-15
 */
@FeignClient(name = "user")
@RequestMapping("address")
public interface AddressFeign {

    @GetMapping("/user/list")
    public Result<List<Address>> list();
}
