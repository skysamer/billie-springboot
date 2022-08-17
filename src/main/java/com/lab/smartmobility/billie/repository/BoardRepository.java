package com.lab.smartmobility.billie.repository;

import com.lab.smartmobility.billie.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface BoardRepository extends JpaRepository<Board, Long> {
}
