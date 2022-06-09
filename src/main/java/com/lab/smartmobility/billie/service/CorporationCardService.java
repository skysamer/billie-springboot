package com.lab.smartmobility.billie.service;

import com.lab.smartmobility.billie.dto.TotalCount;
import com.lab.smartmobility.billie.dto.corporation.*;
import com.lab.smartmobility.billie.entity.*;
import com.lab.smartmobility.billie.entity.corporation.*;
import com.lab.smartmobility.billie.repository.NotificationRepository;
import com.lab.smartmobility.billie.repository.StaffRepository;
import com.lab.smartmobility.billie.repository.corporation.*;
import com.lab.smartmobility.billie.util.SseEmitterSender;
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
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CorporationCardService {
    private final CorporationCardRepository cardRepository;
    private final CorporationCardRepositoryImpl cardRepositoryImpl;
    private final ApplicationRepository applicationRepository;
    private final StaffRepository staffRepository;
    private final NotificationRepository notificationRepository;
    private final ApplicationRepositoryImpl applicationRepositoryImpl;
    private final CorporationCardReturnRepository cardReturnRepository;
    private final CorporationCardUseCaseRepository cardUseCaseRepository;
    private final ExpenseClaimRepository expenseClaimRepository;
    private final ExpenseCaseRepository expenseCaseRepository;
    private final CorporationReturnRepositoryImpl returnRepository;
    private final SseEmitterSender sseEmitterSender;
    private final ModelMapper modelMapper;
    private final Log log;

    /*신규 법인카드 등록*/
    public int createCard(CorporationCardForm corporationCardForm){
        try{
            CorporationCard newCorporationCard=modelMapper.map(corporationCardForm, CorporationCard.class);
            cardRepository.save(newCorporationCard);
        }catch (Exception e){
            log.error("fail : "+e);
            return 9999;
        }
        return 0;
    }

    /*보유 법인카드 목록 조회*/
    public List<CorporationCard> getCardList(int disposalInfo){
        return cardRepositoryImpl.findAll(disposalInfo);
    }

    /*개별 법인카드 정보 상세 조회*/
    public CorporationCard getCardInfo(Long cardId){
        return cardRepository.findByCardId(cardId);
    }

    /*개별 법인카드 정보 수정*/
    public int modifyCardInfo(Long cardId, CorporationCardForm corporationCardForm){
        try{
            CorporationCard card=cardRepository.findByCardId(cardId);
            modelMapper.map(corporationCardForm, card);
            cardRepository.save(card);
        }catch (Exception e){
            log.error("fail : "+e);
            return 9999;
        }
        return 0;
    }

    /*법인카드 폐기*/
    public int abrogate(Long cardId, DisposalForm disposalForm){
        try{
            CorporationCard card=cardRepository.findByCardId(cardId);
            card.setRentalStatus(99);
            card.setReasonForDisposal(disposalForm.getReasonForDisposal());
            cardRepository.save(card);
        }catch (Exception e){
            log.error("fail : "+e);
            return 9999;
        }
        return 0;
    }

    public HttpMessage remove(Long cardId){
        CorporationCard corporationCard=cardRepository.findByCardId(cardId);
        if(corporationCard==null){
            return new HttpMessage("fail", "not-exist-card-info");
        }

        try{
            cardRepository.delete(corporationCard);
        }catch (Exception e){
            log.error(e);
            return new HttpMessage("fail", "fail-remove-card");
        }
        return new HttpMessage("success", "success-remove-card");
    }

    /*법인카드 사용 신청*/
    public HttpMessage applyCardReservation(ApplyCorporationCardForm applyCorporationCardForm) {
        if(applyCorporationCardForm.getStartDate().isBefore(LocalDate.now())){
            return new HttpMessage("fail", "cannot-reservation-earlier-day");
        }

        Application application=modelMapper.map(applyCorporationCardForm, Application.class);
        Staff requester=staffRepository.findByStaffNum(applyCorporationCardForm.getStaffNum());
        application.setStaff(requester);

        Staff approval=staffRepository.findByDepartmentAndRole(requester.getDepartment(), "ROLE_MANAGER");
        if(requester.getDepartment().equals("관리부") || requester.getRole().equals("ROLE_MANAGER")){
            approval=staffRepository.findByStaffNum(37L); // 부장님은 4
        }

        Notification notification=Notification.builder()
                .requester(requester.getName())
                .receiver(approval.getName())
                .type("corporation")
                .approveStatus(application.getApprovalStatus())
                .build();
        try{
            sseEmitterSender.sendSseEmitter(approval);
            notificationRepository.save(notification);
            applicationRepository.save(application);
        }catch (Exception e){
            log.error("fail : "+e);
            return new HttpMessage("fail", "fail-application");
        }
        return new HttpMessage("success", "success-application");
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

    /*부서장의 요청 관리 목록 조회*/
    public List<Application> getListOfApprovalsRequestByManager(Long managerNum, String cardName, String baseYear, int disposalInfo, Pageable pageable){
        Staff manager=staffRepository.findByStaffNum(managerNum);
        return applicationRepositoryImpl.getApplicationListByManager(manager.getDepartment(), "ROLE_USER",
                cardName, baseYear, disposalInfo, pageable);
    }

    /*부서장의 요청 관리 목록 개수 조회*/
    public TotalCount getCountOfApprovalsRequestByManager(Long managerNum, String cardName, String baseYear, int disposalInfo){
        Staff manager=staffRepository.findByStaffNum(managerNum);
        return new TotalCount(
                applicationRepositoryImpl.getApplicationCountByManager(manager.getDepartment(), "ROLE_USER", cardName, baseYear, disposalInfo));
    }

    /*부서장의 카드 사용 승인*/
    public HttpMessage approveCardUseByManager(List<ApprovalCardUseForm> approvalCardUseForms){
        try{
            for(ApprovalCardUseForm approvalCardUseForm : approvalCardUseForms){
                Application application=applicationRepository.findByApplicationId(approvalCardUseForm.getApplicationId());
                application.setApprovalStatus('t');
                applicationRepository.save(application);
            }
        }catch (Exception e){
            log.error(e);
            return new HttpMessage("fail", "fail-approve");
        }
        return new HttpMessage("success", "success-approve");
    }

    /*카드 사용 반려*/
    public HttpMessage rejectCardUse(List<CompanionCardUseForm> companionCardUseForms){
        try{
            for(CompanionCardUseForm companionCardUseForm : companionCardUseForms){
                Application application=applicationRepository.findByApplicationId(companionCardUseForm.getApplicationId());
                application.setApprovalStatus('c');
                application.setReasonForRejection(companionCardUseForm.getReason());
            }
        }catch (Exception e){
            log.error(e);
            return new HttpMessage("fail", "fail-reject");
        }
        return new HttpMessage("success", "success-reject");
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
    public HttpMessage approveCardUseByAdmin(List<ApprovalCardUseForm> approvalCardUseForms){
        try{
            for(ApprovalCardUseForm approvalCardUseForm : approvalCardUseForms){
                Application application=applicationRepository.findByApplicationId(approvalCardUseForm.getApplicationId());
                application.setApprovalStatus('f');

                if(approvalCardUseForm.getCardName().equals("개인경비 청구")){
                    application.setIsClaimedExpense(1);
                    applicationRepository.save(application);
                    return new HttpMessage("success", "success-final-approve");
                }
                CorporationCard corporationCard=cardRepository.findByCardNameAndCompany(approvalCardUseForm.getCardName(), approvalCardUseForm.getCompany());
                application.setCorporationCard(corporationCard);
                applicationRepository.save(application);
            }
        }catch (Exception e){
            log.error(e);
            return new HttpMessage("fail", "fail-final-approve");
        }
        return new HttpMessage("success", "success-final-approve");
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
        Staff my=staffRepository.findByStaffNum(staffNum);
        return applicationRepository.findAllByStaffAndApprovalStatus(my, 'f');
    }

    /*법인카드 반납*/
    public HttpMessage returnCorporationCard(CorporationReturnForm corporationReturnForm){
        Application application=applicationRepository.findByApplicationId(corporationReturnForm.getApplicationId());
        if(application.getApprovalStatus()=='w' || application.getApprovalStatus()=='t'){
            return new HttpMessage("fail", "not-approved-application");
        }

        try{
            application.setEndDate(corporationReturnForm.getEndDate());
            application.setEndTime(corporationReturnForm.getEndTime());
            application.setIsReturned(1);
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
            returnedCard.setRentalStatus(0);
            cardRepository.save(returnedCard);
        }catch (Exception e){
            log.error(e);
            return new HttpMessage("fail", "fail-card-return");
        }
        return new HttpMessage("success", "success-card-return");
    }

    /*개인 경비청구*/
    public HttpMessage chargeForExpenses(ExpenseClaimForm expenseClaimForm){
        Application application=applicationRepository.findByApplicationId(expenseClaimForm.getApplicationId());
        if(application.getApprovalStatus()=='w' || application.getApprovalStatus()=='t'){
            return new HttpMessage("fail", "not-approved-application");
        }

        try{
            application.setEndDate(expenseClaimForm.getEndDate());
            application.setEndTime(expenseClaimForm.getEndTime());
            application.setIsReturned(1);
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
            return new HttpMessage("fail", "fail-expense-claim");
        }
        return new HttpMessage("success", "success-expense-claim");
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
        Staff manager=staffRepository.findByStaffNum(managerNum);
        return returnRepository.returnHistoryListByManager(manager.getDepartment(), "ROLE_USER", disposalInfo, cardName, baseYear, pageable);
    }

    /*부서장 법인카드 반납 이력 조건별 개수*/
    public TotalCount getCardReturnHistoryCountByManager(Long managerNum, int disposalInfo, String cardName, String baseYear){
        Staff manager=staffRepository.findByStaffNum(managerNum);
        return new TotalCount(returnRepository.getReturnHistoryCountByManager(manager.getDepartment(), "ROLE_USER", disposalInfo, cardName, baseYear));
    }

    /*부서장 경비청구 이력 목록 조회*/
    public List<ExpenseClaimHistoryForm> getExpenseClaimHistoryListByManager(Long managerNum, String baseYear, Pageable pageable){
        Staff manager=staffRepository.findByStaffNum(managerNum);
        return returnRepository.getExpenseClaimHistoryListByManager(manager.getDepartment(), "ROLE_USER", baseYear, pageable);
    }

    /*부서장 경비청구 이력 조건별 개수*/
    public TotalCount getExpenseClaimHistoryCountByManager(Long managerNum, String baseYear){
        Staff manager=staffRepository.findByStaffNum(managerNum);
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

        // Header
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

        // Body
        int rowCount=2;
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

            for(int i=0; i<7; i++){
                sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount+(caseSize-1), i, i));
            }
            sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount+(caseSize-1), 11, 11));
            sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount+(caseSize-1), 12, 12));
            rowCount=rowCount+caseSize;
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
        int rowCount=2;
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
