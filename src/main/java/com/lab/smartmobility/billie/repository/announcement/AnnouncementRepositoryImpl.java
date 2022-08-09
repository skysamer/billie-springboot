package com.lab.smartmobility.billie.repository.announcement;

import com.lab.smartmobility.billie.dto.PageResult;
import com.lab.smartmobility.billie.entity.Announcement;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.lab.smartmobility.billie.entity.QAnnouncement.announcement;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnnouncementRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;

    public PageResult<Announcement> getAnnouncementPaging(String type, String keyword, Pageable pageable){
        List<Announcement> content = getAnnouncementList(type, keyword, pageable);
        long count = getAnnouncementCount(type, keyword);
        return new PageResult<>(content, count);
    }

    private List<Announcement> getAnnouncementList(String type, String keyword, Pageable pageable){
        return jpaQueryFactory
                .selectFrom(announcement)
                .where(Expressions.asBoolean(true).isTrue()
                        .and(typeEq(type))
                        .and(keywordLike(keyword))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(announcement.isMain.desc(), announcement.id.desc())
                .fetch();
    }

    public long getAnnouncementCount(String type, String keyword){
        return jpaQueryFactory
                .selectFrom(announcement)
                .where(Expressions.asBoolean(true).isTrue()
                        .and(typeEq(type))
                        .and(keywordLike(keyword))
                )
                .orderBy(announcement.isMain.desc(), announcement.id.desc())
                .stream().count();
    }

    private BooleanExpression typeEq(String type) {
        return type.equals("all") ? null : announcement.type.eq(type);
    }

    private BooleanExpression keywordLike(String keyword) {
        return keyword.equals("all") ? null
                : announcement.title.contains(keyword).or(announcement.content.contains(keyword));
    }

}
