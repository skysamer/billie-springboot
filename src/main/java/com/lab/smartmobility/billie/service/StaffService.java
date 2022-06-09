package com.lab.smartmobility.billie.service;

import com.lab.smartmobility.billie.config.CommonEncoder;
import com.lab.smartmobility.billie.dto.staff.DepartmentDTO;
import com.lab.smartmobility.billie.dto.staff.RankDTO;
import com.lab.smartmobility.billie.dto.staff.EmailTokenForm;
import com.lab.smartmobility.billie.dto.staff.SignUpForm;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.repository.StaffRepository;
import com.lab.smartmobility.billie.util.CustomSimpleMailMessage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class StaffService implements UserDetailsService {
    private final Log log = LogFactory.getLog(getClass());
    private final StaffRepository staffRepository;
    private final CommonEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String managerEmail;

    /*로그인*/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!staffRepository.existsByEmail(username)) {
            return null;
        }
        return staffRepository.findByEmail(username);
    }

    /*이메일 토큰 전송*/
    public int sendEmailToken(String email){
        Staff staff=staffRepository.findByEmail(email);
        if(staff==null){
            return 9999;
        }
        staff.setEmailToken(UUID.randomUUID().toString());
        staff.setEmailTokenGeneratedAt(LocalDateTime.now());
        staffRepository.save(staff);

        CustomSimpleMailMessage mailMessage = CustomSimpleMailMessage.builder()
                .subject("[Billie] 이메일 인증 토큰입니다.")
                .to(new String[]{staff.getEmail()})
                .text("이메일 인증 토큰 : " + staff.getEmailToken())
                .build();
        javaMailSender.send(mailMessage);
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
        staff.setIsVerified(1);
        staffRepository.save(staff);
        return 0;
    }

    public boolean checkPassword(String email, String password) {
        Staff staff = staffRepository.findByEmail(email);
        return passwordEncoder.matches(password, staff.getPasswordToCheckMatch());
    }

    /*회원가입*/
    public int joinIn(SignUpForm signUpForm) {
        Staff staff=staffRepository.findByEmail(signUpForm.getEmail());
        if(staff.getPasswordToCheckMatch()!=null && staff.getRole()!=null){
            return 9999;
        }else if(staff.getIsVerified()==0){
            return 500;
        }

        if (staff.getDepartment().equals("관리부") || (staff.getDepartment().equals("관리부") && staff.getRank().equals("부장"))) {
            staff.setRole("ROLE_ADMIN");
        } else if (staff.getRank().equals("책임연구원") || staff.getRank().equals("실장") || staff.getRank().equals("부장")) {
            staff.setRole("ROLE_MANAGER");
        }  else {
            staff.setRole("ROLE_USER");
        }

        staff.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        staffRepository.save(staff);
        return 0;
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
    public HashMap<String, Object> checkLogin(String email) {
        try {
            Staff staff = staffRepository.findByEmail(email);

            HashMap<String, Object> staffInfo = new HashMap<>();
            staffInfo.put("name", staff.getName());
            staffInfo.put("department", staff.getDepartment());
            staffInfo.put("role", staff.getRole());
            staffInfo.put("staffNum", staff.getStaffNum());
            staffInfo.put("isAuth", true);
            return staffInfo;
        } catch (Exception e) {
            e.printStackTrace();
            HashMap<String, Object> staffInfo = new HashMap<>();
            staffInfo.put("isAuth", false);
            return staffInfo;
        }
    }

    /*부서 목록 조회*/
    public List<DepartmentDTO> getDepartmentList(){
        List<Staff> staffList=staffRepository.findAll();
        List<DepartmentDTO> departmentList=new ArrayList<>();
        for(Staff staff : staffList){
            DepartmentDTO department=new DepartmentDTO(staff.getDepartment());
            departmentList.add(department);
        }
        return departmentList.stream().distinct().collect(Collectors.toList());
    }

    /*직급 목록 조회*/
    public List<RankDTO> getRankList(){
        List<Staff> staffList=staffRepository.findAll();
        List<RankDTO> rankList=new ArrayList<>();
        for(Staff staff : staffList){
            RankDTO department=new RankDTO(staff.getRank());
            rankList.add(department);
        }
        return rankList.stream().distinct().collect(Collectors.toList());
    }
}
