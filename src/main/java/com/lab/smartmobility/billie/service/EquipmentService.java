package com.lab.smartmobility.billie.service;

import com.lab.smartmobility.billie.entity.Equipment;
import com.lab.smartmobility.billie.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;

    public List<Equipment> getEquipmentList(String department){
        return equipmentRepository.findAll(department);
    }
}
