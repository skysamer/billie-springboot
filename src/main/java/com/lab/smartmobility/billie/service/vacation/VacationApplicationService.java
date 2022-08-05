package com.lab.smartmobility.billie.service.vacation;

import com.lab.smartmobility.billie.dto.PageResult;
import com.lab.smartmobility.billie.dto.vacation.VacationApplicationForm;
import com.lab.smartmobility.billie.entity.HttpMessage;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.entity.Vacation;
import com.lab.smartmobility.billie.repository.StaffRepository;
import com.lab.smartmobility.billie.repository.vacation.VacationRepository;
import com.lab.smartmobility.billie.repository.vacation.VacationApplicationRepositoryImpl;
import com.lab.smartmobility.billie.util.AssigneeToApprover;
import com.lab.smartmobility.billie.util.NotificationSender;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class VacationApplicationService {
    private final VacationRepository vacationRepository;
    private final VacationApplicationRepositoryImpl vacationApplicationRepository;
    private final StaffRepository staffRepository;
    private final NotificationSender notificationSender;
    private final ModelMapper modelMapper;
    private final AssigneeToApprover assigneeToApprover;
    private final Log log;

    /*휴가 신청*/
    public HttpMessage apply(VacationApplicationForm vacationApplicationForm){
        if(isEarlierDate(vacationApplicationForm.getStartDate())){
            return new HttpMessage("fail", "이전 날짜로 신청할 수 없습니다");
        }
        Staff applicant = staffRepository.findByStaffNum(vacationApplicationForm.getStaffNum());
        Staff approval = assigneeToApprover.assignApproval(applicant);
        Vacation vacation = modelMapper.map(vacationApplicationForm, Vacation.class);

        insertVacationEntity(applicant, vacation);
        notificationSender.sendVacationNotification(applicant, approval, vacation);
        return new HttpMessage("success", "휴가 신청 성공");
    }

    private boolean isEarlierDate(LocalDate startDate){
        return startDate.isBefore(LocalDate.now());
    }

    private void insertVacationEntity(Staff applicant, Vacation vacation){
        vacation.register(applicant);
        vacationRepository.save(vacation);
    }

    /*나의 휴가 신청 내역 전체 조회*/
    public PageResult<Vacation> getApplicationList(Long staffNum, String baseDate, String vacationType, Pageable pageable){
        return vacationApplicationRepository.getMyApplicationList(staffNum, baseDate, vacationType, pageable);
    }

    /*나의 휴가 신청 내역 상세 조회*/
    public Vacation getMyApplication(Long vacationId){
        return vacationRepository.findByVacationId(vacationId);
    }

    /*휴가 신청 내역 수정*/
    public HttpMessage modify(Long vacationId, VacationApplicationForm vacationApplicationForm){
        Vacation vacation = vacationRepository.findByVacationId(vacationId);
        if(vacation.getApprovalStatus() != 'w'){
            return new HttpMessage("fail", "승인절차가 진행되었으므로 취소 승인 후 재작성되어야 합니다");
        }

        Vacation modifiedVacation = modelMapper.map(vacationApplicationForm, vacation.getClass());
        vacationRepository.save(modifiedVacation);
        return new HttpMessage("success", "");
    }
}
