package cn.edu.zjnu.acm.exception;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionResolver {
    @Data
    public static class Result {
        private int code = 200;
        private String message = "";
    }

    @ExceptionHandler(NeedLoginException.class)
    @ResponseBody
    public String exceptionHandle() {
        return "请登录 Please Login";
    }

    @ExceptionHandler(UnavailableException.class)
    @ResponseBody
    public String unavilableHandle() {
        return "维护中，不可用";
    }

    @ExceptionHandler({BindException.class, ConstraintViolationException.class})
    public String validatorExceptionHandler(Exception e) {
        String msg = e instanceof BindException ? String.valueOf(((BindException) e).getBindingResult())
                : String.valueOf(((ConstraintViolationException) e).getConstraintViolations());
        return msg;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public Result handleBindException(MethodArgumentNotValidException ex) {
        Result errorResult = new Result();
        StringBuilder msg = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((e)->{msg.append(e.getDefaultMessage()+"\n");});
        errorResult.setCode(400);
        errorResult.setMessage(msg.toString());
        return errorResult;
    }
}
