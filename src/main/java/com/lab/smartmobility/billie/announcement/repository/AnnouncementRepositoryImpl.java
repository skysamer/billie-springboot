package com.lab.smartmobility.billie.announcement.repository;

import com.lab.smartmobility.billie.global.dto.PageResult;
import com.lab.smartmobility.billie.announcement.dto.AnnouncementDetailsForm;
import com.lab.smartmobility.billie.announcement.domain.Announcement;
import com.lab.smartmobility.billie.global.util.DateTimeUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.lab.smartmobility.billie.announcement.domain.QAttachment.attachment;
import static com.lab.smartmobility.billie.announcement.domain.QAnnouncement.announcement;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnnouncementRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;
    private final DateTimeUtil dateTimeUtil;

    /*게시글 목록 조회 (페이징)*/
    public PageResult<Announcement> getAnnouncementPaging(String type, String date, String keyword, Pageable pageable){
        List<Announcement> content = getAnnouncementList(type, date, keyword, pageable);
        long count = getAnnouncementCount(type, date, keyword);
        return new PageResult<>(content, count);
    }

    private List<Announcement> getAnnouncementList(String type, String date, String keyword, Pageable pageable){
        return jpaQueryFactory
                .selectFrom(announcement)
                .where(Expressions.asBoolean(true).isTrue()
                        .and(typeEq(type))
                        .and(keywordLike(keyword))
                        .and(dateEq(date))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(announcement.isMain.desc(), announcement.id.desc())
                .fetch();
    }

    public long getAnnouncementCount(String type, String date, String keyword){
        return jpaQueryFactory
                .selectFrom(announcement)
                .where(Expressions.asBoolean(true).isTrue()
                        .and(typeEq(type))
                        .and(keywordLike(keyword))
                        .and(dateEq(date))
                )
                .orderBy(announcement.isMain.desc(), announcement.id.desc())
                .stream().count();
    }

    /*게시글 상세 조회*/
    public AnnouncementDetailsForm getAnnouncement(Long id){
        AnnouncementDetailsForm announcementDetailsForm = jpaQueryFactory
                .select(Projections.fields(AnnouncementDetailsForm.class, announcement.id, announcement.title,
                                announcement.content, announcement.isMain, announcement.type,
                                announcement.createdAt, announcement.modifiedAt, announcement.likes, announcement.views))
                .from(announcement)
                .where(announcement.id.eq(id))
                .fetchFirst();

        List<String> attachmentList = getAttachmentList(id);
        announcementDetailsForm.addFilename(attachmentList);
        return announcementDetailsForm;
    }

    public List<String> getAttachmentList(Long id){
        return jpaQueryFactory
                .select(attachment.filename)
                .from(attachment)
                .where(attachment.announcement.id.eq(id))
                .fetch();
    }

    public List<AnnouncementDetailsForm> getListOrderByIsMainAndId(){
        return jpaQueryFactory
                .select(Projections.fields(AnnouncementDetailsForm.class, announcement.id, announcement.title,
                        announcement.content, announcement.isMain, announcement.type,
                        announcement.createdAt, announcement.modifiedAt, announcement.likes, announcement.views))
                .from(announcement)
                .orderBy(announcement.isMain.desc(), announcement.id.desc())
                .fetch();
    }

    /*조회수 증가*/
    public void updateViewsCount(Long id){
        jpaQueryFactory
                .update(announcement)
                .set(announcement.views, announcement.views.add(1))
                .where(announcement.id.eq(id))
                .execute();
    }

    private BooleanExpression typeEq(String type) {
        return type.equals("all") ? null : announcement.type.eq(type);
    }

    private BooleanExpression keywordLike(String keyword) {
        return keyword.equals("all") ? null
                : announcement.title.contains(keyword).or(announcement.content.contains(keyword));
    }

    private BooleanExpression dateEq(String date) {
        if(date.equals("all")){
            return null;
        }

        LocalDateTime startedAt = dateTimeUtil.getStartDateTime(date);
        LocalDateTime endedAt = dateTimeUtil.getEndDateTime(date);
        return announcement.modifiedAt.between(startedAt, endedAt);
    }
}
