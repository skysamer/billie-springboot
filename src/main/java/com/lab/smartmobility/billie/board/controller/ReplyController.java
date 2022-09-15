package com.lab.smartmobility.billie.board.controller;

import com.lab.smartmobility.billie.global.config.JwtTokenProvider;
import com.lab.smartmobility.billie.board.dto.NestedReplyRegisterForm;
import com.lab.smartmobility.billie.board.dto.ReplyModifyForm;
import com.lab.smartmobility.billie.board.dto.ReplyRegisterForm;
import com.lab.smartmobility.billie.entity.HttpBodyMessage;
import com.lab.smartmobility.billie.board.service.ReplyService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Api(tags = {"댓글 api"})
@RequestMapping("/reply")
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService service;
    private final JwtTokenProvider jwtTokenProvider;

    @ApiOperation(value = "댓글 등록")
    @ApiResponses({
            @ApiResponse(code = 201, message = "댓글 등록 성공"),
            @ApiResponse(code = 400, message = "요청값은 null일 수 없습니다 // 해당 게시글이 존재하지 않습니다 // 토큰이 유효하지 않습니다")
    })
    @PostMapping("/user")
    public ResponseEntity<HttpBodyMessage> register(@Valid @RequestBody ReplyRegisterForm registerForm,
                                                    @RequestHeader("X-AUTH-TOKEN") String token){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(
                    new HttpBodyMessage("fail", "토큰이 유효하지 않습니다"), HttpStatus.BAD_REQUEST);
        }

        String userEmail = jwtTokenProvider.getUserPk(token);
        HttpBodyMessage httpBodyMessage = service.register(registerForm, userEmail);
        if(httpBodyMessage.getCode().equals("fail")){
            return new ResponseEntity<>(httpBodyMessage, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(httpBodyMessage, HttpStatus.CREATED);
    }

    @ApiOperation(value = "대댓글 등록")
    @ApiResponses({
            @ApiResponse(code = 201, message = "댓글 등록 성공"),
            @ApiResponse(code = 400, message = "요청값은 null일 수 없습니다 // 해당 게시글이 존재하지 않습니다 // 토큰이 유효하지 않습니다")
    })
    @PostMapping("/user/nested")
    public ResponseEntity<HttpBodyMessage> registerNested(@Valid @RequestBody NestedReplyRegisterForm registerForm,
                                                    @RequestHeader("X-AUTH-TOKEN") String token){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(
                    new HttpBodyMessage("fail", "토큰이 유효하지 않습니다"), HttpStatus.BAD_REQUEST);
        }

        String userEmail = jwtTokenProvider.getUserPk(token);
        HttpBodyMessage httpBodyMessage = service.registerNested(registerForm, userEmail);
        if(httpBodyMessage.getCode().equals("fail")){
            return new ResponseEntity<>(httpBodyMessage, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(httpBodyMessage, HttpStatus.CREATED);
    }

    @ApiOperation(value = "댓글 수정")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "댓글 번호")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "댓글 수정 성공"),
            @ApiResponse(code = 400, message = "요청값은 null일 수 없습니다"),
            @ApiResponse(code = 404, message = "댓글이 존재하지 않습니다")
    })
    @PutMapping("/user/{id}")
    public ResponseEntity<HttpBodyMessage> modify(@PathVariable Long id, @Valid @RequestBody ReplyModifyForm registerForm){

        HttpBodyMessage httpBodyMessage = service.modify(id, registerForm);
        if(httpBodyMessage.getCode().equals("fail")){
            return new ResponseEntity<>(httpBodyMessage, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(httpBodyMessage, HttpStatus.OK);
    }

    @ApiOperation(value = "댓글 삭제")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "댓글 번호")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "댓글 삭제 성공"),
            @ApiResponse(code = 404, message = "댓글이 존재하지 않습니다")
    })
    @DeleteMapping("/user/{id}")
    public ResponseEntity<HttpBodyMessage> remove(@PathVariable Long id){

        HttpBodyMessage httpBodyMessage = service.remove(id);
        if(httpBodyMessage.getCode().equals("fail")){
            return new ResponseEntity<>(httpBodyMessage, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(httpBodyMessage, HttpStatus.OK);
    }
}
