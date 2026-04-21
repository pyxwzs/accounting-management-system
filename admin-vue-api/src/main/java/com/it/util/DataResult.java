package com.it.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DataResult <T>{
    @ApiModelProperty(value = "响应状态码 code=0代表成功，其它失败")
    private int code;
    @ApiModelProperty(value = "响应的业务数据")
    private T data;
    @ApiModelProperty(value = "响应状提示语")
    private String message;

    /**
     * 响应成功的方法
     * @param data
     * @param <T>
     * @return
     */
    public static <T> DataResult<T> success(T data){
        DataResult<T> result=new DataResult<>();
        result.data=data;
        result.code= ResponseCode.SUCCESS.getCode();
        result.message=ResponseCode.SUCCESS.getMessage();
        return result;
    }

    /**
     * 响应失败工具方法
     * @param code
     * @param message
     * @param <T>
     * @return
     */
    public static <T> DataResult<T> fail(int code ,String message){
        DataResult<T> result=new DataResult<>();
        result.code= code;
        result.message=message;
        return result;
    }
}
