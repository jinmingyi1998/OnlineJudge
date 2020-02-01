package cn.edu.zjnu.acm.exception;

import com.sun.deploy.net.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionResolver {
    @ExceptionHandler(NeedLoginException.class)
    @ResponseBody
    public String exceptionHandle(){
        return "请登录 Please Login";
    }
}
