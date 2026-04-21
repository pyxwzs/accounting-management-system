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
 * 评论回复实体类
 */
@Data
@TableName("sys_reply")
@ApiModel("评论回复实体")
public class Reply extends BasePage {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "条目id")
    private String item_id;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "用户id")
    private String user_id;

    @ApiModelProperty(value = "用户对象")
    @TableField(exist = false)
    private User user;

    @ApiModelProperty(value = "被评论用户id")
    private String send_id;

    @TableField(exist = false)
    @ApiModelProperty(value = "被评论用户昵称")
    private String send_name;


    @ApiModelProperty(value = "创建时间")
    private String create_time;
}