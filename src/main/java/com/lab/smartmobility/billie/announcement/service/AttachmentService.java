package com.lab.smartmobility.billie.announcement.service;

import com.lab.smartmobility.billie.announcement.domain.Announcement;
import com.lab.smartmobility.billie.announcement.domain.Attachment;
import com.lab.smartmobility.billie.announcement.repository.AnnouncementRepository;
import com.lab.smartmobility.billie.announcement.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import java.io.FileInputStream;
import java.io.File;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AttachmentService {
    private final AnnouncementRepository announcementRepository;
    private final AttachmentRepository attachmentRepository;

    private static final String LOCAL_UPLOAD_PATH = "C:\\upload";
    private static final String SERVER_UPLOAD_PATH = "/home/smlab/billie/announcement";

    private static final String WINDOW_PATH = "\\";
    private static final String LINUX_PATH = "/";

    /*게시글의 첨부파일 객체 조회*/
    public List<byte[]> getAttachmentList(Long id) throws IOException {
        Announcement announcement = announcementRepository.findById(id).orElse(null);
        if(announcement == null){
            return new ArrayList<>();
        }

        List<Attachment> attachments = attachmentRepository.findByAnnouncement(announcement);

        List<byte[]> fileList = new ArrayList<>();
        for(Attachment attachment : attachments){
            File file = new File(SERVER_UPLOAD_PATH + LINUX_PATH + attachment.getFilename());

            byte[] fileContent = FileUtils.readFileToByteArray(file);
            fileList.add(fileContent);
        }
        return fileList;
    }

}
