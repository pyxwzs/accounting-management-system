package com.it.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 角色权限关联实体
 */
@TableName("sys_role_permission")
@Data
@ApiModel("角色权限关联实体")
public class RolePermission {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "类型:0菜单/1权限")
    private String type;
    @ApiModelProperty(value = "角色id")
    private String role_id;
    @ApiModelProperty(value = "条目id:菜单表主键/权限表主键")
    private String item_id;
}