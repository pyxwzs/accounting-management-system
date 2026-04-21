package com.it.service;

import com.it.entity.Menu;

import java.util.List;

public interface MenuService {
    /**
     * 权限列表查询
     *
     * @param
     * @return
     */
    List<Menu> selectPage();

    /**
     * 根据角色查询菜单列表
     * @return
     */
    List<Menu> selectPageByUserId(String userId);
    /**
     * 根据主键id查询
     *
     * @param id
     * @return
     */
    Menu selectByPrimaryKey(String id);

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean insert(Menu entity);

    /**
     * 修改
     *
     * @param entity
     * @return
     */
    boolean updateByPrimaryKey(Menu entity);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    boolean deleteByPrimaryKey(String id);


}
