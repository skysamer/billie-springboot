package com.lab.smartmobility.billie.board.controller;

import com.lab.smartmobility.billie.global.config.JwtTokenProvider;
import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.board.dto.BoardDetailsForm;
import com.lab.smartmobility.billie.board.dto.BoardListForm;
import com.lab.smartmobility.billie.board.dto.BoardRegisterForm;
import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.board.service.BoardService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
            @ApiResponse(code = 201, message = "생성된 글번호 반환"),
            @ApiResponse(code = 400, message = "요청값은 null일 수 없습니다 // 토큰이 유효하지 않습니다")
    })
    @PostMapping("/user")
    public ResponseEntity<HttpBodyMessage> register(@Valid @RequestBody BoardRegisterForm registerForm,
                                                    @RequestHeader("X-AUTH-TOKEN") String token){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(
                    new HttpBodyMessage("fail", "토큰이 유효하지 않습니다"), HttpStatus.BAD_REQUEST);
        }

        String userEmail = jwtTokenProvider.getUserPk(token);
        HttpBodyMessage httpBodyMessage = service.register(registerForm, userEmail);
        return new ResponseEntity<>(httpBodyMessage, HttpStatus.CREATED);
    }

    @ApiOperation(value = "게시글 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "date", value = "연월 (yyyy-MM, 전체는 all)"),
            @ApiImplicitParam(name = "keyword", value = "검색어"),
            @ApiImplicitParam(name = "page", value = "페이지"),
            @ApiImplicitParam(name = "size", value = "게시글 수")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 204, message = "조건에 맞는 데이터 없음")
    })
    @GetMapping("/user/{date}/{keyword}/{page}/{size}")
    public ResponseEntity<PageResult<BoardListForm>> getBoardList(@PathVariable String date,
                             @PathVariable String keyword,
                             @PathVariable Integer page,
                             @PathVariable Integer size){
        PageResult<BoardListForm> result = service.getBoardList(keyword, date, PageRequest.of(page, size));
        if(result.getCount() == 0){
            return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "게시글 상세 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "글번호")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 400, message = "토큰이 유효하지 않음"),
            @ApiResponse(code = 404, message = "번호값이 존재하지 않음")
    })
    @GetMapping("/user/{id}")
    public ResponseEntity<BoardDetailsForm> getBoard(@RequestHeader("X-AUTH-TOKEN") String token, @PathVariable Long id){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String email = jwtTokenProvider.getUserPk(token);

        BoardDetailsForm result = service.getBoard(id, email);
        if(result == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "게시글 수정")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "글번호")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "게시글 수정 성공"),
            @ApiResponse(code = 404, message = "게시글이 존재하지 않습니다"),
            @ApiResponse(code = 400, message = "요청값은 null일 수 없습니다 // 해당 유저만 수정할 수 있습니다")
    })
    @PutMapping("/user/{id}")
    public ResponseEntity<HttpBodyMessage> modify(@RequestHeader("X-AUTH-TOKEN") String token, @PathVariable Long id,
                                                  @Valid @RequestBody BoardRegisterForm registerForm){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String email = jwtTokenProvider.getUserPk(token);

        HttpBodyMessage result = service.modify(id, email, registerForm);
        if(result.getMessage().equals("게시글이 존재하지 않습니다")){
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }else if(result.getMessage().equals("해당 유저만 수정할 수 있습니다")){
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "게시글 삭제")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "글번호")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "게시글 삭제 성공"),
            @ApiResponse(code = 404, message = "게시글이 존재하지 않습니다"),
            @ApiResponse(code = 400, message = "해당 유저만 수정할 수 있습니다")
    })
    @DeleteMapping("/user/{id}")
    public ResponseEntity<HttpBodyMessage> remove(@RequestHeader("X-AUTH-TOKEN") String token, @PathVariable Long id){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String email = jwtTokenProvider.getUserPk(token);

        HttpBodyMessage result = service.remove(id, email);
        if(result.getMessage().equals("게시글이 존재하지 않습니다")){
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }else if(result.getMessage().equals("해당 유저만 수정할 수 있습니다")){
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "좋아요")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "글번호")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "좋아요 계산 완료"),
            @ApiResponse(code = 404, message = "게시글이 존재하지 않습니다"),
            @ApiResponse(code = 400, message = "게시글이 존재하지 않습니다")
    })
    @PatchMapping("/user/{id}")
    public ResponseEntity<HttpBodyMessage> like(@PathVariable Long id, @RequestHeader("X-AUTH-TOKEN") String token){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(
                    new HttpBodyMessage("fail", "토큰이 유효하지 않습니다"), HttpStatus.BAD_REQUEST);
        }

        String email = jwtTokenProvider.getUserPk(token);
        HttpBodyMessage result = service.like(email, id);
        if(result.getCode().equals("fail")){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "이전글 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "글번호")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 400, message = "토큰이 유효하지 않음"),
            @ApiResponse(code = 204, message = "이전글 없음")
    })
    @GetMapping("/user/prev/{id}")
    public ResponseEntity<BoardDetailsForm> getPrev(@RequestHeader("X-AUTH-TOKEN") String token, @PathVariable Long id){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String email = jwtTokenProvider.getUserPk(token);

        BoardDetailsForm result = service.getPrevBoard(id, email);
        if(result == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "다음글 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "글번호")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 204, message = "다음글 없음")
    })
    @GetMapping("/user/next/{id}")
    public ResponseEntity<BoardDetailsForm> getNext(@RequestHeader("X-AUTH-TOKEN") String token, @PathVariable Long id){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String email = jwtTokenProvider.getUserPk(token);

        BoardDetailsForm result = service.getNextBoard(id, email);
        if(result == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
