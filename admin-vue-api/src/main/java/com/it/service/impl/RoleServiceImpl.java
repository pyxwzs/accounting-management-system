package com.it.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.it.entity.Role;
import com.it.entity.RolePermission;
import com.it.mapper.RoleMapper;
import com.it.mapper.RolePermissionMapper;
import com.it.service.RoleService;
import com.it.util.AuthUserUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 〈角色service实现类〉<br>
 *
 * @author
 */
@Service
public class RoleServiceImpl implements RoleService {
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RolePermissionMapper rolePermissionMapper;
    @Resource
    private AuthUserUtils authUserUtils;

    @Override
    public List<Role> selectPage() {
        QueryWrapper<Role> searchInfo = new QueryWrapper<>();
//        if (CommonUtils.stringIsNotBlack(entity.getName())) {
//            searchInfo.eq("name", entity.getName());
//        }
        List<Role> selectList = roleMapper.selectList(searchInfo);
        for (Role record : selectList) {
            /**
             * 查询该角色下的权限信息
             */
            QueryWrapper<RolePermission> wrapper = new QueryWrapper<>();
            wrapper.eq("role_id", record.getId());
            List<RolePermission> rolePermissions = rolePermissionMapper.selectList(wrapper);
            record.setRolePermissionList(rolePermissions);
        }
        return selectList;
    }

    @Override
    public Role selectByPrimaryKey(String id) {
        Role role = roleMapper.selectById(id);
        /**
         * 查询该角色下的权限信息
         */
        QueryWrapper<RolePermission> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id", role.getId());
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(wrapper);
        role.setRolePermissionList(rolePermissions);
        return role;
    }

    @Override
    public boolean insert(Role entity) {
        entity.setCreate_name(authUserUtils.getLoginUserName());
        Integer result = roleMapper.insert(entity);
        if (result > 0) {
            List<String> controlsPermission = entity.getControlsPermission();
            if (controlsPermission != null) {
                for (String s : controlsPermission) {
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRole_id(entity.getId());
                    rolePermission.setItem_id(s);
                    rolePermission.setType("1");
                    rolePermissionMapper.insert(rolePermission);
                }
            }
            //========================================================
            List<String> menuPermission = entity.getMenuPermission();
            if (menuPermission != null) {
                for (String s : menuPermission) {
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRole_id(entity.getId());
                    rolePermission.setItem_id(s);
                    rolePermission.setType("0");
                    rolePermissionMapper.insert(rolePermission);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean updateByPrimaryKey(Role entity) {
        Integer result = roleMapper.updateById(entity);
        if (result > 0) {

            QueryWrapper<RolePermission> wrapper = new QueryWrapper<>();
            wrapper.eq("role_id", entity.getId());
            int delete = rolePermissionMapper.delete(wrapper);
            List<String> controlsPermission = entity.getControlsPermission();
            if (controlsPermission != null) {
                for (String s : controlsPermission) {
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRole_id(entity.getId());
                    rolePermission.setItem_id(s);
                    rolePermission.setType("1");
                    rolePermissionMapper.insert(rolePermission);
                }
            }
            //========================================================
            List<String> menuPermission = entity.getMenuPermission();
            if (menuPermission != null) {
                for (String s : menuPermission) {
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRole_id(entity.getId());
                    rolePermission.setItem_id(s);
                    rolePermission.setType("0");
                    rolePermissionMapper.insert(rolePermission);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByPrimaryKey(String id) {
        Integer result = roleMapper.deleteById(id);
        if (result > 0) {
            /**
             * 删除角色时,用同步删除该用户下的权限信息
             */
            QueryWrapper<RolePermission> wrapper = new QueryWrapper<>();
            wrapper.eq("role_id", id);
            int delete = rolePermissionMapper.delete(wrapper);

            return true;
        }
        return false;
    }

    @Override
    public List<Role> getRoleList() {
        QueryWrapper<Role> searchInfo = new QueryWrapper<>();
        List<Role> roleList = roleMapper.selectList(searchInfo);
        return roleList;
    }
}