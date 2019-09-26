package com.changgou.user.service;

import com.changgou.user.pojo.User;
import com.github.pagehelper.PageInfo;

import java.util.List;

/****
 * @Author:sz.itheima
 * @Description:User业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface UserService {

    /***
     * User多条件分页查询
     * @param user
     * @param page
     * @param size
     * @return
     */
    PageInfo<User> findPage(User user, int page, int size);

    /***
     * User分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<User> findPage(int page, int size);

    /***
     * User多条件搜索方法
     * @param user
     * @return
     */
    List<User> findList(User user);

    /***
     * 删除User
     * @param id
     */
    void delete(String id);

    /***
     * 修改User数据
     * @param user
     */
    void update(User user);

    /***
     * 新增User
     * @param user
     */
    void add(User user);

    /**
     * 根据ID查询User
     * @param id
     * @return
     */
     User findById(String id);

    /***
     * 查询所有User
     * @return
     */
    List<User> findAll();

    /***
     * 添加用户积分
     * @param username
     * @param pint
     * @return
     */
    int addUserPoints(String username,Integer pint);

    /**
     * 修改密码
     * @param nickname
     * @param pw
     * @return
     */
    int changepw(String nickname, String pw);

    /**
     * 根据手机号查找用户
     * @param phone
     * @return
     */
    User findByPhone(String phone);


    /**
     * 重置密码
     * @param username
     * @param pw
     */
    void restPw(String username, String pw);
}
