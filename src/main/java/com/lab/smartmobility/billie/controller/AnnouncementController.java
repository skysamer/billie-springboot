package com.lab.smartmobility.billie.controller;

import com.lab.smartmobility.billie.global.config.JwtTokenProvider;
import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.dto.announcement.AnnouncementDetailsForm;
import com.lab.smartmobility.billie.dto.announcement.AnnouncementRegisterForm;
import com.lab.smartmobility.billie.dto.announcement.MainAnnouncementCountDTO;
import com.lab.smartmobility.billie.entity.Announcement;
import com.lab.smartmobility.billie.entity.Attachment;
import com.lab.smartmobility.billie.entity.HttpBodyMessage;
import com.lab.smartmobility.billie.repository.AttachmentRepository;
import com.lab.smartmobility.billie.service.AnnouncementService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/announcement")
@Api(tags = {"공지 및 내규 api"})
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService service;
    private final AttachmentRepository attachmentRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final Log log;

    private static final String WINDOW_PATH = "\\";
    private static final String LINUX_PATH = "/";

    @ApiOperation(value = "게시글 등록")
    @ApiResponses({
            @ApiResponse(code = 201, message = "등록된 글번호가 리턴됩니다."),
            @ApiResponse(code = 400, message = "요청값은 null일 수 없습니다"),
    })
    @PostMapping("/admin")
    public ResponseEntity<HttpBodyMessage> register(@Valid @RequestPart("announcement") AnnouncementRegisterForm announcementRegisterForm,
                                                    @Nullable @RequestPart(value = "file", required = false) List<MultipartFile> attachments){
        HttpBodyMessage httpBodyMessage = null;
        try {
            httpBodyMessage = service.register(announcementRegisterForm, attachments);
        }catch (Exception e){
            log.error(e);
        }
        return new ResponseEntity<>(httpBodyMessage, HttpStatus.CREATED);
    }

    @ApiOperation(value = "게시글 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "종류 (전체는 all)"),
            @ApiImplicitParam(name = "date", value = "연월 (yyyy-MM, 전체는 all)"),
            @ApiImplicitParam(name = "keyword", value = "검색어"),
            @ApiImplicitParam(name = "page", value = "페이지"),
            @ApiImplicitParam(name = "size", value = "게시글 수")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 204, message = "조건에 맞는 데이터 없음")
    })
    @GetMapping("/user/{type}/{date}/{keyword}/{page}/{size}")
    public ResponseEntity<PageResult<Announcement>> getAnnouncementList(@PathVariable String type,
                                                        @PathVariable String date,
                                                        @PathVariable String keyword,
                                                        @PathVariable Integer page,
                                                        @PathVariable Integer size){
        PageResult<Announcement> pageResult = service.getAnnouncementList(type, date, keyword, PageRequest.of(page, size));
        if(pageResult.getCount() == 0){
            return new ResponseEntity<>(pageResult, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(pageResult, HttpStatus.OK);
    }

    @ApiOperation(value = "게시글 상세 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "번호"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 404, message = "id가 존재하지 않음"),
            @ApiResponse(code = 400, message = "토큰이 유효하지 않음")
    })
    @GetMapping("/user/{id}")
    public ResponseEntity<AnnouncementDetailsForm> getAnnouncement(@PathVariable Long id, @RequestHeader("X-AUTH-TOKEN") String token){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String email = jwtTokenProvider.getUserPk(token);
        AnnouncementDetailsForm announcement = service.getAnnouncement(id, email);
        if(announcement == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(announcement, HttpStatus.OK);
    }

    @ApiOperation(value = "첨부파일 다운로드")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filename", value = "파일이름 (uuid 포함)"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "다운로드 성공"),
            @ApiResponse(code = 404, message = "파일이 존재하지 않음")
    })
    @GetMapping("/user/attachment/{filename}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable String filename) {
        try{
            Attachment attachment = attachmentRepository.findByFilename(filename);
            Path filePath = Paths.get(attachment.getFilepath() + LINUX_PATH + attachment.getFilename());
            InputStreamResource resource = new InputStreamResource(new FileInputStream(filePath.toString()));
            String fileName = attachment.getFilename();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM).cacheControl(CacheControl.noCache())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .body(resource);
        }catch (Exception e){
            log.error(e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "게시글 삭제")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "게시글 번호")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "게시글 삭제 성공"),
            @ApiResponse(code = 400, message = "해당하는 게시글을 찾을 수 없음")
    })
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<HttpBodyMessage> remove(@PathVariable Long id){
        HttpBodyMessage bodyMessage = service.remove(id);
        if(bodyMessage.getCode().equals("fail")){
            return new ResponseEntity<>(bodyMessage, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(bodyMessage, HttpStatus.OK);
    }

    @ApiOperation(value = "게시글 수정")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "게시글 번호"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "게시글 수정 성공"),
            @ApiResponse(code = 400, message = "게시글을 찾을 수 없음 // 요청값은 null일 수 없습니다")
    })
    @PutMapping("/admin/{id}")
    public ResponseEntity<HttpBodyMessage> modify(@PathVariable Long id,
                                                  @Valid @RequestPart("announcement") AnnouncementRegisterForm announcementRegisterForm,
                                                  @Nullable @RequestPart("file") List<MultipartFile> attachments){
        HttpBodyMessage bodyMessage = null;
        try {
            bodyMessage = service.modify(id, announcementRegisterForm, attachments);
        }catch (Exception e){
            log.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(bodyMessage.getCode().equals("fail")){
            return new ResponseEntity<>(bodyMessage, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(bodyMessage, HttpStatus.OK);
    }

    @ApiOperation(value = "좋아요 클릭")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "게시글 번호"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "좋아요 수 계산 완료"),
            @ApiResponse(code = 400, message = "토큰이 유효하지 않습니다")
    })
    @PatchMapping("/user/like/{id}")
    public ResponseEntity<HttpBodyMessage> calculateLikeCount(@PathVariable("id") Long announcementId,
                                                              @RequestHeader(value = "X-AUTH-TOKEN") String token){
        if(!jwtTokenProvider.validateToken(token)){
            return new ResponseEntity<>(
                    new HttpBodyMessage("fail", "토큰이 유효하지 않습니다"), HttpStatus.BAD_REQUEST);
        }

        String email = jwtTokenProvider.getUserPk(token);
        HttpBodyMessage bodyMessage = service.calculateLikeCount(announcementId, email);
        return new ResponseEntity<>(bodyMessage, HttpStatus.OK);
    }

    @ApiOperation(value = "메인공지의 개수 계산")
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공")
    })
    @GetMapping("/admin/main-count")
    public ResponseEntity<MainAnnouncementCountDTO> countMainAnnouncement(){
        MainAnnouncementCountDTO mainAnnouncementCount = service.countMain();
        return new ResponseEntity<>(mainAnnouncementCount, HttpStatus.OK);
    }

    @ApiOperation(value = "이전글 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "번호"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 204, message = "이전글이 존재하지 않음")
    })
    @GetMapping("/user/prev/{id}")
    public ResponseEntity<AnnouncementDetailsForm> movePrev(@PathVariable Long id){
        AnnouncementDetailsForm announcement = service.movePrev(id);
        if(announcement == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(announcement, HttpStatus.OK);
    }

    @ApiOperation(value = "다음글 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "번호"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 204, message = "다음글이 존재하지 않음")
    })
    @GetMapping("/user/next/{id}")
    public ResponseEntity<AnnouncementDetailsForm> moveNext(@PathVariable Long id){
        AnnouncementDetailsForm announcement = service.moveNext(id);
        if(announcement == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(announcement, HttpStatus.OK);
    }
}
