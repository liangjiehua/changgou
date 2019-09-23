package com.changgou.seckill.task;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.utils.SeckillStatus;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Steven
 * @version 1.0
 * @description com.changgou.seckill.task
 * @date 2019-9-18
 */
@Component
public class MultiThreadingCreateOrder {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    //@Async，表示当前方法开启新线程执行
    @Async
    public void createOrder(){
        //模拟测试时间要几秒
        /*try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        //右取
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps("SeckillOrderQueue").rightPop();
        //便于测试我们这里的参数先写死
        //时间区间
        String time = seckillStatus.getTime();
        //用户登录名
        String username=seckillStatus.getUsername();
        //用户抢购商品
        Long id = seckillStatus.getGoodsId();

        //超卖方式一：队列-只能处理超卖问题，不能解决正确库存数显示问题
        /*Object sgoods = redisTemplate.boundListOps("SeckillGoodsCountList_" + seckillStatus.getGoodsId()).rightPop();
        //如果队列中读不到数据，说明库存没了
        if (sgoods == null) {
            //清理排队标示
            redisTemplate.boundHashOps("UserQueueCount").delete(seckillStatus.getUsername());
            //清理抢单标示
            redisTemplate.boundHashOps("UserQueueStatus").delete(seckillStatus.getUsername());
            throw new RuntimeException("没货了");
        }*/

        //超卖方式二:自减
        Long count = redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillStatus.getGoodsId(), -1);
        if(count <= 0){
            seckillStatus.setStatus(6);  //库存不足
            redisTemplate.boundHashOps("UserQueueStatus").put(username,seckillStatus);
            throw new RuntimeException("你来晚了一步，当前商品已被抢购一空！");
        }

        //1、从缓存查询商品信息
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_" + time).get(id);
        //2、没货提示用户-throw Exception

        //3、有货-组装订单基本信息
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setSeckillId(id);
        seckillOrder.setMoney(seckillGoods.getCostPrice());
        seckillOrder.setUserId(username);
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setStatus("0");  //未支付订单
        //4、保存订单到redis
        redisTemplate.boundHashOps("SeckillOrder").put(username, seckillOrder);
        //5、扣减库存
        seckillGoods.setStockCount(count.intValue());

        //6、查看扣减后，库存还有没
        //没有货了，把商品信息同步到mysql-同时删除redis中当前的商品信息
        if(count <= 0){
            seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
            //删除已经卖完的商品
            //redisTemplate.boundHashOps("SeckillGoods_" + time).delete(id);
        }else{
            //有货，直接更新redis库存信息
            redisTemplate.boundHashOps("SeckillGoods_" + time).put(id,seckillGoods);
        }

        //修改排队信息为：等待支付
        seckillStatus.setStatus(2);  //等待支付
        seckillStatus.setMoney(new Float(seckillOrder.getMoney()));
        seckillStatus.setOrderId(seckillOrder.getId());
        redisTemplate.boundHashOps("UserQueueStatus").put(username,seckillStatus);
    }
}
