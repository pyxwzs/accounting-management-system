package com.it.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.it.pojo.TreeNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 菜单表
 */
@TableName("sys_menu")
@Data
@ApiModel("菜单实体")
public class Menu extends TreeNode<Menu> {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "菜单名称")
    private String name;
    @ApiModelProperty(value = "图标")
    private String icon;
    @ApiModelProperty(value = "组件路径")
    private String component;
    @ApiModelProperty(value = "路由地址")
    private String url;
    @ApiModelProperty(value = "创建时间")
    private String create_time;
    @ApiModelProperty(value = "父主键")
    private String parent_id;
    @ApiModelProperty(value = "排序")
    private Integer num;

}