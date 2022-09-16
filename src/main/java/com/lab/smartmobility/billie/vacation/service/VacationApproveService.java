package com.lab.smartmobility.billie.vacation.service;

import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.global.util.AssigneeToApprover;
import com.lab.smartmobility.billie.global.util.NotificationSender;
import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import com.lab.smartmobility.billie.vacation.domain.ApprovalStatus;
import com.lab.smartmobility.billie.vacation.domain.Vacation;
import com.lab.smartmobility.billie.vacation.dto.VacationApproveListForm;
import com.lab.smartmobility.billie.vacation.dto.VacationCompanionForm;
import com.lab.smartmobility.billie.vacation.repository.VacationApproveRepository;
import com.lab.smartmobility.billie.vacation.repository.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VacationApproveService {
    private final VacationRepository vacationRepository;
    private final VacationApproveRepository approveRepository;
    private final StaffRepository staffRepository;
    private final AssigneeToApprover assigneeToApprover;
    private final NotificationSender notificationSender;
    private final Log log;

    private static final String DOMAIN_TYPE = "vacation";

    /*부서장의 승인 요청 목록 조회*/
    public PageResult<VacationApproveListForm> getApproveListByManager(String baseDate, String email, String keyword, int isToggleOn, Pageable pageable){
        Staff manager = staffRepository.findByEmail(email);
        return approveRepository.getApproveListByManagerResult(baseDate, manager.getDepartment(), keyword, isToggleOn, pageable);
    }

    /*부서장의 휴가 승인*/
    public HttpBodyMessage approveByManager(List<Long> vacationIdList, String email){
        approveRepository.approveByManager(vacationIdList);
        sendNotification(email);
        return new HttpBodyMessage("success", "휴가승인성공");
    }

    private void sendNotification(String email){
        Staff applicant = staffRepository.findByEmail(email);
        Staff approval = assigneeToApprover.assignApproval(applicant);
        notificationSender.sendNotification(DOMAIN_TYPE, approval, 1);
    }

    /*휴가 반려*/
    public HttpBodyMessage reject(List<VacationCompanionForm> companionFormList){
        for(VacationCompanionForm companionForm : companionFormList){
            Vacation vacation = vacationRepository.findByVacationId(companionForm.getVacationId());
            vacation.reject(companionForm.getCompanionReason());
            notificationSender.sendNotification(DOMAIN_TYPE, vacation.getStaff(), 0);
        }
        return new HttpBodyMessage("success", "반려성공");
    }

    /*관리자 휴가 승인 요청 목록 조회*/
    public void getApproveListByAdmin(String baseDate, String department, String keyword, Pageable pageable){

    }

    private void calculateVacationCount(Staff applicant, String vacationType, int period){
        if(vacationType.equals("반차")){
            applicant.calculateVacation(0.5);
        }else if(vacationType.equals("경조") || vacationType.equals("공가")){
            applicant.calculateVacation(0);
        }else{
            applicant.calculateVacation(period + 1);
        }
    }
}
