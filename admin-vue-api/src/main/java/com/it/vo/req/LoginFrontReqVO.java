package com.it.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 前台登录请求体（避免密码出现在 URL 查询参数中）
 */
@Data
public class LoginFrontReqVO {
    @ApiModelProperty(value = "账号")
    private String username;
    @ApiModelProperty(value = "密码")
    private String password;
}
