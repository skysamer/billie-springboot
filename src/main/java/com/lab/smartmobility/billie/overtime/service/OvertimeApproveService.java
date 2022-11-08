package com.lab.smartmobility.billie.overtime.service;

import com.lab.smartmobility.billie.overtime.dto.OvertimeExcelForm;
import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.global.util.AssigneeToApprover;
import com.lab.smartmobility.billie.global.util.NotificationSender;
import com.lab.smartmobility.billie.overtime.domain.Overtime;
import com.lab.smartmobility.billie.overtime.dto.OvertimeFinalApproveForm;
import com.lab.smartmobility.billie.overtime.dto.OvertimeApproveListForm;
import com.lab.smartmobility.billie.overtime.dto.OvertimeCompanionForm;
import com.lab.smartmobility.billie.overtime.repository.OvertimeApproveRepository;
import com.lab.smartmobility.billie.overtime.repository.OvertimeRepository;
import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OvertimeApproveService {
    private final OvertimeRepository overtimeRepository;
    private final StaffRepository staffRepository;
    private final OvertimeApproveRepository approveRepository;
    private final AssigneeToApprover assigneeToApprover;
    private final NotificationSender notificationSender;
    private static final String DOMAIN_TYPE = "overtime";

    /*부서장의 요청관리 목록 조회*/
    public PageResult<OvertimeApproveListForm> getApproveListByManager(String baseDate, String email, String name, Pageable pageable){
        Staff staff = staffRepository.findByEmail(email);
        return approveRepository.getApproveListPagingByManager(baseDate, staff.getDepartment(), name, pageable);
    }

    /*사전승인*/
    public HttpBodyMessage approveByManager(List<Long> ids, String email){
        for(Long id : ids){
            Overtime overtime = overtimeRepository.findById(id).orElseThrow();
            overtime.preApprove();
        }
        sendNotification(email);
        return new HttpBodyMessage("success", "추가근무 사전승인");
    }

    private void sendNotification(String email){
        Staff applicant = staffRepository.findByEmail(email);
        Staff approval = assigneeToApprover.assignApproval(applicant);
        notificationSender.sendNotification(DOMAIN_TYPE, approval, 1);
    }

    /*추가근무 반려*/
    public HttpBodyMessage reject(List<OvertimeCompanionForm> companionFormList){
        for(OvertimeCompanionForm companionForm : companionFormList){
            Overtime overtime = overtimeRepository.findById(companionForm.getId()).orElseThrow();
            overtime.reject(companionForm.getReason());
            notificationSender.sendNotification(DOMAIN_TYPE, overtime.getStaff(), 0);
        }
        return new HttpBodyMessage("success", "추가근무 반려성공");
    }

    /*관리자의 추가근무 승인 요청목록 조회*/
    public PageResult<OvertimeApproveListForm> getApproveListByAdmin(String name, String department, String baseDate, Pageable pageable){
        return approveRepository.getApproveListPagingByAdmin(name, department, baseDate, pageable);
    }

    /*최종승인*/
    public HttpBodyMessage approveByAdmin(OvertimeFinalApproveForm finalApproveForm){
        Overtime overtime = overtimeRepository.findById(finalApproveForm.getId()).orElseThrow();
        overtime.finalApprove(finalApproveForm.getAdmitTime());
        return new HttpBodyMessage("success", "최종승인");
    }

    /*엑셀 다운로드*/
    public Workbook downloadExcel(String baseDate, String department){
        List<OvertimeExcelForm> excelFormList = approveRepository.extractExcelData(baseDate, department);

        Workbook workbook = new XSSFWorkbook();
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
        cell.setCellValue("시작 - 종료시간");
        cell = row.createCell(5);
        cell.setCellValue("식사여부");
        cell = row.createCell(6);
        cell.setCellValue("제출시간");
        cell = row.createCell(7);
        cell.setCellValue("인정시간");
        cell = row.createCell(8);
        cell.setCellValue("결재상태");

        for(OvertimeExcelForm excelForm : excelFormList){
            row = sheet.createRow(rowNum++);

            cell = row.createCell(0);
            cell.setCellValue(excelForm.getName());
            cell = row.createCell(1);
            cell.setCellValue(excelForm.getEmployeeNumber());
            cell = row.createCell(2);
            cell.setCellValue(excelForm.getDayOfOvertime().toString());
            cell = row.createCell(3);
            cell.setCellValue(excelForm.getContent());
            cell = row.createCell(4);
            cell.setCellValue(excelForm.getStartTime().toString() + "-" + excelForm.getEndTime().toString());
            cell = row.createCell(5);
            cell.setCellValue(excelForm.getId() == 1);
            cell = row.createCell(6);
            cell.setCellValue(excelForm.getSubTime());
            cell = row.createCell(7);
            cell.setCellValue(excelForm.getAdmitTime() == null ? "-" : excelForm.getAdmitTime().toString());
            cell = row.createCell(8);
            cell.setCellValue(excelForm.getApprovalStatus().getValue());
        }
        return workbook;
    }
}
