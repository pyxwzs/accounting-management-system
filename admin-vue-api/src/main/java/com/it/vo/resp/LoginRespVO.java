package com.it.vo.resp;

import com.it.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LoginRespVO {
    @ApiModelProperty(value = "业务token")
    private String token;
    @ApiModelProperty(value = "用户信息")
    private User user;
}
