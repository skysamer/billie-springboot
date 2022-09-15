package com.lab.smartmobility.billie.board.service;

import com.lab.smartmobility.billie.entity.*;
import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.board.dto.BoardDetailsForm;
import com.lab.smartmobility.billie.board.dto.BoardListForm;
import com.lab.smartmobility.billie.board.dto.BoardRegisterForm;
import com.lab.smartmobility.billie.board.repository.BoardLikeRepository;
import com.lab.smartmobility.billie.board.repository.BoardQueryRepository;
import com.lab.smartmobility.billie.board.repository.BoardRepository;
import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper;
    private final StaffRepository staffRepository;
    private final BoardQueryRepository boardQueryRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final Log log;

    /*게시글 등록*/
    public HttpBodyMessage register(BoardRegisterForm registerForm, String email){
        Staff staff = staffRepository.findByEmail(email);
        Board board = modelMapper.map(registerForm, Board.class);

        board.setStaff(staff);
        boardRepository.save(board);
        return new HttpBodyMessage("success", board.getId());
    }

    /*게시글 목록 조회*/
    public PageResult<BoardListForm> getBoardList(String keyword, String date, Pageable pageable){
        return boardQueryRepository.getBoardListPaging(keyword, date, pageable);
    }

    /*게시글 상세 조회*/
    public BoardDetailsForm getBoard(Long id, String email){
        BoardDetailsForm boardDetailsForm = boardQueryRepository.getBoard(id);
        if(boardDetailsForm == null){
            return null;
        }

        checkIsLiked(email, boardDetailsForm);
        boardQueryRepository.plusViews(id);
        return boardDetailsForm;
    }

    private void checkIsLiked(String email, BoardDetailsForm boardDetailsForm){
        boolean isLiked = boardLikeRepository.existsByEmailAndBoardId(email, boardDetailsForm.getId());
        boardDetailsForm.checkIsLiked(isLiked);
    }

    /*게시글 수정*/
    public HttpBodyMessage modify(Long id, String email, BoardRegisterForm registerForm){
        Board board = boardRepository.findById(id).orElse(null);
        log.info(board.getId());
        if(board == null){
            return new HttpBodyMessage("fail", "게시글이 존재하지 않습니다");
        }
        boolean isUser = checkUser(email, board, "update");
        if(!isUser){
            return new HttpBodyMessage("fail", "해당 유저만 수정할 수 있습니다");
        }

        modelMapper.map(registerForm, board);
        return new HttpBodyMessage("success", "게시글 수정 성공");
    }

    private boolean checkUser(String email, Board board, String type){
        Staff user = staffRepository.findByEmail(email);
        if(type.equals("delete")){
            return user.getStaffNum().equals(board.getStaff().getStaffNum()) || user.getRole().equals("ROLE_ADMIN");
        }
        return user.getStaffNum().equals(board.getStaff().getStaffNum());
    }

    /*게시글 삭제*/
    public HttpBodyMessage remove(Long id, String email){
        Board board = boardRepository.findById(id).orElse(null);
        if(board == null){
            return new HttpBodyMessage("fail", "게시글이 존재하지 않습니다");
        }
        boolean isUser = checkUser(email, board, "delete");
        if(!isUser){
            return new HttpBodyMessage("fail", "해당 유저만 삭제할 수 있습니다");
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
    public BoardDetailsForm getPrevBoard(Long id, String email){
        BoardDetailsForm boardDetailsForm = boardQueryRepository.getPrev(id);
        boardQueryRepository.plusViews(boardDetailsForm.getId());
        checkIsLiked(email, boardDetailsForm);
        return boardDetailsForm;
    }

    /*다음글 조회*/
    public BoardDetailsForm getNextBoard(Long id, String email){
        BoardDetailsForm boardDetailsForm = boardQueryRepository.getNext(id);
        boardQueryRepository.plusViews(boardDetailsForm.getId());
        checkIsLiked(email, boardDetailsForm);
        return boardDetailsForm;
    }

}
