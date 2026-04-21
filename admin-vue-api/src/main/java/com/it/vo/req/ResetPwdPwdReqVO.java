package com.it.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ResetPwdPwdReqVO {
    @ApiModelProperty(value = "新密码")
    private String newPwd;
    @ApiModelProperty(value = "账号")
    private String username;
}
