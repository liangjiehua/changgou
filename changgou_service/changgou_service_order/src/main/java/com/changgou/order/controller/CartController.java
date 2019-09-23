package com.changgou.order.controller;

import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import entity.StatusCode;
import entity.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Security;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /***
     * 加入购物车
     * @param num:购买的数量
     * @param id：购买的商品(SKU)ID
     * @return
     */
    @RequestMapping(value = "/add")
    public Result add(Integer num, Long id){
        //用户名
        //String username="szitheima";
        String username = TokenDecode.getUserInfo().get("username");
        //将商品加入购物车
        cartService.add(num,id,username);
        return new Result(true, StatusCode.OK,"加入购物车成功！");
    }

    /***
     * 查询购物车列表
     */
    @RequestMapping(value = "list")
    public Result list(){
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        //用户名
        //String username="szitheima";
        String username = TokenDecode.getUserInfo().get("username");
        //将商品加入购物车
        List<OrderItem> orderItems = cartService.list(username);
        return new Result(true, StatusCode.OK,"查询购物车成功！",orderItems);
    }
}
