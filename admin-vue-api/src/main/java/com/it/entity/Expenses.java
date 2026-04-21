package com.it.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.it.pojo.BasePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 报销实体
 */
@TableName("sys_expenses")
@Data
@ApiModel("报销实体")
public class Expenses extends BasePage {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "账单金额")
    private Float price;


    @ApiModelProperty(value = "用户id")
    private String user_id;

    @ApiModelProperty(value = "相关凭证")
    private String img;

    @ApiModelProperty(value = "报销状态")
    private String state;

    @ApiModelProperty(value = "备注")
    private String content;

    @ApiModelProperty(value = "报销日期")
    private String expenses_time;

}