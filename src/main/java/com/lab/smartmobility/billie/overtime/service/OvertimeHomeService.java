package com.lab.smartmobility.billie.overtime.service;

import com.lab.smartmobility.billie.overtime.dto.OvertimeHourDTO;
import com.lab.smartmobility.billie.overtime.repository.OvertimeRepository;
import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OvertimeHomeService {
    private final OvertimeRepository overtimeRepository;
    private final StaffRepository staffRepository;

    /*이번달 추가근무 시간 조회*/
    public OvertimeHourDTO getMyOvertimeHour(String email){
        Staff staff = staffRepository.findByEmail(email);
        return new OvertimeHourDTO(staff.getOvertimeHour());
    }
}
