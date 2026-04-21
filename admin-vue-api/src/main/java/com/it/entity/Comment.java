package com.it.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.it.pojo.BasePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 评论实体类
 */
@Data
@TableName("sys_comment")
@ApiModel("评论实体")
public class Comment extends BasePage {
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

    @ApiModelProperty(value = "创建时间")
    private String create_time;


    @ApiModelProperty(value = "评论下的回复对象列表")
    @TableField(exist = false)
    private List<Reply> replyList;
}