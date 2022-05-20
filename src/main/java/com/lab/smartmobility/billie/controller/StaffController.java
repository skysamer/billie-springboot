package com.lab.smartmobility.billie.controller;

import com.lab.smartmobility.billie.config.JwtTokenProvider;
import com.lab.smartmobility.billie.dto.DepartmentDTO;
import com.lab.smartmobility.billie.dto.RankDTO;
import com.lab.smartmobility.billie.entity.HttpMessage;
import com.lab.smartmobility.billie.entity.Mail;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.service.StaffService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation(value = "회원가입", notes = "테스트 시 회원가입에 필요한 정보만 담고 나머지 파라미터는 null로 처리")
    @PostMapping(value = "/joinIn", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpMessage joinIn(@RequestBody Staff staff) {
        int checkJoin= staffService.joinIn(staff);

        if(checkJoin == 0){
            return new HttpMessage("fail", "인증된 직원이 아닙니다.");
        }
        else if(checkJoin==400){
            return new HttpMessage("exists", "이미 가입된 회원입니다.");
        }
        return new HttpMessage("success", "회원가입이 완료되었습니다.");
    }

    @ApiOperation(value = "로그인", notes = "성공 시 jwt 토큰을 X-AUTH-TOKEN 키에 매핑하고 헤더에 넣어 반환")
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public Staff login(@RequestBody Staff staff, HttpServletResponse response) {
        if(staffService.loadUserByUsername(staff.getEmail())==null){
            return Staff.builder()
                    .name("가입된 사용자가 아닙니다.")
                    .build();
        }

        Staff findStaff= (Staff) staffService.loadUserByUsername(staff.getEmail());
        String password=staff.getPassword();
        log.info(password);
        boolean checkPassword = staffService.checkPassword(findStaff.getEmail(), password);
        if(!checkPassword){
            return Staff.builder()
                    .password("비밀번호가 일치하지 않습니다.")
                    .build();
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
        String email= jwtTokenProvider.getUserPk(token);
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
