package com.lab.smartmobility.billie.mypage.service;

import com.lab.smartmobility.billie.staff.dto.StaffInfoForm;
import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import com.lab.smartmobility.billie.staff.repository.StaffRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {
    private final StaffRepository staffRepository;
    private final StaffRepositoryImpl staffRepositoryImpl;
    private final ModelMapper modelMapper;

    /*전체 직원정보 조회*/
    public List<StaffInfoForm> getStaffInfoList(){
        return staffRepositoryImpl.getStaffInfoList();
    }

    /*직원 정보 상세조회*/
    public StaffInfoForm getStaffInfo(Long staffNum){
        Staff staff = staffRepository.findByStaffNum(staffNum);
        return modelMapper.map(staff, StaffInfoForm.class);
    }

    /*신규직원 추가*/
    public HttpBodyMessage registerNewStaff(StaffInfoForm staffInfoForm){
        Staff newStaff = modelMapper.map(staffInfoForm, Staff.class);
        staffRepository.save(newStaff);
        return new HttpBodyMessage("success", "success-register");
    }

    /*직원 정보 수정*/
    public HttpBodyMessage modifyStaffInfo(Long staffNum, StaffInfoForm staffInfoForm){
        Staff staff = staffRepository.findByStaffNum(staffNum);
        if(staff == null){
            return new HttpBodyMessage("success", "not-exits-staff-info");
        }

        staffInfoForm.setStaffNum(staffNum);
        modelMapper.map(staffInfoForm, staff);

        staffRepository.save(staff);
        return new HttpBodyMessage("success", "success-modify");
    }

    /*퇴사처리*/
    public HttpBodyMessage resign(Long staffNum){
        Staff retiredStaff = staffRepository.findByStaffNum(staffNum);
        if(retiredStaff == null){
            return new HttpBodyMessage("success", "not-exits-staff-info");
        }

        retiredStaff.resign(1);
        staffRepository.save(retiredStaff);
        return new HttpBodyMessage("success", "success-resign");
    }

    /*정보삭제*/
    public HttpBodyMessage remove(Long staffNum){
        Staff staff = staffRepository.findByStaffNum(staffNum);
        if(staff == null){
            return new HttpBodyMessage("success", "not-exits-staff-info");
        }

        staffRepository.delete(staff);
        return new HttpBodyMessage("success", "success-remove");
    }
}
