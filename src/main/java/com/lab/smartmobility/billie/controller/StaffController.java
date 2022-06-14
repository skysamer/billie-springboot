package com.lab.smartmobility.billie.controller;

import com.lab.smartmobility.billie.config.JwtTokenProvider;
import com.lab.smartmobility.billie.dto.staff.*;
import com.lab.smartmobility.billie.entity.HttpMessage;
import com.lab.smartmobility.billie.entity.Mail;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.service.StaffService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Api(tags = {"로그인, 비밀번호 찾기 및 회원가입을 위한 api"})
@RestController
public class StaffController {
    private final Log log = LogFactory.getLog(getClass());
    private final JwtTokenProvider jwtTokenProvider;
    private final StaffService staffService;

    @ApiOperation(value = "이메일 인증 토큰 전송")
    @ApiResponses({
            @ApiResponse(code = 200, message = "not equal staff info // send email token")
    })
    @PostMapping("/send-email-token")
    public HttpMessage sendEmailToken(@RequestBody EmailForm emailForm){
        if(staffService.sendEmailToken(emailForm.getEmail())==9999){
            return new HttpMessage("fail", "not equal staff info");
        }
        return new HttpMessage("success", "send email token");
    }

    @ApiOperation(value = "이메일 토큰 검증")
    @ApiResponses({
            @ApiResponse(code = 200, message = "not equal email token // equal token // time out")
    })
    @PostMapping("/verify-email-token")
    public HttpMessage verifyEmailToken(@RequestBody EmailTokenForm emailTokenForm){
        int isVerified=staffService.verifyEmailToken(emailTokenForm);
        if(isVerified==9999){
            return new HttpMessage("fail", "not equal email token");
        }else if(isVerified==500){
            return new HttpMessage("fail", "time out");
        }
        return new HttpMessage("success", "equal token");
    }


    @ApiOperation(value = "회원가입")
    @ApiResponses({
            @ApiResponse(code = 200, message = "exists join info // success sign up // not verified")
    })
    @PostMapping(value = "/sign-up", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpMessage joinIn(@RequestBody SignUpForm signUpForm) {
        int checkJoin= staffService.joinIn(signUpForm);
        if(checkJoin==9999){
            return new HttpMessage("fail", "exists join info");
        }else if(checkJoin==500){
            return new HttpMessage("fail", "not verified");
        }
        return new HttpMessage("success", "success sign up");
    }

    @ApiOperation(value = "로그인", notes = "성공 시 jwt 토큰을 X-AUTH-TOKEN 키에 매핑하고 헤더에 넣어 반환")
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public Staff login(@RequestBody LoginForm loginForm, HttpServletResponse response) {
        if(staffService.loadUserByUsername(loginForm.getEmail())==null){
            return Staff.builder()
                    .name("가입된 사용자가 아닙니다.").build();
        }

        Staff findStaff= (Staff) staffService.loadUserByUsername(loginForm.getEmail());

        boolean checkPassword = staffService.checkPassword(findStaff.getEmail(), loginForm.getPassword());
        if(!checkPassword){
            return Staff.builder()
                    .name("비밀번호가 일치하지 않습니다.").build();
        }

        String token=jwtTokenProvider.createTokenLogin(findStaff.getEmail(), findStaff.getRole());
        response.setHeader("X-AUTH-TOKEN", token);
        return findStaff;
    }

    @ApiOperation(value = "비밀번호 찾기")
    @PostMapping(value = "/findPassword", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpMessage findPassword(@RequestBody Mail mail){
        int initializationPassword= staffService.findPassword(mail.getAddress());

        if(initializationPassword==0){
            return new HttpMessage("fail", "존재하지 않는 이메일입니다.");
        }
        else{
            return new HttpMessage("success", "초기화된 비밀번호가 귀하의 이메일로 발송되었습니다.");
        }
    }

    @GetMapping("/check-login")
    @ApiOperation(value = "로그인 여부 체크", notes = "헤더에 사용중인 토큰 추가하여 전송")
    public HashMap<String, Object> checkLogin(ServletRequest request){
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            log.info("error");

            HashMap<String, Object> staffInfo=new HashMap<>();
            staffInfo.put("isAuth", false);
            return staffInfo;
        }
        String email = jwtTokenProvider.getUserPk(token);
        return staffService.checkLogin(email);
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
