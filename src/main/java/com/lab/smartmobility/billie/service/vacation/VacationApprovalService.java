package com.lab.smartmobility.billie.service.vacation;

import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.vacation.domain.Vacation;
import com.lab.smartmobility.billie.repository.vacation.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class VacationApprovalService {
    private final VacationRepository vacationRepository;

    /*부서장의 승인 요청 목록 조회*/
    public PageResult<Vacation> getRequestListByManager(){
        return null;
    }
}
