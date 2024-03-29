package com.lab.smartmobility.billie.announcement.service;

import com.lab.smartmobility.billie.announcement.dto.AnnouncementListForm;
import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.announcement.dto.AnnouncementDetailsForm;
import com.lab.smartmobility.billie.announcement.dto.AnnouncementRegisterForm;
import com.lab.smartmobility.billie.announcement.dto.MainAnnouncementCountDTO;
import com.lab.smartmobility.billie.announcement.domain.Announcement;
import com.lab.smartmobility.billie.announcement.domain.AnnouncementStaffLike;
import com.lab.smartmobility.billie.announcement.domain.Attachment;
import com.lab.smartmobility.billie.global.dto.HttpBodyMessage;
import com.lab.smartmobility.billie.announcement.repository.AttachmentRepository;
import com.lab.smartmobility.billie.announcement.repository.AnnouncementRepository;
import com.lab.smartmobility.billie.announcement.repository.AnnouncementRepositoryImpl;
import com.lab.smartmobility.billie.announcement.repository.AnnouncementStaffLikeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.NamedNativeQuery;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final AnnouncementRepositoryImpl announcementRepositoryImpl;
    private final AttachmentRepository attachmentRepository;
    private final AnnouncementStaffLikeRepository announcementStaffLikeRepository;
    private final ModelMapper modelMapper;
    private final Log log;

    private static final String LOCAL_UPLOAD_PATH = "C:\\upload";
    private static final String SERVER_UPLOAD_PATH = "/home/smlab/billie/announcement";

    /*게시글 등록*/
    public HttpBodyMessage register(AnnouncementRegisterForm announcementRegisterForm, List<MultipartFile> attachments) throws IOException {
        if(announcementRegisterForm.getIsExceedMainCount() == 1){
            cancelOldestMain();
        }

        Announcement announcement = modelMapper.map(announcementRegisterForm, Announcement.class);
        announcementRepository.save(announcement);
        if(attachments != null){
            Announcement savedAnnouncement = announcementRepository.findFirstByOrderByIdDesc();
            uploadFiles(attachments, savedAnnouncement);
        }
        return new HttpBodyMessage("success", announcement.getId());
    }

    /*가장 오래된 메인 공지 취소*/
    private void cancelOldestMain(){
        Announcement oldestMain = announcementRepository.findFirstByIsMainOrderByModifiedAt(1);
        oldestMain.cancelMain();
    }

    /*파일 업로드*/
    private void uploadFiles(List<MultipartFile> attachments, Announcement savedAnnouncement) throws IOException {
        File uploadPath = new File(SERVER_UPLOAD_PATH);
        for(MultipartFile attachment : attachments){
            String uuid = UUID.randomUUID().toString();
            String filename = uuid + "_" + attachment.getOriginalFilename();

            saveAttachmentInfo(uuid, attachment.getOriginalFilename(), savedAnnouncement);
            File saveFile=new File(uploadPath, filename);
            attachment.transferTo(saveFile);
        }
    }

    /*파일정보 저장*/
    private void saveAttachmentInfo(String uuid, String originFilename, Announcement savedAnnouncement){
        String filename = uuid + "_" + originFilename;

        Attachment attachmentInfo = new Attachment();
        attachmentInfo.setFileInfo(filename, originFilename, SERVER_UPLOAD_PATH);
        attachmentInfo.setAnnouncementInfo(savedAnnouncement);
        attachmentRepository.save(attachmentInfo);
    }

    /*게시글 목록 조회*/
    public PageResult<AnnouncementListForm> getAnnouncementList(String type, String date, String keyword, Pageable pageable){
        return announcementRepositoryImpl.getAnnouncementPaging(type, date, keyword, pageable);
    }

    /*게시글 상세 조회*/
    public AnnouncementDetailsForm getAnnouncement(Long id, String email){
        AnnouncementDetailsForm announcement = announcementRepositoryImpl.getAnnouncement(id);
        if(announcement == null){
            return null;
        }

        checkIsLiked(email, announcement);
        announcementRepositoryImpl.updateViewsCount(id);
        return announcement;
    }

    private void checkIsLiked(String email, AnnouncementDetailsForm announcement){
        boolean isLiked = announcementStaffLikeRepository.existsByEmailAndAnnouncementId(email, announcement.getId());
        announcement.checkIsLiked(isLiked);
    }

    /*게시글 삭제*/
    public HttpBodyMessage remove(Long id){
        Announcement announcement = announcementRepository.findById(id).orElse(null);
        if(announcement == null){
            return new HttpBodyMessage("fail", "해당하는 게시글을 찾을 수 없음");
        }

        announcementRepository.delete(announcement);
        return new HttpBodyMessage("success", "게시글 삭제 성공");
    }

    /*게시글 수정*/
    public HttpBodyMessage modify(Long id, AnnouncementRegisterForm announcementRegisterForm, List<MultipartFile> attachments) throws IOException {
        if(announcementRegisterForm.getIsExceedMainCount() == 1){
            cancelOldestMain();
        }

        Announcement announcement = announcementRepository.findById(id).orElse(null);
        if(announcement == null){
            return new HttpBodyMessage("fail", "게시글을 찾을 수 없음");
        }
        modelMapper.map(announcementRegisterForm, announcement);

        if(attachmentRepository.findByAnnouncement(announcement).size() != 0){
            attachmentRepository.deleteAllByAnnouncement(announcement);
        }

        if(attachments != null){
            uploadFiles(attachments, announcement);
        }
        return new HttpBodyMessage("success", "게시글 수정 성공");
    }

    /*좋아요 버튼 클릭*/
    public HttpBodyMessage calculateLikeCount(Long announcementId, String email){
        if(announcementStaffLikeRepository.existsByEmailAndAnnouncementId(email, announcementId)){
            minusLike(announcementId, email);
        }else{
            plusLike(announcementId, email);
        }
        return new HttpBodyMessage("success", "좋아요 수 계산 완료");
    }

    private void minusLike(Long announcementId, String email){
        Announcement announcement = announcementRepository.findById(announcementId).orElseThrow();
        announcement.minusLike();
        announcementStaffLikeRepository.deleteByEmailAndAnnouncementId(email, announcementId);
    }

    private void plusLike(Long announcementId, String email){
        Announcement announcement = announcementRepository.findById(announcementId).orElseThrow();
        announcement.plusLike();

        AnnouncementStaffLike announcementStaffLike = new AnnouncementStaffLike(email, announcementId);
        announcementStaffLikeRepository.save(announcementStaffLike);
    }

    /*메인 공지의 수*/
    public MainAnnouncementCountDTO countMain(){
        long count = announcementRepository.countByIsMain(1);
        return new MainAnnouncementCountDTO(count);
    }

    /*이전글 이동*/
    public AnnouncementDetailsForm movePrev(Long id, String email){
        List<AnnouncementDetailsForm> list = announcementRepositoryImpl.getListOrderByIsMainAndId();
        AnnouncementDetailsForm result = new AnnouncementDetailsForm();

        int size = list.size();
        for(int i=0; i<size; i++){
            try{
                if(list.get(i).getId().equals(id)){
                    result = list.get(i + 1);
                    break;
                }
            }catch (IndexOutOfBoundsException e){
                return null;
            }
        }

        addFilename(result);
        announcementRepositoryImpl.updateViewsCount(result.getId());
        checkIsLiked(email, result);
        return result;
    }

    private void addFilename(AnnouncementDetailsForm result){
        List<String> filenameList = announcementRepositoryImpl.getAttachmentList(result.getId());
        result.addFilename(filenameList);
    }

    /*다음글 이동*/
    public AnnouncementDetailsForm moveNext(Long id, String email){
        List<AnnouncementDetailsForm> list = announcementRepositoryImpl.getListOrderByIsMainAndId();
        AnnouncementDetailsForm result = new AnnouncementDetailsForm();
        int size = list.size();
        for(int i=0; i<size; i++){
            try{
                if(list.get(i).getId().equals(id)){
                    result = list.get(i - 1);
                    break;
                }
            }catch (IndexOutOfBoundsException e){
                return null;
            }
        }

        addFilename(result);
        announcementRepositoryImpl.updateViewsCount(result.getId());
        checkIsLiked(email, result);
        return result;
    }

}
