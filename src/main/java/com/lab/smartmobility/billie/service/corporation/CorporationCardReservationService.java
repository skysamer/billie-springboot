package com.lab.smartmobility.billie.service.corporation;

import com.lab.smartmobility.billie.global.dto.TotalCount;
import com.lab.smartmobility.billie.dto.corporation.ApplyCorporationCardForm;
import com.lab.smartmobility.billie.entity.HttpBodyMessage;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.entity.corporation.Application;
import com.lab.smartmobility.billie.user.repository.StaffRepository;
import com.lab.smartmobility.billie.repository.corporation.*;
import com.lab.smartmobility.billie.global.util.DateTimeUtil;
import com.lab.smartmobility.billie.global.util.NotificationSender;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CorporationCardReservationService {
    private final ApplicationRepository applicationRepository;
    private final StaffRepository staffRepository;
    private final ApplicationRepositoryImpl applicationRepositoryImpl;
    private final NotificationSender notificationSender;
    private final DateTimeUtil dateTimeUtil;

    private final ModelMapper modelMapper;
    private final Log log;

    private static final Long ADMIN_ID = 4L;

    /*법인카드 사용 신청*/
    public HttpBodyMessage applyCardReservation(ApplyCorporationCardForm applyCorporationCardForm) {
        LocalDateTime applyDate = dateTimeUtil.combineDateAndTime(applyCorporationCardForm.getStartDate(), applyCorporationCardForm.getStartTime());
        if(applyDate.isBefore(LocalDateTime.now())){
            return new HttpBodyMessage("fail", "cannot-reservation-earlier-day");
        }

        Application application=modelMapper.map(applyCorporationCardForm, Application.class);
        Staff requester=staffRepository.findByStaffNum(applyCorporationCardForm.getStaffNum());

        application.assignRequester(requester);
        Staff approval = assignApproval(requester);
        if(requester.getRole().equals("ROLE_ADMIN")){
            application.updateApprovalStatus('t');
        }

        try{
            applicationRepository.save(application);
            notificationSender.sendNotification("corporation", approval, 1);
        }catch (Exception e){
            log.error("fail : "+e);
            return new HttpBodyMessage("fail", "fail-application");
        }
        return new HttpBodyMessage("success", "success-application");
    }

    /*후불 경비청구 신청*/
    public HttpBodyMessage applyPostExpenseClaim(ApplyCorporationCardForm applyCorporationCardForm){
        Application application=modelMapper.map(applyCorporationCardForm, Application.class);

        Staff requester=staffRepository.findByStaffNum(applyCorporationCardForm.getStaffNum());
        application.insertRequesterAndPostExpense(requester, 99);

        Staff approval= assignApproval(requester);
        if(requester.getRole().equals("ROLE_ADMIN")){
            application.updateApprovalStatus('t');
        }

        try{
            applicationRepository.save(application);
            notificationSender.sendNotification("corporation", approval, 1);
        }catch (Exception e){
            log.error("fail : "+e);
            return new HttpBodyMessage("fail", "fail-application");
        }
        return new HttpBodyMessage("success", "success-application");
    }

    /*승인권자 할당*/
    private Staff assignApproval(Staff requester){
        if(requester.getDepartment().equals("관리부") || requester.getRole().equals("ROLE_MANAGER")){
            return staffRepository.findByStaffNum(ADMIN_ID);
        }
        return staffRepository.findByDepartmentAndRole(requester.getDepartment(), "ROLE_MANAGER");
    }

    /*나의 사용신청 목록 조회*/
    public List<Application> myApplicationList(Long staffNum, String cardName, String baseYear, Pageable pageable){
        Staff my = staffRepository.findByStaffNum(staffNum);
        return applicationRepositoryImpl.getMyApplicationList(my, cardName, baseYear, pageable);
    }

    /*나의 사용신청 목록 카운팅*/
    public TotalCount myApplicationCount(Long staffNum, String cardName, String baseYear){
        Staff my = staffRepository.findByStaffNum(staffNum);
        return new TotalCount(applicationRepositoryImpl.getMyApplicationCount(my, cardName, baseYear));
    }

    /*나의 사용신청 내역 상세 조회*/
    public Application myApplication(Long staffNum, Long applicationId){
        Staff staff=staffRepository.findByStaffNum(staffNum);
        return applicationRepository.findByStaffAndApplicationId(staff, applicationId);
    }

    /*나의 사용신청 내역 수정*/
    public HttpBodyMessage modifyCardUseApplicationInfo(Long applicationId, ApplyCorporationCardForm applyCorporationCardForm){
        if(applyCorporationCardForm.getStartDate().isBefore(LocalDate.now())){
            return new HttpBodyMessage("fail", "cannot-reservation-earlier-day");
        }

        Application application=applicationRepository.findByApplicationId(applicationId);
        if(application.getApprovalStatus()!='w'){
            return new HttpBodyMessage("fail", "this-application-is-approved");
        }
        if(!application.getStaff().getStaffNum().equals(applyCorporationCardForm.getStaffNum())){
            return new HttpBodyMessage("fail", "not-the-employee");
        }

        modelMapper.map(applyCorporationCardForm, application);
        applicationRepository.save(application);
        return new HttpBodyMessage("success", "modify-application");
    }

    /*나의 사용신청 내역 삭제*/
    public HttpBodyMessage removeCardUseApplicationInfo(Long applicationId){
        Application application=applicationRepository.findByApplicationId(applicationId);
        if(application.getApprovalStatus()!='w'){
            return new HttpBodyMessage("fail", "this-application-is-approved");
        }

        applicationRepository.delete(application);
        return new HttpBodyMessage("success", "remove-application");
    }

    /*관리자의 신청삭제*/
    public HttpBodyMessage removeCardUseApplicationByAdmin(Long applicationId){
        Application application=applicationRepository.findByApplicationId(applicationId);
        if(application==null){
            return new HttpBodyMessage("fail", "not-exist-info");
        }

        applicationRepository.delete(application);
        return new HttpBodyMessage("success", "remove-application");
    }

    /*관리자의 신청 수정*/
    public HttpBodyMessage modifyCardUseApplicationByAdmin(Long applicationId, ApplyCorporationCardForm applyCorporationCardForm){
        Application application=applicationRepository.findByApplicationId(applicationId);
        if(application==null){
            return new HttpBodyMessage("fail", "not-exist-info");
        }

        modelMapper.map(applyCorporationCardForm, application);
        applicationRepository.save(application);
        return new HttpBodyMessage("success", "modify-application");
    }
}
