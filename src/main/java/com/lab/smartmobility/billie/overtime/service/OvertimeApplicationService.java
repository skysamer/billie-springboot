package com.lab.smartmobility.billie.overtime.service;

import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.global.util.AssigneeToApprover;
import com.lab.smartmobility.billie.global.util.NotificationSender;
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
        Overtime overtime = modelMapper.map(applyForm, Overtime.class);
        overtime.calculateSubTime(applyForm.getStartTime(), applyForm.getEndTime(), applyForm.getIsMeal());

        Staff applicant = staffRepository.findByEmail(email);
        Staff approval = assigneeToApprover.assignApproval(applicant);

        notificationSender.sendNotification(DOMAIN_TYPE, approval, 1);
        overtimeRepository.save(overtime);
        return new HttpBodyMessage("success", "추가근무 신청 성공");
    }

    /*나의 추가근무 신청 목록 조회*/
    public PageResult<OvertimeApplicationListForm> getApplicationList(String email, String baseDate, Pageable pageable){
        return applicationRepository.getApplicationListPaging(email, baseDate, pageable);
    }
}
