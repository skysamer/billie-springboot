package com.lab.smartmobility.billie.repository;

import com.lab.smartmobility.billie.entity.Equipment;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EquipmentRepository {
    List<Equipment> findAll(String department);
}
