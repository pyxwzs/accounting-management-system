package com.it.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.entity.User;
import com.it.vo.resp.LoginRespVO;

import java.util.List;

public interface UserService {

    /**
     * 通过UserName查询到User对象
     *
     * @param username
     * @return
     */
    User getUserByUsername(String username);

    /**
     * 分页查询
     *
     * @param entity
     * @param page
     * @param limit
     * @return
     */
    IPage<User> selectPage(User entity, int page, int limit);

    /**
     * 根据主键id查询
     *
     * @param id
     * @return
     */
    User selectByPrimaryKey(String id);

    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    boolean insert(User user);

    /**
     * 修改用户
     *
     * @param user
     * @return
     */
    boolean updateByPrimaryKey(User user);

    /**
     * 重置密码
     *
     * @param id
     * @return
     */
    boolean resetPwa(String id);

    boolean resetPwa(String username, String newPwd);

    /**
     * 修改密码
     *
     * @param oldPwd
     * @param newPwd
     * @return
     */
    boolean updatePwa(String oldPwd, String newPwd, String userId);


    /**
     * 验证密码
     * @param password
     * @return
     */
    boolean checkPassword(String password,String userId);
    /**
     * 删除,单个删除批量删除通用
     *
     * @param ids
     * @return
     */
    boolean deleteByPrimaryKey(List<String> ids);

    /**
     * 获取用户集合
     *
     * @return
     */
    List<User> selectList();

    /**
     * 获取用户集合
     *
     * @return
     */
    List<User> selectList(String roleId);

    /**
     * 获取用户集合
     *
     * @return
     */
    List<User> selectList(User entity);

    /**
     * 后台登录
     *
     * @return
     */
    LoginRespVO login(String username, String password);

    /**
     * 前台登录登录
     *
     * @return
     */
    LoginRespVO loginFront(String username, String password);
}
