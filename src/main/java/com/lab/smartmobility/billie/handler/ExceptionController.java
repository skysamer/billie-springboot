package com.lab.smartmobility.billie.handler;

import com.lab.smartmobility.billie.entity.HttpMessage;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Api(tags = {"에러 핸들러 api"})
public class ExceptionController {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        final HttpMessage errorResponse = HttpMessage.builder()
                .code("fail")
                .message("json variable value is null").build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

   /* @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleNoSuchElementFoundException(Exception e) {
        final HttpMessage errorResponse = HttpMessage.builder()
                .code("fail")
                .message(e.getMessage()).build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }*/
}
