package com.lab.smartmobility.billie.global.handler;

import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
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
        final HttpBodyMessage errorResponse = HttpBodyMessage.builder()
                .code("fail")
                .message("요청값은 null일 수 없습니다").build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
