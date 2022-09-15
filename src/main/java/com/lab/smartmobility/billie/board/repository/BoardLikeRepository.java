package com.lab.smartmobility.billie.board.repository;

import com.lab.smartmobility.billie.board.domain.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    boolean existsByEmailAndBoardId(String email, Long boardId);
    void deleteByEmailAndBoardId(String email, Long boardId);
}
