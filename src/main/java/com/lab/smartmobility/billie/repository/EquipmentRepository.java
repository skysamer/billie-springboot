package com.lab.smartmobility.billie.repository;

import com.lab.smartmobility.billie.entity.Equipment;

import java.util.List;

public interface EquipmentRepository {
    List<Equipment> findAll(String department);
}
