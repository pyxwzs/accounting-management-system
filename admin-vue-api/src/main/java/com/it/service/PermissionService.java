package com.it.service;

import com.it.entity.Permission;
import com.it.entity.RolePermission;

import java.util.List;

public interface PermissionService {
    /**
     * 权限列表查询
     *
     * @param entity
     * @return
     */
    List<Permission> selectPage(Permission entity);

    /**
     * 根据主键id查询
     *
     * @param id
     * @return
     */
    Permission selectByPrimaryKey(String id);

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean insert(Permission entity);

    /**
     * 修改
     *
     * @param entity
     * @return
     */
    boolean updateByPrimaryKey(Permission entity);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    boolean deleteByPrimaryKey(String id);

    /**
     * 根据用户角色和类型查询权限数据
     * @param roleId
     * @param type
     * @return
     */
    List<RolePermission> selectRolePermissionByType(String roleId,String type);
}
