package com.lab.smartmobility.billie.service.vacation;

import com.lab.smartmobility.billie.dto.PageResult;
import com.lab.smartmobility.billie.dto.vacation.VacationApplicationForm;
import com.lab.smartmobility.billie.entity.HttpBodyMessage;
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
import java.time.Period;
import java.util.List;

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
    public HttpBodyMessage apply(VacationApplicationForm vacationApplicationForm){
        if(isEarlierDate(vacationApplicationForm.getStartDate())){
            return new HttpBodyMessage("fail", "이전 날짜로 신청할 수 없습니다");
        }
        Staff applicant = staffRepository.findByStaffNum(vacationApplicationForm.getStaffNum());
        int vacationRequestCount = 0;
        if(applicant.getVacationCount() == 0){
            return new HttpBodyMessage("fail", "휴가 개수를 모두 소진했습니다");
        }

        Staff approval = assigneeToApprover.assignApproval(applicant);
        Vacation vacation = modelMapper.map(vacationApplicationForm, Vacation.class);

        insertVacationEntity(applicant, vacation);
        notificationSender.sendNotification("vacation", approval, 1);
        return new HttpBodyMessage("success", "휴가 신청 성공");
    }

    private int calculateVacationRequestCount(VacationApplicationForm vacationApplicationForm){
        return Period.between(vacationApplicationForm.getStartDate(), vacationApplicationForm.getEndDate()).getDays() == 0 ? 0
                : Period.between(vacationApplicationForm.getStartDate(), vacationApplicationForm.getEndDate()).getDays();
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

    /*나의 최근 휴가 신청 내역*/
    public List<Vacation> getMyRecentApplication(Long staffNum) {
        Staff staff = staffRepository.findByStaffNum(staffNum);
        return vacationRepository.findTop4ByStaffOrderByStartDate(staff);
    }

    // TODO 휴가 신청 내역 수정
    public HttpBodyMessage modify(Long vacationId, VacationApplicationForm vacationApplicationForm){
        Vacation vacation = vacationRepository.findByVacationId(vacationId);
        if(vacation.getApprovalStatus() == 'w'){
            Vacation modifiedVacation = modelMapper.map(vacationApplicationForm, vacation.getClass());
            vacationRepository.save(modifiedVacation);
            return new HttpBodyMessage("success", "승인 절차가 진행되지 않아 자동 수정되었습니다");
        }


        return new HttpBodyMessage("success", "취소 요청이 전송되었습니다");
    }

    // TODO 휴가 신청 내역 취소
    public HttpBodyMessage delete(Long vacationId){
        Vacation vacation = vacationRepository.findByVacationId(vacationId);
        if(vacation.getApprovalStatus() == 'w'){
            vacationRepository.delete(vacation);
            return new HttpBodyMessage("success", "승인 절차가 진행되지 않아 자동 취소되었습니다");
        }

        return new HttpBodyMessage("success", "취소 요청이 전송되었습니다");
    }
}
