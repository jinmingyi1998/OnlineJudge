package cn.edu.zjnu.acm.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
    }

    public ForbiddenException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        try {
            if (!super.getMessage().equals(""))
                return super.getMessage();
        } catch (Exception e) {
        }
        return "Forbidden";
    }
}
