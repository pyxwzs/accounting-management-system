package com.it.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 角色表，决定用户可以访问的权限
 */
@Data
@TableName("sys_role")
public class Role implements Serializable {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    @ApiModelProperty(value = "角色名称")
    private String name;
    @ApiModelProperty(value = "角色描述")
    private String description;
    @ApiModelProperty(value = "创建人")
    private String create_name;
    @TableField(exist = false)
    private List<RolePermission> RolePermissionList;
    @TableField(exist = false)
    private List<String> controlsPermission;
    @TableField(exist = false)
    private List<String> menuPermission;
    @TableField(exist = false)
    private List<String> controlsPermissionList;
    @TableField(exist = false)
    private List<String> menuPermissionList;
}