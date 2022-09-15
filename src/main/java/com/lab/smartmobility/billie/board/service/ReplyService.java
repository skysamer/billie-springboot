package com.lab.smartmobility.billie.board.service;

import com.lab.smartmobility.billie.board.dto.NestedReplyRegisterForm;
import com.lab.smartmobility.billie.board.dto.ReplyModifyForm;
import com.lab.smartmobility.billie.board.dto.ReplyRegisterForm;
import com.lab.smartmobility.billie.entity.Board;
import com.lab.smartmobility.billie.entity.HttpBodyMessage;
import com.lab.smartmobility.billie.entity.Reply;
import com.lab.smartmobility.billie.staff.domain.Staff;
import com.lab.smartmobility.billie.repository.ReplyRepository;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import com.lab.smartmobility.billie.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final StaffRepository staffRepository;
    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper;

    /*댓글 등록*/
    public HttpBodyMessage register(ReplyRegisterForm registerForm, String email){
        Staff staff = staffRepository.findByEmail(email);
        Board board = boardRepository.findById(registerForm.getBoardId()).orElse(null);
        if(board == null){
            return new HttpBodyMessage("fail", "해당 게시글이 존재하지 않습니다");
        }

        Reply reply = modelMapper.map(registerForm, Reply.class);

        board.plusReplyCnt();
        reply.insert(staff, board);
        replyRepository.save(reply);
        return new HttpBodyMessage("success", "댓글 등록 성공");
    }

    /*대댓글 등록*/
    public HttpBodyMessage registerNested(NestedReplyRegisterForm registerForm, String email){
        Staff staff = staffRepository.findByEmail(email);
        Board board = boardRepository.findById(registerForm.getBoardId()).orElse(null);
        if(board == null){
            return new HttpBodyMessage("fail", "해당 게시글이 존재하지 않습니다");
        }

        Reply reply = modelMapper.map(registerForm, Reply.class);
        Reply parent = replyRepository.findById(registerForm.getParentId()).orElse(null);

        board.plusReplyCnt();
        reply.insertNested(staff, board, parent);
        replyRepository.save(reply);
        return new HttpBodyMessage("success", "대댓글 등록 성공");
    }

    /*댓글 수정*/
    public HttpBodyMessage modify(Long id, ReplyModifyForm registerForm){
        Reply reply = replyRepository.findById(id).orElse(null);
        if(reply == null){
            return new HttpBodyMessage("fail", "댓글이 존재하지 않습니다");
        }

        modelMapper.map(registerForm, reply);
        return new HttpBodyMessage("success", "댓글 수정 성공");
    }

    /*댓글 삭제*/
    public HttpBodyMessage remove(Long id){
        Reply reply = replyRepository.findById(id).orElse(null);
        if(reply == null){
            return new HttpBodyMessage("fail", "댓글이 존재하지 않습니다");
        }

        replyRepository.delete(reply);
        return new HttpBodyMessage("success", "댓글 삭제 성공");
    }
}