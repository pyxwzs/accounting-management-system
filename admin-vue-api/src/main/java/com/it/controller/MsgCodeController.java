package com.it.controller;

import com.it.entity.User;
import com.it.service.MsgCodeService;
import com.it.service.UserService;
import com.it.util.BusinessException;
import com.it.util.CommonUtils;
import com.it.util.ResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

@RestController
@RequestMapping("/code")
@Api(tags = "验证码模块")
public class MsgCodeController {
    @Resource
    MsgCodeService msgCodeService;
    @Resource
    UserService userService;
    @Resource
    private JavaMailSender mailSender;
    private static String FROM = "1156326165@qq.com";
    private static String SUBJECT = "vue-admin";

    @GetMapping("/sendCode")
    @ApiOperation(value = "发送验证码")
    public void sendCode(String username) {
        User userByUsername = userService.getUserByUsername(username);
        if (userByUsername == null) {
            throw new BusinessException(ResponseCode.ACCOUNT_NOT_FOUND.getCode(), ResponseCode.ACCOUNT_NOT_FOUND.getMessage());
        }
        String insert = msgCodeService.insert();
        if (!CommonUtils.stringIsNotBlack(insert)) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
        //TODO 发送邮箱操作

        try {
            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setFrom(FROM);
            message.setTo(userByUsername.getEmail());
            message.setSubject(SUBJECT);
            message.setText("验证码" + insert + "用于更改密码,五分钟内有效,验证码提供给他人可能导致账号被盗,请勿泄露,谨防别骗!");

            this.mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.out.println(e);
            throw new BusinessException(ResponseCode.EMAIL_SEND_ERROR.getCode(), ResponseCode.EMAIL_SEND_ERROR.getMessage());
        }

    }


    @GetMapping("/verifyCode")
    @ApiOperation(value = "验证验证码")
    public void verifyCode(String code) {
        boolean insert = msgCodeService.verifyCode(code);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }
}
