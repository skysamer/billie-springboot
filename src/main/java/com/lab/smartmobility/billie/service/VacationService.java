package com.lab.smartmobility.billie.service;

import com.lab.smartmobility.billie.dto.ApplyVacationForm;
import com.lab.smartmobility.billie.dto.MyVacationDTO;
import com.lab.smartmobility.billie.entity.Notification;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.entity.Vacation;
import com.lab.smartmobility.billie.repository.NotificationRepository;
import com.lab.smartmobility.billie.repository.StaffRepository;
import com.lab.smartmobility.billie.repository.VacationRepository;
import com.lab.smartmobility.billie.util.SseEmitterSender;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VacationService {
    private final StaffRepository staffRepository;
    private final VacationRepository vacationRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final SseEmitterSender sseEmitterSender;
    private final ModelMapper modelMapper;
    private final Log log= LogFactory.getLog(getClass());

    /*나의 남은 휴가 개수, 전체 개수, 사용개수 및 소진기한*/
    public MyVacationDTO getMyVacationInfo(Long staffNum){
        Staff staff=staffRepository.findByStaffNum(staffNum);

        double totalVacationCount=0;
        int yearsOfService=Period.between(staff.getHireDate(), LocalDate.now()).getYears();
        log.info(yearsOfService);
        if(yearsOfService<1){
            totalVacationCount=12;
        }else if(yearsOfService<3){
            totalVacationCount=15;
        }else{
            totalVacationCount=15 + (int)((yearsOfService-3) / 2) + 1;
        }

        if(totalVacationCount>25){
            totalVacationCount=25;
        }
        int baseYear = ((LocalDate.of(LocalDate.now().get(ChronoField.YEAR), staff.getHireDate().get(ChronoField.MONTH_OF_YEAR), staff.getHireDate().get(ChronoField.DAY_OF_MONTH))).isAfter(LocalDate.now()))
                ? LocalDate.now().minusYears(1).getYear() : LocalDate.now().getYear();

        return MyVacationDTO.builder()
                .remainingVacationCount(staff.getVacationCount())
                .totalVacationCount(totalVacationCount)
                .numberOfUses(totalVacationCount - staff.getVacationCount())
                .startDate(LocalDate.of(baseYear, staff.getHireDate().get(ChronoField.MONTH_OF_YEAR), staff.getHireDate().get(ChronoField.DAY_OF_MONTH)))
                .endDate(LocalDate.of(baseYear+1, staff.getHireDate().get(ChronoField.MONTH_OF_YEAR), staff.getHireDate().get(ChronoField.DAY_OF_MONTH)).minusDays(1))
                .build();
    }

    /*휴가신청*/
    public int applyVacation(List<ApplyVacationForm> applyVacationFormList){
        Staff staff=staffRepository.findByStaffNum(applyVacationFormList.get(0).getStaffNum());
        Staff manager=staffRepository.findByDepartmentAndRole(staff.getDepartment(), "ROLE_MANAGER");
        if(staff.getDepartment().equals("관리부") || staff.getRole().equals("ROLE_MANAGER")){
            manager = staffRepository.findByStaffNum(37L); // 부장님은 4
        }

        if(staff.getVacationCount()==0){
            return 500;
        }
        try{
            insertVacation(applyVacationFormList, staff);
            insertNotification(staff, manager);
            sseEmitterSender.sendSseEmitter(manager);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
        return 0;
    }

    private void insertVacation(List<ApplyVacationForm> applyVacationFormList, Staff staff){
        for(ApplyVacationForm applyVacationForm : applyVacationFormList){
            Vacation newVacation=new Vacation();
            modelMapper.map(applyVacationForm, newVacation);
            newVacation.setStaff(staff);
            newVacation.setApprovalStatus('W');

            vacationRepository.save(newVacation);
        }
    }

    private void insertNotification(Staff requester, Staff approval){
        Notification notification=Notification.builder()
                .type("vacation")
                .createdAt(LocalDateTime.now())
                .requester(requester.getName())
                .readAt(0)
                .staff(approval)
                .build();
        notificationRepository.save(notification);
    }

    /*나의 휴가 신청 목록 조회*/
    public List<Vacation> getMyApplicationList(Long staffNum, LocalDate startDate, LocalDate endDate, char approvalStatus, Pageable pageable){
        Staff staff=staffRepository.findByStaffNum(staffNum);

        if(approvalStatus=='A'){
            return vacationRepository.findAllByStaffAndStartDateBetweenOrderByStartDateDesc(staff, startDate, endDate, pageable);
        }
        return vacationRepository.findAllByStaffAndApprovalStatusAndStartDateBetweenOrderByStartDateDesc(staff, approvalStatus, startDate, endDate, pageable);
    }

    /*휴가 취소 신청*/

    /*휴가 신청 내역 수정*/
    public int updateMyVacationInfo(Long vacationNum, ApplyVacationForm applyVacationForm){
        Vacation vacation=vacationRepository.findByVacationNum(vacationNum);
        if(vacation.getApprovalStatus()!='W'){
            return 400;
        }

        try {
            conductUpdateVacation(applyVacationForm, vacation);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
        return 0;
    }

    private void conductUpdateVacation(ApplyVacationForm applyVacationForm, Vacation vacation){
        modelMapper.map(applyVacationForm, vacation);
        vacationRepository.save(vacation);
    }

    /*휴가 신청 내역 삭제*/
    public int removeMyVacation(Long vacationNum){
        Vacation vacation=vacationRepository.findByVacationNum(vacationNum);
        if(vacation.getApprovalStatus()!='W'){
            return 400;
        }

        try {
            vacationRepository.deleteByVacationNum(vacationNum);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
        return 0;
    }

    /*부서장 휴가 요청관리 내역 조회*/
    public List<Vacation> requestHistory(Long staffNum, Pageable pageable){
        Staff manager=staffRepository.findByStaffNum(staffNum);
        List<Vacation> requestVacationList=vacationRepository.findAllByOrderByStartDateDesc(pageable);
        requestVacationList.removeIf(vacation -> !vacation.getStaff().getDepartment().equals(manager.getDepartment()));
        return requestVacationList;
    }

    /*부서장의 휴가 승인*/
    public int approveVacationOfManager(List<Vacation> vacationList){
        try {
            for(Vacation vacation : vacationList){
                if (vacation.getStaff().getDepartment().equals("관리부") || vacation.getStaff().getRole().equals("ROLE_MANAGER")) {
                    vacation.setApprovalStatus('F');
                }
                vacation.setApprovalStatus('T');

                vacationRepository.save(vacation);
            }
            if(vacationList.get(0).getStaff().getDepartment().equals("관리부") || vacationList.get(0).getStaff().getRole().equals("ROLE_MANAGER")){
                Staff staff= vacationList.get(0).getStaff();
                insertNotification(staff, staff);
                sseEmitterSender.sendSseEmitter(staff);
            }

            // TODO sseEmitter
            Staff admin=staffRepository.findByStaffNum(37L); // 부장님은 4
            insertNotification(vacationList.get(0).getStaff(), admin);
            sseEmitterSender.sendSseEmitter(admin);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
       return 0;
    }

    /*부서장의 휴가 반려*/
    public int rejectVacationOfManager(List<Vacation> vacationList){
        try {
            for(Vacation vacation : vacationList){
                vacation.setApprovalStatus('C');
                vacation.setCompanionReason(vacation.getCompanionReason());
                vacationRepository.save(vacation);
            }
            // TODO sseEmitter
            Staff staff= vacationList.get(0).getStaff();
            insertNotification(staff, staff);
            sseEmitterSender.sendSseEmitter(staff);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
        return 0;
    }

    /*관리자 휴가 최종 승인*/
    public int approveVacationOfAdmin(List<Vacation> vacationList){
        try {
            Staff staff=vacationList.get(0).getStaff();
            double count=0;
            for(Vacation vacation : vacationList){
                vacation.setApprovalStatus('F');
                if(vacation.getVacationType().equals("오전 반차") || vacation.getVacationType().equals("오후 반차")){
                    count+=0.5;
                }
                count+=1;
                vacationRepository.save(vacation);
            }

            staff.setVacationCount(staff.getVacationCount() - count);
            staffRepository.save(staff);

            // TODO sseEmitter
            insertNotification(staff, staff);
            sseEmitterSender.sendSseEmitter(staff);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
        return 0;
    }

    /*관리자의 휴가 반려*/
    public int rejectVacationOfAdmin(List<Vacation> vacationList){
        try {
            for(Vacation vacation : vacationList){
                vacation.setApprovalStatus('C');
                vacationRepository.save(vacation);
            }
            // TODO sseEmitter
            Staff staff= vacationList.get(0).getStaff();
            insertNotification(staff, staff);
            sseEmitterSender.sendSseEmitter(staff);
        }catch (Exception e){
            e.printStackTrace();
            return 9999;
        }
        return 0;
    }

    /*승인된 월단위 휴가목록 조회*/
    public List<Vacation> getApprovedVacationList(LocalDate startDate, LocalDate endDate,  HashMap<String, List<String>> departmentList){
        List<Vacation> approvedVacationList=vacationRepository.findAllByApprovalStatusAndStartDateBetween('F', startDate, endDate);
        if(departmentList.get("departmentList")==null){
            return approvedVacationList;
        }

        List<Vacation> approvedVacationListByDepartment=new ArrayList<>();
        for(Vacation approvedVacation : approvedVacationList){
            for (String department : departmentList.get("departmentList")) {
                if(approvedVacation.getStaff().getDepartment().equals(department)) approvedVacationListByDepartment.add(approvedVacation);
            }
        }
        return approvedVacationListByDepartment;
    }

}
