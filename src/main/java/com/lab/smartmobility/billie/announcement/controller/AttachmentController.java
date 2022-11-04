package com.lab.smartmobility.billie.announcement.controller;

import com.lab.smartmobility.billie.announcement.service.AttachmentService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/announcement")
@Api(tags = {"공지 첨부파일 api"})
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService service;

    @ApiOperation(value = "첨부파일 변환")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "글번호"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 400, message = "해당 게시글이 존재하지 않음")
    })
    @GetMapping("/user/attachment-list/{id}")
    public ResponseEntity<List<byte[]>> getAnnouncementList(@PathVariable Long id) throws IOException {
        List<byte[]> fileList = service.getAttachmentList(id);

        if(fileList.size() == 0){
            return new ResponseEntity<>(fileList, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(fileList, HttpStatus.OK);
    }
}
