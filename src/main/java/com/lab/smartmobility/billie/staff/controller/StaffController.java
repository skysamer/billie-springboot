package com.lab.smartmobility.billie.staff.controller;

import com.lab.smartmobility.billie.global.config.JwtTokenProvider;
import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.global.dto.Mail;
import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.staff.dto.*;
import com.lab.smartmobility.billie.staff.service.StaffService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Api(tags = {"로그인, 비밀번호 찾기 및 회원가입을 위한 api"})
@RestController
public class StaffController {
    private final Log log;
    private final JwtTokenProvider jwtTokenProvider;
    private final StaffService staffService;

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    @ApiOperation(value = "이메일 인증 토큰 전송")
    @ApiResponses({
            @ApiResponse(code = 200, message = "not equal staff info // send email token")
    })
    @PostMapping("/send-email-token")
    public HttpBodyMessage sendEmailToken(@RequestBody EmailForm emailForm){
        if(staffService.sendEmailToken(emailForm.getEmail())==9999){
            return new HttpBodyMessage("fail", "not equal staff info");
        }
        return new HttpBodyMessage("success", "send email token");
    }

    @ApiOperation(value = "이메일 토큰 검증")
    @ApiResponses({
            @ApiResponse(code = 200, message = "not equal email token // equal token // time out")
    })
    @PostMapping("/verify-email-token")
    public HttpBodyMessage verifyEmailToken(@RequestBody EmailTokenForm emailTokenForm){
        int isVerified=staffService.verifyEmailToken(emailTokenForm);
        if(isVerified==9999){
            return new HttpBodyMessage("fail", "not equal email token");
        }else if(isVerified==500){
            return new HttpBodyMessage("fail", "time out");
        }
        return new HttpBodyMessage("success", "equal token");
    }


    @ApiOperation(value = "회원가입")
    @ApiResponses({
            @ApiResponse(code = 200, message = "exists join info // success sign up // not verified")
    })
    @PostMapping(value = "/sign-up", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpBodyMessage joinIn(@RequestBody SignUpForm signUpForm) {
        return staffService.joinIn(signUpForm);
    }

    @ApiOperation(value = "로그인", notes = "성공 시 jwt 토큰을 X-AUTH-TOKEN 키에 매핑하고 헤더에 넣어 반환")
    @ApiResponses({
            @ApiResponse(code = 200, message = "로그인 성공 (가입 직원 정보)"),
            @ApiResponse(code = 400, message = "아이디 혹은 비밀번호 불일치")
    })
    @PostMapping(value = "/login/{is-auto}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Staff> login(@PathVariable("is-auto") int isAuto, @RequestBody LoginForm loginForm) {
        Staff staff = staffService.login(loginForm.getEmail(), loginForm.getPassword());
        if(staff == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String token = setToken(isAuto, staff);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-AUTH-TOKEN", token);
        return new ResponseEntity<>(staff, headers, HttpStatus.OK);
    }

    private String setToken(int isAuto, Staff staff){
        if(isAuto == 1){
            return jwtTokenProvider.createLongTermTokenLogin(staff.getEmail(), staff.getRole());
        }
        return jwtTokenProvider.createTokenLogin(staff.getEmail(), staff.getRole());
    }

    @ApiOperation(value = "비밀번호 찾기")
    @PostMapping(value = "/findPassword", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpBodyMessage findPassword(@RequestBody Mail mail){
        int initializationPassword= staffService.findPassword(mail.getAddress());

        if(initializationPassword==0){
            return new HttpBodyMessage("fail", "존재하지 않는 이메일입니다.");
        }
        else{
            return new HttpBodyMessage("success", "초기화된 비밀번호가 귀하의 이메일로 발송되었습니다.");
        }
    }

    @GetMapping("/check-login")
    @ApiOperation(value = "로그인 여부 체크", notes = "헤더에 사용중인 토큰 추가하여 전송")
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회성공"),
            @ApiResponse(code = 400, message = "토큰이 유효하지 않은 경우"),
    })
    public ResponseEntity<UserInfoForm> checkLogin(@RequestHeader("X-AUTH-TOKEN") String token){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String email = jwtTokenProvider.getUserPk(token);
        UserInfoForm userInfo = staffService.checkLogin(email);
        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }

    @GetMapping("/department")
    @ApiOperation(value = "전체 부서 목록 조회")
    public List<DepartmentDTO> getDepartmentList(){
        return staffService.getDepartmentList();
    }

    @GetMapping("/rank")
    @ApiOperation(value = "전체 직급 목록 조회")
    public List<RankDTO> getRankList(){
        return staffService.getRankList();
    }

}
