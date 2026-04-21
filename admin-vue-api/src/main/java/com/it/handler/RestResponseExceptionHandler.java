package com.it.handler;

import com.it.util.BusinessException;
import com.it.util.DataResult;
import com.it.util.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class RestResponseExceptionHandler {
   /* @ExceptionHandler(ArithmeticException.class)
    public <T>DataResult<T> arithmeticException(ArithmeticException e){
        log.error("arithmeticException:{}",e);
        return DataResult.fail(ResponseCode.SYSTEM_ERROR.getCode(),ResponseCode.SYSTEM_ERROR.getMessage());
    }*/

    /**
     * 兜底异常监控
     */
    @ExceptionHandler(Exception.class)
    public <T> DataResult<T> exception(Exception e) {
        log.error("exception:{}", e);
        return DataResult.fail(ResponseCode.SYSTEM_ERROR.getCode(), ResponseCode.SYSTEM_ERROR.getMessage());
    }

    /**
     * 自定义业务异常
     *
     * @param e
     * @param <T>
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public <T> DataResult<T> businessException(BusinessException e) {
        log.error("BusinessException:{}", e);
        return DataResult.fail(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public <T> DataResult<T> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException:{}", e);
        String message = e.getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(","));
        return DataResult.fail(ResponseCode.DATA_PARAM_ERROR.getCode(), message);
    }


    /**
     * 无访问权限异常
     */
    @ExceptionHandler(UnauthorizedException.class)
    public <T> DataResult<T> unauthorizedException(UnauthorizedException e) {
        log.error("UnauthorizedException:{}", e);
        return DataResult.fail(ResponseCode.PERMISSION_ERROR.getCode(), ResponseCode.PERMISSION_ERROR.getMessage());
    }
}
