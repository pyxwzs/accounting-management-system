package com.it.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.it.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限信息数据访问层
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

}
