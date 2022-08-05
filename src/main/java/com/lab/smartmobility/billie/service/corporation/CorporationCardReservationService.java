package com.lab.smartmobility.billie.service.corporation;

import com.lab.smartmobility.billie.dto.NotificationEventDTO;
import com.lab.smartmobility.billie.dto.TotalCount;
import com.lab.smartmobility.billie.dto.corporation.ApplyCorporationCardForm;
import com.lab.smartmobility.billie.entity.HttpMessage;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.entity.corporation.Application;
import com.lab.smartmobility.billie.repository.StaffRepository;
import com.lab.smartmobility.billie.repository.corporation.*;
import com.lab.smartmobility.billie.util.DateTimeUtil;
import com.lab.smartmobility.billie.util.NotificationSender;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
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

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ModelMapper modelMapper;
    private final Log log;

    private static final Long ADMIN_ID = 4L;

    /*법인카드 사용 신청*/
    public HttpMessage applyCardReservation(ApplyCorporationCardForm applyCorporationCardForm) {
        LocalDateTime applyDate = dateTimeUtil.combineDateAndTime(applyCorporationCardForm.getStartDate(), applyCorporationCardForm.getStartTime());
        if(applyDate.isBefore(LocalDateTime.now())){
            return new HttpMessage("fail", "cannot-reservation-earlier-day");
        }

        Application application=modelMapper.map(applyCorporationCardForm, Application.class);
        Staff requester=staffRepository.findByStaffNum(applyCorporationCardForm.getStaffNum());

        application.assignRequester(requester);
        Staff approval= assignApproval(requester);
        if(requester.getRole().equals("ROLE_ADMIN")){
            application.updateApprovalStatus('t');
        }

        NotificationEventDTO notificationEvent = NotificationEventDTO.builder()
                .requester(requester.getName()).receiver(approval.getName())
                .approvalStatus(application.getApprovalStatus()).type("corporation").approval(approval)
                .build();
        try{
            applicationRepository.save(application);
            applicationEventPublisher.publishEvent(notificationEvent);
        }catch (Exception e){
            log.error("fail : "+e);
            return new HttpMessage("fail", "fail-application");
        }
        return new HttpMessage("success", "success-application");
    }

    /*후불 경비청구 신청*/
    public HttpMessage applyPostExpenseClaim(ApplyCorporationCardForm applyCorporationCardForm){
        Application application=modelMapper.map(applyCorporationCardForm, Application.class);

        Staff requester=staffRepository.findByStaffNum(applyCorporationCardForm.getStaffNum());
        application.insertRequesterAndPostExpense(requester, 99);

        Staff approval= assignApproval(requester);
        if(requester.getRole().equals("ROLE_ADMIN")){
            application.updateApprovalStatus('t');
        }

        NotificationEventDTO notificationEvent = NotificationEventDTO.builder()
                .requester(requester.getName()).receiver(approval.getName())
                .approvalStatus(application.getApprovalStatus()).type("corporation").approval(approval)
                .build();

        try{
            applicationRepository.save(application);
            applicationEventPublisher.publishEvent(notificationEvent);
        }catch (Exception e){
            log.error("fail : "+e);
            return new HttpMessage("fail", "fail-application");
        }
        return new HttpMessage("success", "success-application");
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
        Staff my=staffRepository.findByStaffNum(staffNum);
        return applicationRepositoryImpl.getMyApplicationList(my, cardName, baseYear, pageable);
    }

    /*나의 사용신청 목록 카운팅*/
    public TotalCount myApplicationCount(Long staffNum, String cardName, String baseYear){
        Staff my=staffRepository.findByStaffNum(staffNum);
        return new TotalCount(applicationRepositoryImpl.getMyApplicationCount(my, cardName, baseYear));
    }

    /*나의 사용신청 내역 상세 조회*/
    public Application myApplication(Long staffNum, Long applicationId){
        Staff staff=staffRepository.findByStaffNum(staffNum);
        return applicationRepository.findByStaffAndApplicationId(staff, applicationId);
    }

    /*나의 사용신청 내역 수정*/
    public HttpMessage modifyCardUseApplicationInfo(Long applicationId, ApplyCorporationCardForm applyCorporationCardForm){
        if(applyCorporationCardForm.getStartDate().isBefore(LocalDate.now())){
            return new HttpMessage("fail", "cannot-reservation-earlier-day");
        }

        Application application=applicationRepository.findByApplicationId(applicationId);
        if(application.getApprovalStatus()!='w'){
            return new HttpMessage("fail", "this-application-is-approved");
        }
        if(!application.getStaff().getStaffNum().equals(applyCorporationCardForm.getStaffNum())){
            return new HttpMessage("fail", "not-the-employee");
        }

        modelMapper.map(applyCorporationCardForm, application);
        applicationRepository.save(application);
        return new HttpMessage("success", "modify-application");
    }

    /*나의 사용신청 내역 삭제*/
    public HttpMessage removeCardUseApplicationInfo(Long applicationId){
        Application application=applicationRepository.findByApplicationId(applicationId);
        if(application.getApprovalStatus()!='w'){
            return new HttpMessage("fail", "this-application-is-approved");
        }

        applicationRepository.delete(application);
        return new HttpMessage("success", "remove-application");
    }

    /*관리자의 신청삭제*/
    public HttpMessage removeCardUseApplicationByAdmin(Long applicationId){
        Application application=applicationRepository.findByApplicationId(applicationId);
        if(application==null){
            return new HttpMessage("fail", "not-exist-info");
        }

        applicationRepository.delete(application);
        return new HttpMessage("success", "remove-application");
    }

    /*관리자의 신청 수정*/
    public HttpMessage modifyCardUseApplicationByAdmin(Long applicationId, ApplyCorporationCardForm applyCorporationCardForm){
        Application application=applicationRepository.findByApplicationId(applicationId);
        if(application==null){
            return new HttpMessage("fail", "not-exist-info");
        }

        modelMapper.map(applyCorporationCardForm, application);
        applicationRepository.save(application);
        return new HttpMessage("success", "modify-application");
    }
}
