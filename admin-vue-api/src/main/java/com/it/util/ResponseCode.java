package com.it.util;

public enum ResponseCode {
    SUCCESS(0, "响应成功"),
    SYSTEM_ERROR(500000, "服务异常，请稍后再试"),
    OPERATION_ERROR(500001, "操作失败，请稍后再试"),
    CREATE_PATH_ERROR(500003, "创建文件夹异常"),
    UPLOAD_FILE_ERROR(500004, "上传文件异常"),
    EMOJIfILTER_ERROR(500005, "您保存的内容中含有特殊字符,因转义失败出现错误!"),

    DATA_PARAM_ERROR(400000, "传入参数错误"),
    ACCOUNT_ALREADY_EXISTS(400001, "账号已存在，请登录"),
    ACCOUNT_NOT_FOUND(400002, "账号不存在"),
    ACCOUNT_LOCK(400003, "账号已锁定，请联系管理员解锁"),
    ACCOUNT_ERROR(400004, "账户密码不匹配"),
    TOKEN_ERROR(401000, "登录信息已失效,请重新登录"),
    PERMISSION_ERROR(400005, "无访问权限"),
    OLD_PASSWORD_ERROR(400006, "旧密码输入错误，请重新输入"),
    CODE_ERROR(400007, "验证码不正确，请重新输入"),
    CODE_EXPIRE_ERROR(400008, "验证码已失效，请重新发送"),
    EMAIL_SEND_ERROR(400009, "验证码发送失败"),
    DELETE_USER_ERROR(400009, "系统管理员不可被删除"),
    LOGIN_ERROR(400010, "普通用户无法登录后台"),
    PASSWORD_ERROR(400011, "密码不正确");
    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
