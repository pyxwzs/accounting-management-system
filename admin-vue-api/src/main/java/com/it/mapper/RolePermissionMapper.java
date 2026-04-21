package com.it.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.it.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限信息数据访问层
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

}
