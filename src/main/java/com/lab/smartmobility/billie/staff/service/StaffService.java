package com.lab.smartmobility.billie.staff.service;

import com.lab.smartmobility.billie.global.config.CommonEncoder;
import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.staff.dto.*;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import com.lab.smartmobility.billie.staff.repository.StaffRepositoryImpl;
import com.lab.smartmobility.billie.global.util.CustomSimpleMailMessage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RequiredArgsConstructor
@Transactional
@Service
public class StaffService implements UserDetailsService {
    private final Log log = LogFactory.getLog(getClass());
    private final StaffRepository staffRepository;
    private final StaffRepositoryImpl staffRepositoryImpl;
    private final CommonEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final ModelMapper modelMapper;

    @Value("${spring.mail.username}")
    private String managerEmail;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return staffRepository.findByEmail(email);
    }

    /*로그인*/
    public Staff login(String email, String password) {
        String[] emailAndPassword = email.split(" ");
        if (staffRepository.existsByEmail(email) && checkPassword(emailAndPassword[0], password)) {
            return staffRepository.findByEmail(emailAndPassword[0]);
        }
        return null;
    }

    /*비밀번호 일치 여부*/
    private boolean checkPassword(String email, String password) {
        Staff staff = staffRepository.findByEmail(email);
        return passwordEncoder.matches(password, staff.getPasswordToCheckMatch());
    }

    /*이메일 토큰 전송*/
    public int sendEmailToken(String email){
        Staff staff=staffRepository.findByEmail(email);
        if(staff==null){
            return 9999;
        }
        staff.insert(UUID.randomUUID().toString(), LocalDateTime.now());

        CustomSimpleMailMessage mailMessage = CustomSimpleMailMessage.builder()
                .subject("[Billie] 이메일 인증 토큰입니다.")
                .to(new String[]{staff.getEmail()})
                .text("이메일 인증 토큰 : " + staff.getEmailToken())
                .build();

        javaMailSender.send(mailMessage);
        staffRepository.save(staff);
        return 0;
    }

    /*이메일 토큰 검증*/
    public int verifyEmailToken(EmailTokenForm emailTokenForm){
        Staff staff=staffRepository.findByEmail(emailTokenForm.getEmail());
        if(!staff.getEmailToken().equals(emailTokenForm.getEmailToken())){
            return 9999;
        }else if(ChronoUnit.MINUTES.between(staff.getEmailTokenGeneratedAt(), LocalDateTime.now())>=10){
            return 500;
        }
        staff.certify(1);
        staffRepository.save(staff);
        return 0;
    }

    /*회원가입*/
    public HttpBodyMessage joinIn(SignUpForm signUpForm) {
        Staff staff=staffRepository.findByEmail(signUpForm.getEmail());
        if(staff.getPasswordToCheckMatch() != null && staff.getRole() != null){
            return new HttpBodyMessage("fail", "exists join info");
        }else if(staff.getIsVerified() == 0){
            return new HttpBodyMessage("fail", "not verified");
        }

        if (staff.getDepartment().equals("관리부") || staff.getStaffNum().equals(4L) || staff.getRank().equals("대표")) {
            staff.setRole("ROLE_ADMIN");
        }else if (staff.getRank().equals("책임연구원") || staff.getRank().equals("실장") || staff.getRank().equals("부장")) {
            staff.setRole("ROLE_MANAGER");
        }else {
            staff.setRole("ROLE_USER");
        }

        staff.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        staffRepository.save(staff);
        return new HttpBodyMessage("success", "success sign up");
    }

    /*비밀번호 찾기*/
    public int findPassword(String address) {
        if (staffRepository.findByEmail(address) == null) {
            return 0;
        }
        Staff staff = staffRepository.findByEmail(address);

        Random random = new Random();
        String generatedString = random.ints(48, 122 + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        staff.setPassword(passwordEncoder.encode(generatedString));
        staffRepository.save(staff);

        CustomSimpleMailMessage mailMessage = CustomSimpleMailMessage.builder()
                .subject("[Billie] 임시 비밀번호를 발송해드립니다.")
                .from(managerEmail)
                .to(new String[]{address})
                .text("임시 비밀번호 : " + generatedString)
                .build();
        javaMailSender.send(mailMessage);
        return 1;
    }

    /*로그인 여부 체크*/
    public UserInfoForm checkLogin(String email) {
        Staff staff = staffRepository.findByEmail(email);
        return modelMapper.map(staff, UserInfoForm.class);
    }

    /*부서 목록 조회*/
    public List<DepartmentDTO> getDepartmentList(){
        return staffRepositoryImpl.getDepartmentNameList();
    }

    /*직급 목록 조회*/
    public List<RankDTO> getRankList(){
        return staffRepositoryImpl.getRankList();
    }
}
