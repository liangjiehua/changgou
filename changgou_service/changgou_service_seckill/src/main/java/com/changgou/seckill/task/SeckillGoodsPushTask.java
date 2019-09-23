package com.changgou.seckill.task;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import entity.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Steven
 * @version 1.0
 * @description com.changgou.seckill.task
 * @date 2019-9-18
 */
@Component
public class SeckillGoodsPushTask {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    //当前方法是定时器:30/次
    /**
     * cron：表达式是一个字符串，教案详细介绍，案例：cron = "1/2 * * * * *"
     * zone：时区接收一个java.util.TimeZone#ID，
     *        cron表达式会基于该时区解析。默认是一个空字符串，即取服务器所在地的时区
     *
     * 注意：以下参数设置的单位都是毫秒
     * fixedDelay：上一次执行完毕时间点之后多长时间再执行，案例:fixedDelay = 1000
     * fixedDelayString:与 fixedDelay 意思相同，只是使用字符串的形式。唯一不同的是支持占位符,
     *      点位符中内容可以来源属性文件，案例:fixedDelayString = "${task.work.delay}"
     * fixedRate：上一次开始执行时间点之后多长时间再执行，案例:fixedRate = 2000
     * fixedRateString：与fixedRate意思相同，类似上面的fixedDelayString
     * initialDelay：第一次延迟多长时间后再执行，案例:@Scheduled(initialDelay = 1000,fixedDelay=2000)
     * initialDelayString：与initialDelay意思相同，类似上面的fixedDelayString
     */
    @Scheduled(cron = "0/5 * * * * *")
    public void loadGoodsPushRedis(){
        //System.out.println("定时器被调用了....");
        //查询所有时间段-5组
        List<Date> dateMenus = DateUtil.getDateMenus();
        //查询在当前时间段的所有商品，压入Redis
        Example example = null;
        for (Date date : dateMenus) {
            //当前时间
            String nowDate = DateUtil.data2str(date, "yyyyMMddHH");
            example = new Example(SeckillGoods.class);
            //组装查询条件
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("status", "1");  //审核通过的状态
            criteria.andGreaterThan("stockCount", 0);  //有库存
            //startTime < 当前查询的时间
            criteria.andLessThanOrEqualTo("startTime", date);
            //结束时间表 > 当前查询时间
            criteria.andGreaterThan("endTime",date);
            //[goodsId]--要排除的商品列表
            //where id not in(2,2,3,4,4)
            Set ids = redisTemplate.boundHashOps("SeckillGoods_" + nowDate).keys();
            if(ids != null && ids.size() > 0){
                criteria.andNotIn("id", ids);
            }

            List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
            //System.out.println("导入了时间段为:" + nowDate + "商品数量为：" + seckillGoodsList.size());

            //导入Redis
            for (SeckillGoods seckillGoods : seckillGoodsList) {
                /**
                 * key:SeckillGoods_2019091816 value:{goodsId:{goods}}
                 */
                redisTemplate.boundHashOps("SeckillGoods_" + nowDate).put(seckillGoods.getId(), seckillGoods);

                //把商品的库存信息，压入队列
                /*for (int i = 0; i < seckillGoods.getStockCount(); i++) {
                    redisTemplate.boundListOps("SeckillGoodsCountList_" + seckillGoods.getId()).leftPush(i);
                }*/
                //一次性导入
                redisTemplate.boundListOps("SeckillGoodsCountList_" + seckillGoods.getId()).leftPushAll(pushIds(seckillGoods.getStockCount(), seckillGoods.getId()));

                //把商品库存信息存一个hash:{goodsId:StockCount}
                redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillGoods.getId(),seckillGoods.getStockCount());
            }
        }

    }

    /***
     * 将商品ID存入到数组中
     * @param len:长度
     * @param id :值
     */
    public Long[] pushIds(int len,Long id){
        Long[] ids = new Long[len];
        for (int i = 0; i <ids.length ; i++) {
            ids[i]=id;
        }
        return ids;
    }

}
