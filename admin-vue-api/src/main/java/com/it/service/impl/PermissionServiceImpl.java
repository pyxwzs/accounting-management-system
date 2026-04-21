package com.it.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.it.entity.Permission;
import com.it.entity.RolePermission;
import com.it.mapper.PermissionMapper;
import com.it.mapper.RolePermissionMapper;
import com.it.service.PermissionService;
import com.it.util.CommonUtils;
import com.it.util.DateUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 〈权限service实现类〉<br>
 *
 * @author
 */
@Service
public class PermissionServiceImpl implements PermissionService {
    @Resource
    private PermissionMapper PermissionMapper;
    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public List<Permission> selectPage(Permission entity) {
        QueryWrapper<Permission> searchInfo = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getName())) {
            searchInfo.like("name", entity.getName());
        }
        if (CommonUtils.stringIsNotBlack(entity.getMark())) {
            searchInfo.like("mark", entity.getMark());
        }
        List<Permission> selectPage = PermissionMapper.selectList(searchInfo);
        return selectPage;
    }

    @Override
    public Permission selectByPrimaryKey(String id) {
        return PermissionMapper.selectById(id);
    }

    @Override
    public boolean insert(Permission entity) {
        entity.setCreate_time(DateUtil.getNowDateSS());
        Integer result = PermissionMapper.insert(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateByPrimaryKey(Permission entity) {
        Integer result = PermissionMapper.updateById(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByPrimaryKey(String id) {
        Integer result = PermissionMapper.deleteById(id);
        if (result > 0) {
            //删除操作权限要删除权限关联表中对应的数据
            QueryWrapper<RolePermission> searchInfo = new QueryWrapper<>();
            searchInfo.eq("item_id", id);
            rolePermissionMapper.delete(searchInfo);
            return true;
        }
        return false;
    }

    @Override
    public List<RolePermission> selectRolePermissionByType(String roleId, String type) {
        QueryWrapper<RolePermission> searchInfo = new QueryWrapper<>();
        searchInfo.eq("role_id", roleId);
        searchInfo.eq("type", type);
        List<RolePermission> selectPage = rolePermissionMapper.selectList(searchInfo);
        return selectPage;
    }
}