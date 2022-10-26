package com.lab.smartmobility.billie.vacation.service;

import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.vacation.dto.MyRecentVacationForm;
import com.lab.smartmobility.billie.vacation.dto.VacationApplicationDetailsForm;
import com.lab.smartmobility.billie.vacation.dto.VacationApplicationForm;
import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.vacation.domain.Vacation;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import com.lab.smartmobility.billie.vacation.repository.VacationReportRepository;
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

import java.time.Period;
import java.util.List;

import static com.lab.smartmobility.billie.vacation.domain.ApprovalStatus.FINAL;
import static com.lab.smartmobility.billie.vacation.domain.ApprovalStatus.WAITING;

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
    private final VacationCalculateService calculateService;
    private final Log log;

    private static final String DOMAIN_TYPE = "vacation";

    /*휴가 신청*/
    public HttpBodyMessage apply(VacationApplicationForm vacationApplicationForm){
        Staff applicant = staffRepository.findByStaffNum(vacationApplicationForm.getStaffNum());
        if(applicant.getVacationCount() == 0 &&
                (vacationApplicationForm.getVacationType().equals("연차") || vacationApplicationForm.getVacationType().equals("반차"))){
            return new HttpBodyMessage("fail", "휴가 개수를 모두 소진했습니다");
        }
        Staff approval = assigneeToApprover.assignApproval(applicant);
        Vacation vacation = modelMapper.map(vacationApplicationForm, Vacation.class);

        calculateCount(vacation, applicant);
        insertVacationEntity(applicant, vacation);
        notificationSender.sendNotification(DOMAIN_TYPE, approval, 1);
        return new HttpBodyMessage("success", "휴가 신청 성공");
    }

    private void calculateCount(Vacation vacation, Staff applicant){
        Period period = Period.between(vacation.getStartDate(), vacation.getEndDate());
        double count = calculateService.calculateVacationCount(applicant, vacation.getVacationType(), period.getDays());
        vacation.setCount(count);
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
        if(vacation.getApprovalStatus().equals(WAITING)){
            vacationRepository.delete(vacation);
            return new HttpBodyMessage("success", "휴가 삭제 완료");
        }else if(vacation.getApprovalStatus().equals(FINAL)){
            restoreVacationCount(vacation);
        }

        vacation.cancel();
        return new HttpBodyMessage("success", "승인된 휴가에 대한 취소 처리 완료");
    }

    private void restoreVacationCount(Vacation vacation){
        Staff staff = staffRepository.findByStaffNum(vacation.getStaff().getStaffNum());
        Period period = Period.between(vacation.getStartDate(), vacation.getEndDate());
        calculateService.restoreVacationCount(staff, vacation.getVacationType(), period.getDays());
    }
}
