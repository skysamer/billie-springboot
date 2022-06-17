package com.lab.smartmobility.billie.service;

import com.lab.smartmobility.billie.dto.staff.StaffInfoForm;
import com.lab.smartmobility.billie.entity.HttpMessage;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.repository.StaffRepository;
import com.lab.smartmobility.billie.repository.StaffRepositoryImpl;
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
    public HttpMessage registerNewStaff(StaffInfoForm staffInfoForm){
        Staff newStaff = modelMapper.map(staffInfoForm, Staff.class);
        staffRepository.save(newStaff);
        return new HttpMessage("success", "success-register");
    }

    /*직원 정보 수정*/
    public HttpMessage modifyStaffInfo(Long staffNum, StaffInfoForm staffInfoForm){
        Staff staff = staffRepository.findByStaffNum(staffNum);
        if(staff == null){
            return new HttpMessage("success", "not-exits-staff-info");
        }

        staffInfoForm.setStaffNum(staffNum);
        modelMapper.map(staffInfoForm, staff);

        staffRepository.save(staff);
        return new HttpMessage("success", "success-modify");
    }

    /*퇴사처리*/
    public HttpMessage resign(Long staffNum){
        Staff retiredStaff = staffRepository.findByStaffNum(staffNum);
        if(retiredStaff == null){
            return new HttpMessage("success", "not-exits-staff-info");
        }

        retiredStaff.resign(1);
        staffRepository.save(retiredStaff);
        return new HttpMessage("success", "success-resign");
    }

    /*정보삭제*/
    public HttpMessage remove(Long staffNum){
        Staff staff = staffRepository.findByStaffNum(staffNum);
        if(staff == null){
            return new HttpMessage("success", "not-exits-staff-info");
        }

        staffRepository.delete(staff);
        return new HttpMessage("success", "success-remove");
    }
}
