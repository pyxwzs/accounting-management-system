package com.it.service;

import com.it.entity.Role;

import java.util.List;

public interface RoleService {
    /**
     * 角色列表查询
     *
     * @param
     * @return
     */
    List<Role> selectPage();

    /**
     * 根据主键id查询
     *
     * @param id
     * @return
     */
    Role selectByPrimaryKey(String id);

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean insert(Role entity);

    /**
     * 修改
     *
     * @param entity
     * @return
     */
    boolean updateByPrimaryKey(Role entity);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    boolean deleteByPrimaryKey(String id);


    /**
     * 得到所有角色,没有条件
     *
     * @return
     */
    List<Role> getRoleList();

}
