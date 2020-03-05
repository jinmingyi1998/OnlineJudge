package cn.edu.zjnu.acm.exception;

import cn.edu.zjnu.acm.util.RestfulResult;
import cn.edu.zjnu.acm.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionResolver {
    public static final Result pleaseLoginResult = new Result(403, "请登录 Please Login");

    @ExceptionHandler(NeedLoginException.class)
    @ResponseBody
    public Result exceptionHandle() {
        return pleaseLoginResult;
    }

    @ExceptionHandler(UnavailableException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Result unavilableHandle() {
        return new Result(503, "维护中，不可用");
    }

    @ExceptionHandler({BindException.class, ConstraintViolationException.class})
    public Result validatorExceptionHandler(Exception e) {
        String msg = e instanceof BindException ? String.valueOf(((BindException) e).getBindingResult())
                : String.valueOf(((ConstraintViolationException) e).getConstraintViolations());
        return new Result(400, msg);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public Result handleBindException(MethodArgumentNotValidException ex) {
        RestfulResult errorResult = new RestfulResult(400, "Bad Request", "");
        StringBuilder msg = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((e) -> {
            msg.append(e.getDefaultMessage() + "\n");
        });
        errorResult.setCode(400);
        errorResult.setData(msg.toString());
        return errorResult;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public Result notFoundExceptionHandle(NotFoundException e) {
        return new Result(404, e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public Result forbiddenExceptionHandle(ForbiddenException e) {
        return new Result(403, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Result serverExceptionHandle(Exception e) {
        e.printStackTrace();
        return new Result(500, "Internal Server Error");
    }
}
