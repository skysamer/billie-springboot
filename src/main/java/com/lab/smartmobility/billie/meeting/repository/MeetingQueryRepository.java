package com.lab.smartmobility.billie.meeting.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.lab.smartmobility.billie.entity.QMeeting.meeting;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public boolean checkIsDuplicate(Long meetingNum, LocalDate date, LocalTime endTime, LocalTime startTime){
        return jpaQueryFactory
                .selectFrom(meeting)
                .where(Expressions.asBoolean(true).isTrue()
                        .and(meetingNumNe(meetingNum))
                        .and(meeting.date.eq(date))
                        .and(meeting.startTime.before(endTime))
                        .and(meeting.endTime.after(startTime))
                )
                .fetchFirst() != null;
    }

    private BooleanExpression meetingNumNe(Long meetingNum) {
        return meetingNum == -1 ? null : meeting.meetingNum.ne(meetingNum);
    }
}
