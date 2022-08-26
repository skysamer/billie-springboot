package com.lab.smartmobility.billie.service;

import com.lab.smartmobility.billie.dto.PageResult;
import com.lab.smartmobility.billie.dto.board.BoardDetailsForm;
import com.lab.smartmobility.billie.dto.board.BoardListForm;
import com.lab.smartmobility.billie.dto.board.BoardRegisterForm;
import com.lab.smartmobility.billie.entity.*;
import com.lab.smartmobility.billie.repository.ViewRedisRepository;
import com.lab.smartmobility.billie.repository.board.BoardLikeRepository;
import com.lab.smartmobility.billie.repository.board.BoardQueryRepository;
import com.lab.smartmobility.billie.repository.board.BoardRepository;
import com.lab.smartmobility.billie.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper;
    private final StaffRepository staffRepository;
    private final BoardQueryRepository boardQueryRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final ViewRedisRepository viewRedisRepository;

    /*게시글 등록*/
    public HttpBodyMessage register(BoardRegisterForm registerForm, String email){
        Staff staff = staffRepository.findByEmail(email);
        Board board = modelMapper.map(registerForm, Board.class);

        board.setStaff(staff);
        boardRepository.save(board);
        return new HttpBodyMessage("success", "게시글 등록 성공");
    }

    /*게시글 목록 조회*/
    public PageResult<BoardListForm> getBoardList(String keyword, String date, Pageable pageable){
        return boardQueryRepository.getBoardListPaging(keyword, date, pageable);
    }

    /*게시글 상세 조회*/
    public BoardDetailsForm getBoard(Long id){
        BoardDetailsForm boardDetailsForm = boardQueryRepository.getBoard(id);
        if(boardDetailsForm == null){
            return null;
        }

        boardQueryRepository.plusViews(id);
        return boardDetailsForm;
    }

    /*게시글 수정*/
    public HttpBodyMessage modify(Long id, BoardRegisterForm registerForm){
        Board board = boardRepository.findById(id).orElse(null);
        if(board == null){
            return new HttpBodyMessage("fail", "게시글이 존재하지 않습니다");
        }

        modelMapper.map(registerForm, board);
        return new HttpBodyMessage("success", "게시글 수정 성공");
    }

    /*게시글 삭제*/
    public HttpBodyMessage remove(Long id){
        Board board = boardRepository.findById(id).orElse(null);
        if(board == null){
            return new HttpBodyMessage("fail", "게시글이 존재하지 않습니다");
        }

        boardRepository.delete(board);
        return new HttpBodyMessage("success", "게시글 삭제 성공");
    }

    /*좋아요*/
    public HttpBodyMessage like(String email, Long id){
        Board board = boardRepository.findById(id).orElse(null);
        if(board == null){
            return new HttpBodyMessage("fail", "게시글이 존재하지 않습니다");
        }

        if(boardLikeRepository.existsByEmailAndBoardId(email, id)){
            minusLike(email, id, board);
        }else{
            plusLike(email, id, board);
        }
        return new HttpBodyMessage("success", "좋아요 계산 완료");
    }

    private void plusLike(String email, Long id, Board board){
        board.plusLikes();
        BoardLike boardLike = new BoardLike(email, id);
        boardLikeRepository.save(boardLike);
    }

    private void minusLike(String email, Long id, Board board){
        board.minusLikes();
        boardLikeRepository.deleteByEmailAndBoardId(email, id);
    }

    /*이전글 조회*/
    public BoardDetailsForm getPrevBoard(Long id){
        BoardDetailsForm boardDetailsForm = boardQueryRepository.getPrev(id);
        boardQueryRepository.plusViews(boardDetailsForm.getId());
        return boardDetailsForm;
    }

    /*다음글 조회*/
    public BoardDetailsForm getNextBoard(Long id){
        BoardDetailsForm boardDetailsForm = boardQueryRepository.getNext(id);
        boardQueryRepository.plusViews(boardDetailsForm.getId());
        return boardDetailsForm;
    }

    public HttpBodyMessage test(Long id){
        Board board = boardRepository.findById(id).orElse(null);
        if(board == null){
            return new HttpBodyMessage("fail", "게시글 존재하지 않음");
        }
        View view = View.builder()
                .id(board.getClass().getSimpleName() + board.getId())
                .views(board.getViews() + 1)
                .refreshTime(LocalDateTime.now())
                .build();

        viewRedisRepository.save(view);
        return new HttpBodyMessage("success", "조회수 증가");
    }

}
