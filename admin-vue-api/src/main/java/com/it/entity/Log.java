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
 * 操作日志表
 */
@TableName("sys_log")
@Data
@ApiModel("操作日志实体")
public class Log extends BasePage {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "操作用户")
    private String name;
    @ApiModelProperty(value = "操作明细")
    private String operation;
    @ApiModelProperty(value = "创建时间")
    private String create_time;
    @ApiModelProperty(value = "0成功/1失败")
    private String operation_result;
    @ApiModelProperty(value = "失败原因")
    private String operation_fail_reason;
    @ApiModelProperty(value = "开始时间")
    @TableField(exist = false)
    private String startTime;
    @ApiModelProperty(value = "结束时间")
    @TableField(exist = false)
    private String endTime;
}