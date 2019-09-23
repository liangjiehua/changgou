package com.changgou.order.service.impl;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Steven
 * @version 1.0
 * @description com.changgou.order.service.impl
 * @date 2019-9-14
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private SpuFeign spuFeign;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void add(Integer num, Long skuId, String username) {
        //如果传入的数量少于
        if(num < 1){
            redisTemplate.boundHashOps("Cart_" + username).delete(skuId);
            return;
        }
        //1.根据skuId查询sku信息
        Sku sku = skuFeign.findById(skuId).getData();
        if(sku != null){
            //2.从sku中获取spuID去查询spu信息
            Spu spu = spuFeign.findById(sku.getSpuId()).getData();

            //构建购物车商品对象
            OrderItem orderItem = new OrderItem();
            orderItem.setCategoryId1(spu.getCategory1Id());
            orderItem.setCategoryId2(spu.getCategory2Id());
            orderItem.setCategoryId3(spu.getCategory3Id());
            orderItem.setSpuId(sku.getSpuId());
            orderItem.setSkuId(skuId);
            orderItem.setName(sku.getName());
            orderItem.setPrice(sku.getPrice());
            orderItem.setNum(num);
            orderItem.setMoney(num * orderItem.getPrice());       //单价*数量
            orderItem.setPayMoney(num * orderItem.getPrice());    //实付金额
            orderItem.setImage(sku.getImage());
            orderItem.setWeight(sku.getWeight() * num);           //重量=单个重量*数量

            //3.添加一条记录到redis
            redisTemplate.boundHashOps("Cart_" + username).put(skuId, orderItem);
        }
    }

    @Override
    public List<OrderItem> list(String username) {
        List<OrderItem> values = redisTemplate.boundHashOps("Cart_" + username).values();
        return values;
    }
}
