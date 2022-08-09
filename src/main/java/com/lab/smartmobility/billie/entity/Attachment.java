package com.lab.smartmobility.billie.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity @Table(name = "tbl_announcement_attachment")
@ApiModel(value = "공지 및 내규 첨부파일 엔티티")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "번호")
    private Long id;

    @ApiModelProperty(value = "파일이름 (구분값포함)")
    private String filename;

    @ApiModelProperty(value = "원래의 파일이름")
    @Column(name = "origin_filename")
    private String originFilename;

    @ApiModelProperty(value = "파일경로")
    private String filepath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id")
    private Announcement announcement;

    public void setFileInfo(String filename, String originFilename, String filepath){
        this.filename = filename;
        this.originFilename = originFilename;
        this.filepath = filepath;
    }

    public void setAnnouncementInfo(Announcement announcement){
        this.announcement = announcement;
    }
}
