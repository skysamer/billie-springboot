package com.lab.smartmobility.billie.service.corporation;

import com.lab.smartmobility.billie.dto.TotalCount;
import com.lab.smartmobility.billie.dto.corporation.ApprovalCardUseForm;
import com.lab.smartmobility.billie.dto.corporation.CompanionCardUseForm;
import com.lab.smartmobility.billie.entity.HttpBodyMessage;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.entity.corporation.Application;
import com.lab.smartmobility.billie.entity.corporation.CorporationCard;
import com.lab.smartmobility.billie.repository.StaffRepository;
import com.lab.smartmobility.billie.repository.corporation.*;
import com.lab.smartmobility.billie.util.NotificationSender;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CorporationCardApprovalService {
    private final CorporationCardRepository cardRepository;
    private final ApplicationRepository applicationRepository;
    private final StaffRepository staffRepository;
    private final ApplicationRepositoryImpl applicationRepositoryImpl;
    private final NotificationSender notificationSender;
    private final Log log;

    private static final Long ADMIN_ID = 4L;
    private static final String NOTIFICATION_DOMAIN_TYPE = "corporation";

    /*부서장의 요청 관리 목록 조회*/
    public List<Application> getListOfApprovalsRequestByManager(Long managerNum, String cardName, String baseYear, int disposalInfo, Pageable pageable){
        Staff manager = staffRepository.findByStaffNum(managerNum);
        return applicationRepositoryImpl.getApplicationListByManager(manager.getDepartment(), "ROLE_USER",
                cardName, baseYear, disposalInfo, pageable);
    }

    /*부서장의 요청 관리 목록 개수 조회*/
    public TotalCount getCountOfApprovalsRequestByManager(Long managerNum, String cardName, String baseYear, int disposalInfo){
        Staff manager = staffRepository.findByStaffNum(managerNum);
        return new TotalCount(
                applicationRepositoryImpl.getApplicationCountByManager(manager.getDepartment(), "ROLE_USER", cardName, baseYear, disposalInfo));
    }

    /*부서장의 카드 사용 승인*/
    public HttpBodyMessage approveCardUseByManager(List<ApprovalCardUseForm> approvalCardUseForms){
        try{
            for(ApprovalCardUseForm approvalCardUseForm : approvalCardUseForms){
                Application application = applicationRepository.findByApplicationId(approvalCardUseForm.getApplicationId());
                application.approveByManager('t');

                Staff admin = staffRepository.findByStaffNum(ADMIN_ID);
                notificationSender.sendNotification(NOTIFICATION_DOMAIN_TYPE, admin, 1);
            }
        }catch (Exception e){
            log.error(e);
            return new HttpBodyMessage("fail", "fail-approve");
        }
        return new HttpBodyMessage("success", "success-approve");
    }

    /*카드 사용 반려*/
    public HttpBodyMessage rejectCardUse(List<CompanionCardUseForm> companionCardUseForms){
        try{
            for(CompanionCardUseForm companionCardUseForm : companionCardUseForms){
                Application application = applicationRepository.findByApplicationId(companionCardUseForm.getApplicationId());
                application.reject('c', companionCardUseForm.getReason());
                Staff requester = application.getStaff();

                notificationSender.sendNotification(NOTIFICATION_DOMAIN_TYPE, requester, 0);
            }
        }catch (Exception e){
            log.error(e);
            return new HttpBodyMessage("fail", "fail-reject");
        }
        return new HttpBodyMessage("success", "success-reject");
    }

    /*관리자의 요청 관리 목록 조회*/
    public List<Application> getListOfApprovalsRequestByAdmin(String cardName, String baseYear, int disposalInfo, Pageable pageable){
        return applicationRepositoryImpl.getApplicationListAdmin(cardName, baseYear, disposalInfo, pageable);
    }

    /*관리자의 요청 관리 목록 전체 개수 조회*/
    public TotalCount getCountOfApprovalsRequestByAdmin(String cardName, String baseYear, int disposalInfo){
        return new TotalCount(applicationRepositoryImpl.getApplicationCountAdmin(cardName, baseYear, disposalInfo));
    }

    /*관리자의 최종 사용 승인*/
    public HttpBodyMessage approveCardUseByAdmin(List<ApprovalCardUseForm> approvalCardUseForms){
        for(ApprovalCardUseForm approvalCardUseForm : approvalCardUseForms){
            Application toBeApproveApplication = applicationRepository.findByApplicationId(approvalCardUseForm.getApplicationId());
            CorporationCard card = cardRepository.findByCardNameAndCompany(approvalCardUseForm.getCardName(), approvalCardUseForm.getCompany());

            if(card == null){
                toBeApproveApplication.approveExpenseByAdmin(1, 'f');
                Staff requester = toBeApproveApplication.getStaff();

                notificationSender.sendNotification(NOTIFICATION_DOMAIN_TYPE, requester, 0);
                continue;
            }

            if(checkReservationIsDuplicate(card,
                    toBeApproveApplication.getStartDate(), toBeApproveApplication.getEndDate(),
                    toBeApproveApplication.getStartTime(), toBeApproveApplication.getEndTime())){
                throw new RuntimeException();
            }
            toBeApproveApplication.approveCorporationByAdmin(card, 'f');
            Staff requester = toBeApproveApplication.getStaff();

            notificationSender.sendNotification(NOTIFICATION_DOMAIN_TYPE, requester, 0);
        }
        return new HttpBodyMessage("success", "success-final-approve");
    }

    /*시간대가 겹치는지 체크*/
    private boolean checkReservationIsDuplicate(CorporationCard card, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime){
        return applicationRepositoryImpl.isDuplicate(card, 0, startDate, endDate, startTime, endTime) == 1;
    }

    /*승인된 법인카드 신청 내역 월별 조회*/
    public List<Application> getApprovedApplicationListMonthly(LocalDate startDate, LocalDate endDate){
        return applicationRepository.findAllByApprovalStatusAndStartDateBetween('f', startDate, endDate);
    }

    /*승인된 법인카드 신청 내역 상세 조회*/
    public Application getApprovedApplication(Long applicationId){
        return applicationRepository.findByApplicationId(applicationId);
    }

    /*내가 사용중인 법인카드 내역 조회*/
    public List<Application> getMyCorporationCard(Long staffNum){
        Staff my = staffRepository.findByStaffNum(staffNum);
        return applicationRepository.findAllByStaffAndIsReturned(my, 0);
    }
}
