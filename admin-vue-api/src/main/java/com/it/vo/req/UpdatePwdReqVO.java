package com.it.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdatePwdReqVO {
    @ApiModelProperty(value = "旧密码")
    private String oldPwd;
    @ApiModelProperty(value = "新密码")
    private String newPwd;
}
