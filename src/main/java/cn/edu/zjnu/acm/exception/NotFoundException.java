package cn.edu.zjnu.acm.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        try {
            if (!super.getMessage().equals(""))
                return super.getMessage();
        } catch (Exception e) {
        }
        return "Not Found";
    }
}
