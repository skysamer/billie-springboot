package com.lab.smartmobility.billie.vacation.service;

import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.vacation.dto.MyRecentVacationForm;
import com.lab.smartmobility.billie.vacation.dto.VacationApplicationDetailsForm;
import com.lab.smartmobility.billie.vacation.dto.VacationApplicationForm;
import com.lab.smartmobility.billie.entity.HttpBodyMessage;
import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.vacation.domain.ApprovalStatus;
import com.lab.smartmobility.billie.vacation.domain.Vacation;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import com.lab.smartmobility.billie.vacation.repository.VacationRepository;
import com.lab.smartmobility.billie.vacation.repository.VacationApplicationRepositoryImpl;
import com.lab.smartmobility.billie.global.util.AssigneeToApprover;
import com.lab.smartmobility.billie.global.util.NotificationSender;
import com.lab.smartmobility.billie.vacation.dto.VacationApplicationListForm;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        if(applicant.getVacationCount() == 0){
            return new HttpBodyMessage("fail", "휴가 개수를 모두 소진했습니다");
        }

//        Period period = Period.between(vacationApplicationForm.getStartDate(), vacationApplicationForm.getEndDate());
//        calculateVacationCount(applicant, vacationApplicationForm.getVacationType(), period.getDays());

        Staff approval = assigneeToApprover.assignApproval(applicant);
        Vacation vacation = modelMapper.map(vacationApplicationForm, Vacation.class);

        insertVacationEntity(applicant, vacation);
        notificationSender.sendNotification("vacation", approval, 1);
        return new HttpBodyMessage("success", "휴가 신청 성공");
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

    private boolean isEarlierDate(LocalDate startDate){
        return startDate.isBefore(LocalDate.now());
    }

    private void insertVacationEntity(Staff applicant, Vacation vacation){
        vacation.register(applicant);
        vacationRepository.save(vacation);
    }

    /*나의 휴가 신청 내역 전체 조회*/
    public PageResult<VacationApplicationListForm> getApplicationList(Long staffNum, String baseDate, String vacationType, Pageable pageable){
        return vacationApplicationRepository.getMyApplicationList(staffNum, baseDate, vacationType, pageable);
    }

    /*나의 휴가 신청 내역 상세 조회*/
    public VacationApplicationDetailsForm getMyApplication(Long vacationId){
        return vacationApplicationRepository.findById(vacationId);
    }

    /*나의 최근 휴가 신청 내역*/
    public List<MyRecentVacationForm> getMyRecentApplication(Long staffNum) {
        return vacationApplicationRepository.findMyRecentVacationList(staffNum);
    }

    /*휴가 신청 내역 취소*/
    public HttpBodyMessage cancel(Long vacationId){
        Vacation vacation = vacationRepository.findByVacationId(vacationId);
        if(vacation.getApprovalStatus().equals(ApprovalStatus.WAITING)){
            vacationRepository.delete(vacation);
            return new HttpBodyMessage("success", "휴가 삭제 완료");
        }

        vacation.cancel();
        return new HttpBodyMessage("success", "승인된 휴가에 대한 취소 처리 완료");
    }
}