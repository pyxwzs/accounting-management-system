package com.it.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.it.entity.Menu;
import com.it.entity.RolePermission;
import com.it.mapper.MenuMapper;
import com.it.mapper.RolePermissionMapper;
import com.it.service.MenuService;
import com.it.util.DateUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 〈菜单service实现类〉<br>
 *
 * @author
 */
@Service
public class MenuServiceImpl implements MenuService {
    @Resource
    private MenuMapper MenuMapper;
    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public List<Menu> selectPage() {
        QueryWrapper<Menu> searchInfo = new QueryWrapper<>();
        searchInfo.orderBy(true, true, "num");
        List<Menu> selectList = MenuMapper.selectList(searchInfo);
        return selectList;
    }

    @Override
    public List<Menu> selectPageByUserId(String userId) {
        if ("".equals(userId) && userId == null) {
            return new ArrayList<>();
        }
        List<Menu> allMenu = MenuMapper.getMenuInfoByUserId(userId);
        return allMenu;
    }

    @Override
    public Menu selectByPrimaryKey(String id) {
        return MenuMapper.selectById(id);
    }

    @Override
    public boolean insert(Menu entity) {
        entity.setCreate_time(DateUtil.getNowDateSS());
        Integer result = MenuMapper.insert(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateByPrimaryKey(Menu entity) {
        Integer result = MenuMapper.updateById(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByPrimaryKey(String id) {
        Integer result = MenuMapper.deleteById(id);
        if (result > 0) {
            //删除菜单要删除权限关联表中对应的数据
            QueryWrapper<RolePermission> searchInfo = new QueryWrapper<>();
            searchInfo.eq("item_id", id);
            rolePermissionMapper.delete(searchInfo);
            return true;
        }
        return false;
    }
}