package cn.edu.zjnu.acm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class NeedLoginException extends RuntimeException {
}
