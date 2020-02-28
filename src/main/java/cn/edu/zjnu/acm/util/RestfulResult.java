package cn.edu.zjnu.acm.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestfulResult extends Result {

    Object data;

    @Override
    public String toString() {
        return "{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data.toString() +
                '}';
    }

    public RestfulResult() {
    }

    public RestfulResult(Integer code, String message, Object data) {
        super(code,message);
        this.data = data;
    }

}
