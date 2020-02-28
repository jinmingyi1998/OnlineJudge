package cn.edu.zjnu.acm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ForbiddenException extends RuntimeException {
    @Override
    public String getMessage() {
        try {
            if (!super.getMessage().equals(""))
                return super.getMessage();
        } catch (Exception e) {
            ;
        }
        return "Forbidden";
    }

    public ForbiddenException() {
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
