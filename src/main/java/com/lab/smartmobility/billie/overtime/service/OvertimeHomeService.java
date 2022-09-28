package com.lab.smartmobility.billie.overtime.service;

import com.lab.smartmobility.billie.overtime.domain.Overtime;
import com.lab.smartmobility.billie.overtime.dto.OvertimeDetailsForm;
import com.lab.smartmobility.billie.overtime.dto.OvertimeHourDTO;
import com.lab.smartmobility.billie.overtime.dto.OvertimeMonthlyForm;
import com.lab.smartmobility.billie.overtime.repository.OvertimeHomeRepository;
import com.lab.smartmobility.billie.overtime.repository.OvertimeRepository;
import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OvertimeHomeService {
    private final OvertimeRepository overtimeRepository;
    private final OvertimeHomeRepository homeRepository;
    private final StaffRepository staffRepository;
    private final ModelMapper modelMapper;

    /*이번달 추가근무 시간 조회*/
    public OvertimeHourDTO getMyOvertimeHour(String email){
        Staff staff = staffRepository.findByEmail(email);
        return new OvertimeHourDTO(staff.getOvertimeHour());
    }

    /*추가근무 내역 개별 상세 조회*/
    public OvertimeDetailsForm getOvertime(Long id){
        Overtime overtime = overtimeRepository.findById(id).orElseThrow();
        return modelMapper.map(overtime, OvertimeDetailsForm.class);
    }

    /*월별 추가근무 내역 캘린더*/
    public List<OvertimeMonthlyForm> getOvertimeMonthly(LocalDate startDate, LocalDate endDate){
        return homeRepository.getOvertimeMonthly(startDate, endDate);
    }
}
