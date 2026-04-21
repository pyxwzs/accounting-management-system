package com.it.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.it.pojo.BasePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 消息记录实体类
 */
@Data
@TableName("sys_message")
@ApiModel("消息记录实体")
public class Message extends BasePage {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "接收人")
    private String user_id;

    @ApiModelProperty(value = "发送人")
    private String send_id;

    @ApiModelProperty(value = "接收内容")
    private String content;

    @ApiModelProperty(value = "创建时间")
    private String create_time;

    @ApiModelProperty(value = "消息类型")
    private String type;

    @ApiModelProperty(value = "消息读取状态")
    private String state;
}