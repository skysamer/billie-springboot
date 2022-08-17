package com.lab.smartmobility.billie.controller;

import com.lab.smartmobility.billie.config.JwtTokenProvider;
import com.lab.smartmobility.billie.dto.board.BoardRegisterForm;
import com.lab.smartmobility.billie.entity.HttpBodyMessage;
import com.lab.smartmobility.billie.service.BoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Api(tags = {"자유게시판 api"})
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService service;
    private final JwtTokenProvider jwtTokenProvider;

    @ApiOperation(value = "게시글 등록")
    @ApiResponses({
            @ApiResponse(code = 201, message = "게시글 등록 성공"),
            @ApiResponse(code = 400, message = "요청값은 null일 수 없습니다"),
    })
    @PostMapping("/user")
    public ResponseEntity<HttpBodyMessage> register(@Valid @RequestBody BoardRegisterForm registerForm,
                                                    @RequestHeader("X-AUTH-TOKEN") String token){
        String userEmail = jwtTokenProvider.getUserPk(token);
        HttpBodyMessage httpBodyMessage = service.register(registerForm, userEmail);
        return new ResponseEntity<>(httpBodyMessage, HttpStatus.CREATED);
    }
}
