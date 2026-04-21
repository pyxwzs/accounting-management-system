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
 * 存钱计划实体
 */
@TableName("sys_plan")
@Data
@ApiModel("存钱计划实体")
public class Plan extends BasePage {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "目标名称")
    private String name;

    @ApiModelProperty(value = "目标金额")
    private Float price;

    @ApiModelProperty(value = "已存入金额")
    private Float hasPrice;

    @ApiModelProperty(value = "币种")
    private String type;

    @ApiModelProperty(value = "用户id")
    private String user_id;

    @ApiModelProperty(value = "起始日期")
    private String start_time;

    @ApiModelProperty(value = "截止日期")
    private String end_time;

    @ApiModelProperty(value = "金额进度 0-100")
    @TableField(exist = false)
    private Float progress_percent;

    @ApiModelProperty(value = "时间进度 0-100（已过天数/计划总天数）")
    @TableField(exist = false)
    private Float time_progress_percent;

    @ApiModelProperty(value = "预测说明")
    @TableField(exist = false)
    private String forecast_message;

    @ApiModelProperty(value = "是否有望按期完成")
    @TableField(exist = false)
    private Boolean forecast_on_track;

    @ApiModelProperty(value = "近30天日均结余（收入-支出）/30")
    @TableField(exist = false)
    private Float recent_net_daily;

    @ApiModelProperty(value = "按期完成所需日均存入")
    @TableField(exist = false)
    private Float required_daily_save;

}