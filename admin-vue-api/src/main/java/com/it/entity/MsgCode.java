package com.it.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.it.pojo.BasePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 验证码实体
 */
@TableName("sys_msgCode")
@Data
@ApiModel("验证码实体")
public class MsgCode extends BasePage {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "验证码")
    private String code;
    @ApiModelProperty(value = "创建时间")
    private String create_time;
    @ApiModelProperty(value = "过期时间")
    private String expire_time;

}