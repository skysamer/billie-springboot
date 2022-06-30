package com.lab.smartmobility.billie.service;

import com.lab.smartmobility.billie.dto.ApplyOvertimeForm;
import com.lab.smartmobility.billie.entity.Notification;
import com.lab.smartmobility.billie.entity.Overtime;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.repository.OvertimeRepository;
import com.lab.smartmobility.billie.repository.StaffRepository;
import com.lab.smartmobility.billie.util.SseEmitterSender;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OvertimeService {
    private final OvertimeRepository overtimeRepository;
    private final StaffRepository staffRepository;
    private final ModelMapper modelMapper;
    private final SseEmitterSender sseEmitterSender;
    private final Log log= LogFactory.getLog(getClass());

    /*이번달 추가근무 시간*/
    public double getMyOvertimeCount(Long staffNum){
        return staffRepository.findByStaffNum(staffNum).getOvertimeCount();
    }

    /*추가근무 신청*/
    public int applyOvertime(ApplyOvertimeForm applyOvertimeForm){
        try {
            Staff requester=staffRepository.findByStaffNum(applyOvertimeForm.getStaffNum());
            Staff manager=staffRepository.findByDepartmentAndRole(requester.getDepartment(), "ROLE_MANAGER");
            if(requester.getDepartment().equals("관리부") || requester.getRole().equals("ROLE_MANAGER")){
                manager = staffRepository.findByStaffNum(37L); // 부장님은 4
            }
            Overtime overtime=new Overtime();
            modelMapper.map(applyOvertimeForm, overtime);
            overtime.setApprovalStatus('W');
            overtime.setStaff(requester);

            sendNotification(requester, manager);
            overtimeRepository.save(overtime);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
        return 0;
    }

    private void sendNotification(Staff requester, Staff receiver){
        Notification notification=Notification.builder()
                .type("overtime")
                .createdAt(LocalDateTime.now())
                .readAt(0)
                .build();
    }

    /*나의 추가근무 신청 목록 조회*/
    public List<Overtime> getMyOvertimeList(Long staffNum, LocalDate startDate, LocalDate endDate, char approvalStatus, PageRequest pageRequest){
        Staff staff=staffRepository.findByStaffNum(staffNum);
        if(approvalStatus=='A'){
            return overtimeRepository.findAllByStaffAndDateOfOvertimeBetweenOrderByOvertimeNumDesc(staff, startDate, endDate, pageRequest);
        }
        return overtimeRepository.findAllByStaffAndApprovalStatusAndDateOfOvertimeBetweenOrderByOvertimeNumDesc(staff, approvalStatus, startDate, endDate, pageRequest);
    }

    /*추가근무 신청 상세 조회*/
    public Overtime getMyOvertime(Long overtimeNum){
        return overtimeRepository.findByOvertimeNum(overtimeNum);
    }

    /*추가근무 신청 수정*/
    public void modifyOvertimeApplication(Long overtimeNum, ApplyOvertimeForm applyOvertimeForm){
        Overtime overtime=overtimeRepository.findByOvertimeNum(overtimeNum);
        Staff staff=staffRepository.findByStaffNum(applyOvertimeForm.getStaffNum());

        modelMapper.map(applyOvertimeForm, overtime);
        overtime.setStaff(staff);
        overtimeRepository.save(overtime);
    }

    /*부서장의 추가근무 승인*/
    public void approveByManager(){

    }
}
