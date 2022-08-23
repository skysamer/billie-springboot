package com.lab.smartmobility.billie.entity;

import lombok.Getter;
import javax.persistence.*;

@Getter
@Entity @Table(name = "tbl_announcement_staff_like")
public class AnnouncementStaffLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(name = "announcement_id")
    private Long announcementId;

    public AnnouncementStaffLike(){

    }

    public AnnouncementStaffLike(String email, Long announcementId){
        this.email = email;
        this.announcementId = announcementId;
    }
}
