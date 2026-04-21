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
 * 用户实体类
 */
@Data
@TableName("sys_user")
@ApiModel("用户实体")
public class User extends BasePage {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "账号")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "加密的盐")
    private String salt;

    @ApiModelProperty(value = "联系方式")
    private String phone;

    @ApiModelProperty(value = "姓名")
    private String real_name;

    @ApiModelProperty(value = "昵称")
    private String nick_name;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "用户角色id")
    private String roleId;

    @ApiModelProperty(value = "用户状态，0表示用户已禁用")
    private String status;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "创建时间")
    private String create_time;

    @ApiModelProperty(value = "用户头像")
    private String img_url;
    @TableField(exist = false)
    @ApiModelProperty(value = "角色名")
    private String role_name;

    @ApiModelProperty(value = "年龄")
    private String age;


}