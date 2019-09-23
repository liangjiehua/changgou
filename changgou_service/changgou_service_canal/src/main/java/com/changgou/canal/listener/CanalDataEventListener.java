package com.changgou.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.*;

/**
 * 实现数据库增删改监听
 * @author Steven
 * @version 1.0
 * @description com.changgou.canal.listener
 * @date 2019-9-5
 */
@CanalEventListener
public class CanalDataEventListener {

    /**
     * @InsertListenPoint : 新增监听
     * CanalEntry.EventType:业务操作类型
     * CanalEntry.RowData 修改的数据
     */
    @InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType, CanalEntry.RowData rowData){
        //rowData.getBeforeColumnsList();  --修改前的后所有列
        //rowData.getAfterColumnsList()  --修改后的所有列
        System.out.println("---------新增监听--------");
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            System.out.println(column.getName() + ":" + column.getValue());
        }
    }

    /**
     * 修改监听
     * @UpdateListenPoint：更新监听
     * CanalEntry.EventType:变更操作类型
     * CanalEntry.RowData：此次变更的数据
     */
    @UpdateListenPoint
    public void onEventUpdate(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        //rowData.getBeforeColumnsList():数据变更前的内容
        //rowData.getAfterColumnsList()：数据变更后的内容
        System.out.println("---------更新监听--------");
        int i = 0;
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            //获取修改前数据
            CanalEntry.Column beforeColumns = rowData.getBeforeColumns(i);
            //如果修改了字段
            if(!beforeColumns.getValue().equals(column.getValue())) {
                System.out.print("修改了字段:" + column.getName() + "   ");
                System.out.println(beforeColumns.getValue() + "-->" + column.getValue());
            }
            i++;
        }
    }
    /**
     * 删除监听
     * @DeleteListenPoint：删除监听
     * CanalEntry.EventType:变更操作类型
     * CanalEntry.RowData：此次变更的数据
     */
    @DeleteListenPoint
    public void onEventDelete(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        //rowData.getBeforeColumnsList():数据变更前的内容
        //rowData.getAfterColumnsList()：数据变更后的内容
        System.out.println("---------删除监听--------");
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            System.out.println(column.getName() + ":" + column.getValue());
        }
    }

    /**
     * @ListenPoint : 自定义监听
     * destination:必须使用canal.properties配置文件中canal.destinations属性的名字
     * schema:监听的数据库
     * table:监听的表
     * eventType:操作的类型
     */
    @ListenPoint(destination = "example",schema = "changgou_content",table = "tb_content",eventType = {CanalEntry.EventType.DELETE})
    public void onEventCustomUpdate(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        //rowData.getBeforeColumnsList():数据变更前的内容
        //rowData.getAfterColumnsList()：数据变更后的内容
        System.out.println("---------自定义监听--------");
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            System.out.println(column.getName() + ":" + column.getValue());
        }
    }

}
