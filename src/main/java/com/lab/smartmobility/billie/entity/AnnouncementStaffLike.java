package com.lab.smartmobility.billie.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "tbl_announcement_staff_like")
public class AnnouncementStaffLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(name = "announcement_id")
    private Long announcementId;
}
