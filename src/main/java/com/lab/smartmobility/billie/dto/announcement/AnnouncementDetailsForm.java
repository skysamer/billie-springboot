package com.lab.smartmobility.billie.dto.announcement;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class AnnouncementDetailsForm {
    @ApiModelProperty(value = "번호")
    private Long id;

    @ApiModelProperty(value = "종류")
    private String type;

    @ApiModelProperty(value = "제목")
    private String title;

    @ApiModelProperty(value = "내용")
    private String content;

    @ApiModelProperty(value = "메인공지 여부")
    private int isMain;

    @ApiModelProperty(value = "조회수")
    private long views;

    @ApiModelProperty(value = "생성일")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "수정일")
    private LocalDateTime modifiedAt;

    @ApiModelProperty(value = "첨부파일 정보 (uuid가 포함된 파일이름)")
    private final List<String> filenameList = new ArrayList<>();

    public void setFilenameList(List<String> filenameList){
        this.filenameList.addAll(filenameList);
    }
}
