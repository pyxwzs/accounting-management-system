package com.it.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 权限表，决定用户的具体操作
 */
@TableName("sys_permission")
@Data
@ApiModel("权限实体")
public class Permission {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "权限名称")
    private String name;
    @ApiModelProperty(value = "操作类型")
    private String type;
    @ApiModelProperty(value = "权限标识")
    private String mark;
    @ApiModelProperty(value = "创建时间")
    private String create_time;
}