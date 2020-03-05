package cn.edu.zjnu.acm.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestfulResult extends Result {

    @JsonIgnore
    public static final String SUCCESS = "success";

    private Object data;

    public RestfulResult() {
    }

    public RestfulResult(Integer code, String message, Object data) {
        super(code, message);
        this.data = data;
    }

    public RestfulResult(int code, String message) {
        super(code, message);
        this.data=null;
    }

    @Override
    public String toString() {
        return "{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data.toString() +
                '}';
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static RestfulResult successResult(){
        return new RestfulResult(200,SUCCESS,null);
    }

}
