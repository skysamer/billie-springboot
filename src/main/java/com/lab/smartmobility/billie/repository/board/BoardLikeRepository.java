package com.lab.smartmobility.billie.repository.board;

import com.lab.smartmobility.billie.entity.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    boolean existsByEmailAndBoardId(String email, Long boardId);
    void deleteByEmailAndBoardId(String email, Long boardId);
}
