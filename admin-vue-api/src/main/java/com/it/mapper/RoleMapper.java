package com.it.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.it.entity.Role;
import org.apache.ibatis.annotations.Mapper;

@Mapper
/**
 * 角色数据访问层
 */
public interface RoleMapper extends BaseMapper<Role> {


}
