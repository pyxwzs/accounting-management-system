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
 * 预算实体类
 */
@Data
@TableName("sys_budget")
@ApiModel("预算实体")
public class Budget extends BasePage {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "用户id")
    private String user_id;

    @ApiModelProperty(value = "预算用途")
    private String name;

    @ApiModelProperty(value = "预算金额")
    private Float price;

    @ApiModelProperty(value = "已使用金额")
    @TableField(exist = false)
    private Float use_price;

    @ApiModelProperty(value = "剩余金额")
    @TableField(exist = false)
    private Float residue_price;

    @ApiModelProperty(value = "统计周期：none累计 month月 quarter季 year年")
    private String cycle_type;

    @ApiModelProperty(value = "预警阈值0-1，达到后提醒")
    private Float warn_ratio;

    @ApiModelProperty(value = "上次发送预警邮件的日期 yyyy-MM-dd")
    private String last_email_date;

    @ApiModelProperty(value = "已用占预算比例 0-1")
    @TableField(exist = false)
    private Float usage_ratio;

    @ApiModelProperty(value = "界面预警文案")
    @TableField(exist = false)
    private String alert_message;

    @ApiModelProperty(value = "下期计划建议")
    @TableField(exist = false)
    private String next_period_suggestion;

    @ApiModelProperty(value = "上一周期是否超支")
    @TableField(exist = false)
    private Boolean previous_period_overspend;

    @ApiModelProperty(value = "创建时间")
    private String create_time;
}