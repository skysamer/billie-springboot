package com.lab.smartmobility.billie.global.service;

import com.lab.smartmobility.billie.global.dto.NameDropdownForm;
import com.lab.smartmobility.billie.staff.repository.StaffRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UtilService {
    private final StaffRepositoryImpl staffRepositoryImpl;

    public List<NameDropdownForm> getNameList(){
        return staffRepositoryImpl.getNameList();
    }
}
