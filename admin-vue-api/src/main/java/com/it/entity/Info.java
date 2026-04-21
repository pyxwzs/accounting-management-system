package com.it.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.it.pojo.BasePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 收支记录实体类
 */
@Data
@TableName("sys_info")
@ApiModel("收支记录实体")
public class Info extends BasePage {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "用户id")
    private String user_id;

    @ApiModelProperty(value = "记录名称")
    private String name;

    @ApiModelProperty(value = "金额")
    private Float price;

    @ApiModelProperty(value = "类型/收入,支出")
    private String type;

    @ApiModelProperty(value = "分类")
    private String classify;

    @ApiModelProperty(value = "备注")
    private String content;

    @ApiModelProperty(value = "支出预算id")
    private String budget_id;
    @TableField(exist = false)
    private String budget_name;

    @ApiModelProperty(value = "日期")
    private String day;

    @ApiModelProperty(value = "创建时间")
    private String create_time;
}