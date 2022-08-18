package com.lab.smartmobility.billie.repository;

import com.lab.smartmobility.billie.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
