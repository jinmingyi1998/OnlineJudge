package cn.edu.zjnu.acm.util;

import lombok.Data;

@Data
public class Result {
    int code = 200;
    String message = "";

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result() {
    }

    @Override
    public String toString() {
        return "{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
