package com.lab.smartmobility.billie.service;

import com.lab.smartmobility.billie.dto.board.BoardRegisterForm;
import com.lab.smartmobility.billie.entity.Board;
import com.lab.smartmobility.billie.entity.HttpBodyMessage;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.repository.BoardRepository;
import com.lab.smartmobility.billie.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper;
    private final StaffRepository staffRepository;

    /*게시글 등록*/
    public HttpBodyMessage register(BoardRegisterForm registerForm, String email){
        Staff staff = staffRepository.findByEmail(email);
        Board board = modelMapper.map(registerForm, Board.class);

        board.setStaff(staff);
        boardRepository.save(board);
        return new HttpBodyMessage("success", "게시글 등록 성공");
    }
}
