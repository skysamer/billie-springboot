package com.lab.smartmobility.billie.vacation.service;

import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.global.util.AssigneeToApprover;
import com.lab.smartmobility.billie.global.util.NotificationSender;
import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import com.lab.smartmobility.billie.vacation.domain.ApprovalStatus;
import com.lab.smartmobility.billie.vacation.domain.Vacation;
import com.lab.smartmobility.billie.vacation.dto.VacationApproveListForm;
import com.lab.smartmobility.billie.vacation.dto.VacationCompanionForm;
import com.lab.smartmobility.billie.vacation.dto.VacationExcelForm;
import com.lab.smartmobility.billie.vacation.repository.VacationApproveRepository;
import com.lab.smartmobility.billie.vacation.repository.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Period;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VacationApproveService {
    private final VacationRepository vacationRepository;
    private final VacationApproveRepository approveRepository;
    private final StaffRepository staffRepository;
    private final AssigneeToApprover assigneeToApprover;
    private final NotificationSender notificationSender;
    private final Workbook workbook;
    private final Log log;

    private static final String DOMAIN_TYPE = "vacation";

    /*부서장의 승인 요청 목록 조회*/
    public PageResult<VacationApproveListForm> getApproveListByManager(String baseDate, String email, String keyword, Pageable pageable){
        Staff manager = staffRepository.findByEmail(email);
        return approveRepository.getApproveListByManagerResult(baseDate, manager.getDepartment(), keyword, pageable);
    }

    /*부서장의 휴가 승인*/
    public HttpBodyMessage approveByManager(List<Long> vacationIdList, String email){
        approveRepository.approveByManager(vacationIdList);
        sendNotification(email);
        return new HttpBodyMessage("success", "휴가승인성공");
    }

    private void sendNotification(String email){
        Staff applicant = staffRepository.findByEmail(email);
        Staff approval = assigneeToApprover.assignApproval(applicant);
        notificationSender.sendNotification(DOMAIN_TYPE, approval, 1);
    }

    /*휴가 반려*/
    public HttpBodyMessage reject(List<VacationCompanionForm> companionFormList){
        for(VacationCompanionForm companionForm : companionFormList){
            Vacation vacation = vacationRepository.findByVacationId(companionForm.getVacationId());
            vacation.reject(companionForm.getCompanionReason());
            notificationSender.sendNotification(DOMAIN_TYPE, vacation.getStaff(), 0);
        }
        return new HttpBodyMessage("success", "반려성공");
    }

    /*관리자 휴가 승인 요청 목록 조회*/
    public PageResult<VacationApproveListForm> getApproveListByAdmin(String baseDate, String department, String keyword, Pageable pageable){
        return approveRepository.getApproveListByAdminResult(baseDate, department, keyword, pageable);
    }

    /*관리자 휴가 최종 승인*/
    public HttpBodyMessage approveByAdmin(List<Long> vacationIdList){
        for(Long id : vacationIdList){
            Vacation vacation = vacationRepository.findByVacationId(id);
            vacation.approve(ApprovalStatus.FINAL);
            Staff applicant = vacation.getStaff();

            Period period = Period.between(vacation.getStartDate(), vacation.getEndDate());
            calculateVacationCount(applicant, vacation.getVacationType(), period.getDays());
            notificationSender.sendNotification(DOMAIN_TYPE, vacation.getStaff(), 0);
        }
        return new HttpBodyMessage("success", "휴가승인성공");
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

    /*요청 관리내역 엑셀 다운로드*/
    public Workbook downloadExcel(String baseDate, String department){
        List<VacationExcelForm> excelFormList = approveRepository.excelDownloadList(baseDate, department);

        Sheet sheet = workbook.createSheet(baseDate);
        Row row;
        Cell cell;
        int rowNum = 0;

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("이름");
        cell = row.createCell(1);
        cell.setCellValue("사번");
        cell = row.createCell(2);
        cell.setCellValue("날짜");
        cell = row.createCell(3);
        cell.setCellValue("내용");
        cell = row.createCell(4);
        cell.setCellValue("종류");
        cell = row.createCell(5);
        cell.setCellValue("상태");

        for (VacationExcelForm excelForm : excelFormList) {
            String status = convertToKorean(excelForm.getApprovalStatus());
            row = sheet.createRow(rowNum++);

            cell = row.createCell(0);
            cell.setCellValue(excelForm.getName());
            cell = row.createCell(1);
            cell.setCellValue(excelForm.getEmployeeNumber());
            cell = row.createCell(2);
            cell.setCellValue(excelForm.getStartDate() + " - " + excelForm.getEndDate());
            cell = row.createCell(3);
            cell.setCellValue(excelForm.getReason());
            cell = row.createCell(4);
            cell.setCellValue(excelForm.getVacationType());
            cell = row.createCell(5);
            cell.setCellValue(status);
        }
        return workbook;
    }

    private String convertToKorean(String status){
        switch (status){
            case "CANCEL":
                return "취소";
            case "WAITING":
                return "대기중";
            case "TEAM":
                return "부장승인";
            case "FINAL":
                return "최종승인";
        }
        return "반려";
    }

}
