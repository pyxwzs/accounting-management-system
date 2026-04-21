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
 * 轮播图实体类
 */
@Data
@TableName("sys_slideshow")
@ApiModel("轮播图实体")
public class Slideshow extends BasePage {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "关联条目id")
    private String item_id;

    @ApiModelProperty(value = "图片")
    private String img;

    @ApiModelProperty(value = "创建时间")
    private String create_time;

    @ApiModelProperty(value = "关联条目")
    @TableField(exist = false)
    private Article article;
}