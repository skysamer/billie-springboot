package com.lab.smartmobility.billie.overtime.service;

import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.global.util.AssigneeToApprover;
import com.lab.smartmobility.billie.global.util.NotificationSender;
import com.lab.smartmobility.billie.overtime.domain.ApprovalStatus;
import com.lab.smartmobility.billie.overtime.domain.Overtime;
import com.lab.smartmobility.billie.overtime.dto.OvertimeApplicationListForm;
import com.lab.smartmobility.billie.overtime.dto.OvertimeApplyForm;
import com.lab.smartmobility.billie.overtime.repository.OvertimeApplicationRepository;
import com.lab.smartmobility.billie.overtime.repository.OvertimeRepository;
import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
@Transactional
@RequiredArgsConstructor
public class OvertimeApplicationService {
    private final OvertimeRepository overtimeRepository;
    private final OvertimeApplicationRepository applicationRepository;
    private final StaffRepository staffRepository;
    private final NotificationSender notificationSender;
    private final ModelMapper modelMapper;
    private final AssigneeToApprover assigneeToApprover;
    private final Log log;

    private static final String DOMAIN_TYPE = "overtime";

    /*추가근무 신청*/
    public HttpBodyMessage apply(String email, OvertimeApplyForm applyForm){
        Staff applicant = staffRepository.findByEmail(email);
        Overtime overtime = modelMapper.map(applyForm, Overtime.class);
        overtime.setApplicant(applicant);
        overtime.calculateSubTime(applyForm.getStartTime(), applyForm.getEndTime(), applyForm.getIsMeal());

        Staff approval = assigneeToApprover.assignApproval(applicant);
        notificationSender.sendNotification(DOMAIN_TYPE, approval, 1);
        overtimeRepository.save(overtime);
        return new HttpBodyMessage("success", "추가근무 신청 성공");
    }

    /*나의 추가근무 신청 목록 조회*/
    public PageResult<OvertimeApplicationListForm> getApplicationList(String email, String baseDate, Pageable pageable){
        return applicationRepository.getApplicationListPaging(email, baseDate, pageable);
    }

    /*추가근무 신청 내역 삭제*/
    public HttpBodyMessage remove(Long id){
        Overtime overtime = overtimeRepository.findById(id).orElseThrow();
        overtimeRepository.delete(overtime);
        return new HttpBodyMessage("success", "삭제성공");
    }

    /*근무확정*/
    public HttpBodyMessage confirm(Long id, OvertimeApplyForm applyForm){
        Overtime overtime = overtimeRepository.findById(id).orElseThrow();
        if(overtime.getApprovalStatus().equals(ApprovalStatus.PRE)){
            modelMapper.map(applyForm, overtime);
            overtime.calculateSubTime(applyForm.getStartTime(), applyForm.getEndTime(), applyForm.getIsMeal());
            overtime.confirm();
            return new HttpBodyMessage("fail", "근무확정");
        }
        return new HttpBodyMessage("success", "사전승인이 되어야만 근무확정을 할 수 있습니다");
    }
}
