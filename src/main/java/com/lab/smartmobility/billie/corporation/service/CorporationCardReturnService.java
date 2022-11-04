package com.lab.smartmobility.billie.corporation.service;

import com.lab.smartmobility.billie.corporation.domain.*;
import com.lab.smartmobility.billie.corporation.dto.*;
import com.lab.smartmobility.billie.corporation.repository.*;
import com.lab.smartmobility.billie.global.dto.TotalCount;
import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CorporationCardReturnService {
    private final CorporationCardRepository cardRepository;
    private final ApplicationRepository applicationRepository;
    private final StaffRepository staffRepository;
    private final CorporationCardReturnRepository cardReturnRepository;
    private final CorporationCardUseCaseRepository cardUseCaseRepository;
    private final ExpenseClaimRepository expenseClaimRepository;
    private final ExpenseCaseRepository expenseCaseRepository;
    private final CorporationReturnRepositoryImpl returnRepository;

    private final ModelMapper modelMapper;
    private final Log log;

    /*법인카드 반납*/
    public HttpBodyMessage returnCorporationCard(CorporationReturnForm corporationReturnForm){
        Application application=applicationRepository.findByApplicationId(corporationReturnForm.getApplicationId());
        if(application.getApprovalStatus() == 'w' || application.getApprovalStatus() == 't'){
            return new HttpBodyMessage("fail", "not-approved-application");
        }

        try{
            application.returnUpdate(corporationReturnForm.getEndDate(), corporationReturnForm.getEndTime(), 1);
            applicationRepository.save(application);

            CorporationCardReturn corporationCardReturn=modelMapper.map(corporationReturnForm, CorporationCardReturn.class);
            corporationCardReturn.setApplication(application);
            cardReturnRepository.save(corporationCardReturn);

            for(CorporationUseCaseForm corporationUseCaseForm : corporationReturnForm.getUseCaseFormList()){
                CorporationCardUseCase cardUseCase=modelMapper.map(corporationUseCaseForm, CorporationCardUseCase.class);
                cardUseCase.setCorporationCardReturn(corporationCardReturn);
                cardUseCaseRepository.save(cardUseCase);
            }

            CorporationCard returnedCard=application.getCorporationCard();
            returnedCard.returnCard(0);
            cardRepository.save(returnedCard);
        }catch (Exception e){
            log.error(e);
            return new HttpBodyMessage("fail", "fail-card-return");
        }
        return new HttpBodyMessage("success", "success-card-return");
    }

    /*개인 경비청구*/
    public HttpBodyMessage chargeForExpenses(ExpenseClaimForm expenseClaimForm){
        Application application=applicationRepository.findByApplicationId(expenseClaimForm.getApplicationId());
        if(application.getApprovalStatus()=='w' || application.getApprovalStatus()=='t'){
            return new HttpBodyMessage("fail", "not-approved-application");
        }

        try{
            application.returnUpdate(expenseClaimForm.getEndDate(), expenseClaimForm.getEndTime(), 1);
            applicationRepository.save(application);

            ExpenseClaim expenseClaim=modelMapper.map(expenseClaimForm, ExpenseClaim.class);
            expenseClaim.setApplication(application);
            expenseClaimRepository.save(expenseClaim);

            for(ExpenseCaseForm expenseCaseForm : expenseClaimForm.getExpenseCaseFormList()){
                ExpenseCase expenseCase=modelMapper.map(expenseCaseForm, ExpenseCase.class);
                expenseCase.setExpenseClaim(expenseClaim);
                expenseCaseRepository.save(expenseCase);
            }
        }catch (Exception e){
            log.error(e);
            return new HttpBodyMessage("fail", "fail-expense-claim");
        }
        return new HttpBodyMessage("success", "success-expense-claim");
    }

    /*반납 이력 상세 조회*/
    public CorporationHistoryForm getCorporationHistory(Long returnId){
        return returnRepository.getCorporationHistory(returnId);
    }

    /*경비 청구 이력 상세 조회*/
    public ExpenseClaimHistoryForm getClaimHistoryInfo(Long expenseId){
        return returnRepository.getExpenseClaimHistory(expenseId);
    }

    /*나의 법인카드 반납 이력 목록 조회*/
    public List<CorporationHistoryForm> getMyReturnHistory(Long staffNum, String cardName, String baseYear, Pageable pageable){
        return returnRepository.myReturnHistoryList(staffNum, cardName, baseYear, pageable);
    }

    /*나의 법인카드 반납 이력 조건별 개수 조회*/
    public TotalCount getMyReturnHistoryCount(Long staffNum, String cardName, String baseYear){
        return new TotalCount(returnRepository.getMyReturnHistoryCount(staffNum, cardName, baseYear));
    }

    /*나의 경비청구 이력 목록 조회*/
    public List<ExpenseClaimHistoryForm> getMyExpenseClaimHistoryList(Long staffNum, String baseYear, Pageable pageable){
        return returnRepository.getMyExpenseClaimHistoryList(staffNum, baseYear, pageable);
    }

    /*나의 경비청구 이력 조건별 개수*/
    public TotalCount getMyExpenseHistoryCount(Long staffNum, String baseYear){
        return new TotalCount(returnRepository.getMyExpenseHistoryCount(staffNum, baseYear));
    }

    /*부서장 법인카드 반납 이력 목록 조회*/
    public List<CorporationHistoryForm> getCardReturnHistoryListByManager(Long managerNum, int disposalInfo, String cardName, String baseYear, Pageable pageable){
        Staff manager = staffRepository.findByStaffNum(managerNum);
        return returnRepository.returnHistoryListByManager(manager.getDepartment(), "ROLE_USER", disposalInfo, cardName, baseYear, pageable);
    }

    /*부서장 법인카드 반납 이력 조건별 개수*/
    public TotalCount getCardReturnHistoryCountByManager(Long managerNum, int disposalInfo, String cardName, String baseYear){
        Staff manager = staffRepository.findByStaffNum(managerNum);
        return new TotalCount(returnRepository.getReturnHistoryCountByManager(manager.getDepartment(), "ROLE_USER", disposalInfo, cardName, baseYear));
    }

    /*부서장 경비청구 이력 목록 조회*/
    public List<ExpenseClaimHistoryForm> getExpenseClaimHistoryListByManager(Long managerNum, String baseYear, Pageable pageable){
        Staff manager = staffRepository.findByStaffNum(managerNum);
        return returnRepository.getExpenseClaimHistoryListByManager(manager.getDepartment(), "ROLE_USER", baseYear, pageable);
    }

    /*부서장 경비청구 이력 조건별 개수*/
    public TotalCount getExpenseClaimHistoryCountByManager(Long managerNum, String baseYear){
        Staff manager = staffRepository.findByStaffNum(managerNum);
        return new TotalCount(returnRepository.getExpenseClaimHistoryCountByManager(manager.getDepartment(), "ROLE_USER", baseYear));
    }

    /*관리자 법인카드 반납 이력 목록 조회*/
    public List<CorporationHistoryForm> getCardReturnHistoryListByAdmin(int disposalInfo, String cardName, String baseYear, Pageable pageable){
        return returnRepository.getCardReturnHistoryListByAdmin(disposalInfo, cardName, baseYear, pageable);
    }

    /*관리자 법인카드 반납 이력 조건별 개수*/
    public TotalCount getCardReturnHistoryCountByAdmin(int disposalInfo, String cardName, String baseYear){
        return new TotalCount(returnRepository.getCardReturnHistoryCountByAdmin(disposalInfo, cardName, baseYear));
    }

    /*관리자 법인카드 반납 이력 엑셀 다운로드*/
    public Workbook excelDownloadReturnHistory(int disposalInfo, String cardName, String baseYear){
        List<CorporationHistoryForm> corporationHistoryFormList=returnRepository.excelCardReturnHistoryListByAdmin(disposalInfo, cardName, baseYear);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(baseYear);
        Row row = null;
        Cell cell = null;
        int rowNum = 0;

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("사용자");
        cell = row.createCell(1);
        cell.setCellValue("사용카드 배정");
        cell = row.createCell(2);
        cell.setCellValue("카드번호");
        cell = row.createCell(3);
        cell.setCellValue("사용시작일");
        cell = row.createCell(4);
        cell.setCellValue("사용시작 시간");
        cell = row.createCell(5);
        cell.setCellValue("사용종료일");
        cell = row.createCell(6);
        cell.setCellValue("사용종료 시간");

        cell = row.createCell(7);
        cell = row.createCell(8);
        cell = row.createCell(9);
        cell = row.createCell(10);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 7, 10));
        cell = row.createCell(7);
        cell.setCellValue("법인카드 사용건");

        cell = row.createCell(11);
        cell.setCellValue("총 사용금액");
        cell = row.createCell(12);
        cell.setCellValue("비고");

        row = sheet.createRow(rowNum++);
        cell = row.createCell(7);
        cell.setCellValue("사용날짜");
        cell = row.createCell(8);
        cell.setCellValue("사용금액");
        cell = row.createCell(9);
        cell.setCellValue("사용목적");
        cell = row.createCell(10);
        cell.setCellValue("참석자명");

        for(int i=0; i<7; i++){
            sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i));
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 11, 11));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 12, 12));

        int rowCount = 2;
        for (CorporationHistoryForm corporationHistoryForm : corporationHistoryFormList) {
            row = sheet.createRow(rowNum++);

            cell = row.createCell(0);
            cell.setCellValue(corporationHistoryForm.getName());
            cell = row.createCell(1);
            cell.setCellValue(corporationHistoryForm.getCompany()+" "+corporationHistoryForm.getCardName());
            cell = row.createCell(2);
            cell.setCellValue(corporationHistoryForm.getCardNumber());
            cell = row.createCell(3);
            cell.setCellValue(String.valueOf(corporationHistoryForm.getStartDate()));
            cell = row.createCell(4);
            cell.setCellValue(String.valueOf(corporationHistoryForm.getStartTime()));
            cell = row.createCell(5);
            cell.setCellValue(String.valueOf(corporationHistoryForm.getEndDate()));
            cell = row.createCell(6);
            cell.setCellValue(String.valueOf(corporationHistoryForm.getEndTime()));
            cell = row.createCell(11);
            cell.setCellValue(corporationHistoryForm.getTotalAmountUsed());
            cell = row.createCell(12);
            cell.setCellValue(corporationHistoryForm.getNote());

            int caseSize=corporationHistoryForm.getCardUseCases().size();
            for(int i=0; i<caseSize; i++){
                cell = row.createCell(7);
                cell.setCellValue(corporationHistoryForm.getCardUseCases().get(i).getUsedAt().toString());
                cell = row.createCell(8);
                cell.setCellValue(corporationHistoryForm.getCardUseCases().get(i).getAmount());
                cell = row.createCell(9);
                cell.setCellValue(corporationHistoryForm.getCardUseCases().get(i).getPurpose());
                cell = row.createCell(10);
                cell.setCellValue(corporationHistoryForm.getCardUseCases().get(i).getParticipants());

                if(i==caseSize-1){
                    break;
                }
                row = sheet.createRow(rowNum++);
            }

            if(rowCount == rowCount+(caseSize-1)){
                rowCount = rowCount + caseSize;
                continue;
            }

            for(int i=0; i<7; i++){
                sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount+(caseSize-1), i, i));
            }
            sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount+(caseSize-1), 11, 11));
            sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount+(caseSize-1), 12, 12));
            rowCount = rowCount + caseSize;
        }
        return workbook;
    }

    /*관리자 경비청구 이력 목록 조회*/
    public List<ExpenseClaimHistoryForm> getExpenseClaimHistoryListByAdmin(String baseYear, Pageable pageable){
        return returnRepository.getExpenseClaimHistoryListByAdmin(baseYear, pageable);
    }

    /*관리자 경비청구 이력 조건별 개수*/
    public TotalCount getExpenseClaimHistoryCountByAdmin(String baseYear){
        return new TotalCount(returnRepository.getExpenseClaimHistoryCountByAdmin(baseYear));
    }

    /*경비청구 이력 엑셀 다운로드*/
    public Workbook excelDownloadExpenseClaimHistory(String baseYear){
        List<ExpenseClaimHistoryForm> expenseClaimHistoryFormList=returnRepository.excelExpenseClaimHistoryList(baseYear);

        Workbook workbook = new XSSFWorkbook();
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);

        Sheet sheet = workbook.createSheet(baseYear);
        Row row = null;
        Cell cell = null;
        int rowNum = 0;

        // Header
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("사용자");
        cell = row.createCell(1);
        cell.setCellValue("사용카드 배정");
        cell = row.createCell(2);
        cell.setCellValue("입금 은행");
        cell = row.createCell(3);
        cell.setCellValue("입금 계좌번호");
        cell = row.createCell(4);
        cell.setCellValue("사용시작일");
        cell = row.createCell(5);
        cell.setCellValue("사용시작 시간");
        cell = row.createCell(6);
        cell.setCellValue("사용종료일");
        cell = row.createCell(7);
        cell.setCellValue("사용종료 시간");

        cell = row.createCell(8);
        cell = row.createCell(9);
        cell = row.createCell(10);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 8, 10));
        cell = row.createCell(8);
        cell.setCellValue("개인경비 청구건");

        cell = row.createCell(11);
        cell.setCellValue("총 사용금액");
        cell = row.createCell(12);
        cell.setCellValue("비고");

        row = sheet.createRow(rowNum++);
        cell = row.createCell(8);
        cell.setCellValue("사용날짜");
        cell = row.createCell(9);
        cell.setCellValue("사용금액");
        cell = row.createCell(10);
        cell.setCellValue("사용목적");

        for(int i=0; i<8; i++){
            sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i));
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 11, 11));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 12, 12));

        // Body
        int rowCount = 2;
        for (ExpenseClaimHistoryForm expenseClaimHistoryForm : expenseClaimHistoryFormList) {
            row = sheet.createRow(rowNum++);

            cell = row.createCell(0);
            cell.setCellValue(expenseClaimHistoryForm.getName());
            cell = row.createCell(1);
            cell.setCellValue("개인경비 청구");
            cell = row.createCell(2);
            cell.setCellValue(expenseClaimHistoryForm.getDepositBank());
            cell = row.createCell(3);
            cell.setCellValue(expenseClaimHistoryForm.getDepositAccountNumber());
            cell = row.createCell(4);
            cell.setCellValue(String.valueOf(expenseClaimHistoryForm.getStartDate()));
            cell = row.createCell(5);
            cell.setCellValue(String.valueOf(expenseClaimHistoryForm.getStartTime()));
            cell = row.createCell(6);
            cell.setCellValue(String.valueOf(expenseClaimHistoryForm.getEndDate()));
            cell = row.createCell(7);
            cell.setCellValue(String.valueOf(expenseClaimHistoryForm.getEndTime()));
            cell = row.createCell(11);
            cell.setCellValue(expenseClaimHistoryForm.getTotalAmountUsed());
            cell = row.createCell(12);
            cell.setCellValue(expenseClaimHistoryForm.getNote());

            int caseSize=expenseClaimHistoryForm.getExpenseCaseList().size();

            for(int i=0; i<caseSize; i++){
                cell = row.createCell(8);
                cell.setCellValue(expenseClaimHistoryForm.getExpenseCaseList().get(i).getUsedAt().toString());
                cell = row.createCell(9);
                cell.setCellValue(expenseClaimHistoryForm.getExpenseCaseList().get(i).getAmount());
                cell = row.createCell(10);
                cell.setCellValue(expenseClaimHistoryForm.getExpenseCaseList().get(i).getPurpose());

                if(i==caseSize-1){
                    break;
                }
                row = sheet.createRow(rowNum++);
            }

            if(rowCount == rowCount+(caseSize-1)){
                rowCount = rowCount + caseSize;
                continue;
            }

            for(int i=0; i<8; i++){
                sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount+(caseSize-1), i, i));
            }
            sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount+(caseSize-1), 11, 11));
            sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount+(caseSize-1), 12, 12));
            rowCount=rowCount+caseSize;
        }
        return workbook;
    }
}
