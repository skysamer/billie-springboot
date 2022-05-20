package com.lab.smartmobility.billie.service;

import com.lab.smartmobility.billie.config.CommonEncoder;
import com.lab.smartmobility.billie.dto.DepartmentDTO;
import com.lab.smartmobility.billie.dto.RankDTO;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RequiredArgsConstructor
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

    public boolean checkPassword(String email, String password) {
        Staff staff = staffRepository.findByEmail(email);
        return passwordEncoder.matches(password, staff.getPassword());
    }

    /*회원가입*/
    public int joinIn(Staff staff) {
        String insertPassword = staff.getPassword();
        if (!staffRepository.existsByNameAndDepartmentAndBirthAndEmail(staff.getName(), staff.getDepartment(), staff.getBirth(), staff.getEmail())) {
            return 0;
        }

        Staff findStaff = staffRepository.findByEmail(staff.getEmail());
        if (findStaff.getPassword() != null && findStaff.getRole() != null) {
            return 400;
        }

        if (findStaff.getDepartment().equals("관리부") || (findStaff.getDepartment().equals("관리부") && findStaff.getRank().equals("부장"))) {
            findStaff.setRole("ROLE_ADMIN");
        } else if (findStaff.getRank().equals("책임연구원") || findStaff.getRank().equals("실장") || findStaff.getRank().equals("부장")) {
            findStaff.setRole("ROLE_MANAGER");
        }  else {
            findStaff.setRole("ROLE_USER");
        }

        findStaff.setPassword(passwordEncoder.encode(insertPassword));
        staffRepository.save(findStaff);
        return 1;
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
