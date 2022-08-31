package com.lab.smartmobility.billie.repository.board;

import com.lab.smartmobility.billie.dto.PageResult;
import com.lab.smartmobility.billie.dto.ReplyResponseForm;
import com.lab.smartmobility.billie.dto.board.BoardDetailsForm;
import com.lab.smartmobility.billie.dto.board.BoardListForm;
import com.lab.smartmobility.billie.dto.reply.NestedReplyResponseForm;
import com.lab.smartmobility.billie.util.DateTimeUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.lab.smartmobility.billie.entity.QBoard.board;
import static com.lab.smartmobility.billie.entity.QReply.reply;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final DateTimeUtil dateTimeUtil;
    private final Log log;

    public PageResult<BoardListForm> getBoardListPaging(String keyword, String date, Pageable pageable){
        List<BoardListForm> list = getBoardList(keyword, date, pageable);
        long count = getBoardListCount(keyword, date);
        return new PageResult<>(list, count);
    }

    private List<BoardListForm> getBoardList(String keyword, String date, Pageable pageable){
        return jpaQueryFactory
                .select(Projections.fields(BoardListForm.class, board.id, board.title, board.content,
                        board.likes, board.views, board.createdAt, board.modifiedAt, board.replyCnt))
                .from(board)
                .where(Expressions.asBoolean(true).isTrue()
                        .and(keywordLike(keyword))
                        .and(dateEq(date))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(board.id.desc())
                .fetch();
    }

    private long getBoardListCount(String keyword, String date){
        return jpaQueryFactory
                .select(Projections.fields(BoardListForm.class, board.id, board.title, board.content,
                        board.likes, board.views, board.createdAt, board.modifiedAt, board.replyCnt))
                .from(board)
                .where(Expressions.asBoolean(true).isTrue()
                        .and(keywordLike(keyword))
                        .and(dateEq(date))
                )
                .orderBy(board.id.desc())
                .stream().count();
    }

    /*게시글 상세 조회*/
    public BoardDetailsForm getBoard(Long id){
        BoardDetailsForm boardDetailsForm = jpaQueryFactory
                .select(Projections.fields(BoardDetailsForm.class, board.id, board.title, board.content,
                        board.createdAt, board.modifiedAt, board.views, board.likes, board.replyCnt, board.isAnonymous,
                        board.staff.staffNum, board.staff.name))
                .from(board)
                .where(board.id.eq(id))
                .fetchFirst();
        if(boardDetailsForm == null){
            return null;
        }

        List<ReplyResponseForm> replyList = getReplyList(id);

        List<NestedReplyResponseForm> childrenReplyList = jpaQueryFactory.select(Projections.fields(NestedReplyResponseForm.class,
                        reply.parent.id.as("parentId"), reply.id, reply.staff.staffNum, reply.staff.name,
                        reply.content, reply.createdAt, reply.modifiedAt, reply.isAnonymous))
                .from(reply)
                .where(reply.parent.id.isNotNull()
                        .and(reply.board.id.eq(id))
                )
                .fetch();

        replyList.forEach(parent -> {
                            parent.addChildren(childrenReplyList.stream()
                                    .filter(child -> child.getParentId().equals(parent.getId()))
                                    .collect(Collectors.toList()));
                        });

        boardDetailsForm.addReply(replyList);
        return boardDetailsForm;
    }

    private List<ReplyResponseForm> getReplyList(Long id){
        return jpaQueryFactory
                .select(Projections.fields(ReplyResponseForm.class, reply.parent.id, reply.id, reply.content,
                        reply.createdAt, reply.modifiedAt, reply.staff.staffNum, reply.staff.name, reply.isAnonymous))
                .from(reply)
                .where(reply.board.id.eq(id)
                        .and(reply.parent.id.isNull())
                )
                .orderBy(reply.id.asc())
                .fetch();
    }

    /*조회수 증가*/
    public void plusViews(Long id){
        jpaQueryFactory
                .update(board)
                .set(board.views, board.views.add(1))
                .where(board.id.eq(id))
                .execute();
    }

    /*이전글 조회*/
    public BoardDetailsForm getPrev(Long id){
        BoardDetailsForm boardDetailsForm = jpaQueryFactory
                .select(Projections.fields(BoardDetailsForm.class, board.id, board.title, board.content,
                        board.createdAt, board.modifiedAt, board.views, board.likes, board.replyCnt, board.isAnonymous,
                        board.staff.staffNum, board.staff.name))
                .from(board)
                .where(board.id.lt(id))
                .orderBy(board.id.desc())
                .fetchFirst();
        if(boardDetailsForm == null){
            return null;
        }

        List<ReplyResponseForm> replyList = getReplyList(boardDetailsForm.getId());

        List<NestedReplyResponseForm> childrenReplyList = jpaQueryFactory.select(Projections.fields(NestedReplyResponseForm.class,
                        reply.parent.id.as("parentId"), reply.id, reply.staff.staffNum, reply.staff.name,
                        reply.content, reply.createdAt, reply.modifiedAt, reply.isAnonymous))
                .from(reply)
                .where(reply.parent.id.isNotNull()
                        .and(reply.board.id.eq(boardDetailsForm.getId()))
                )
                .fetch();

        replyList.forEach(parent -> {
            parent.addChildren(childrenReplyList.stream()
                    .filter(child -> child.getParentId().equals(parent.getId()))
                    .collect(Collectors.toList()));
        });

        boardDetailsForm.addReply(replyList);
        return boardDetailsForm;
    }

    /*다음글 조회*/
    public BoardDetailsForm getNext(Long id){
        BoardDetailsForm boardDetailsForm = jpaQueryFactory
                .select(Projections.fields(BoardDetailsForm.class, board.id, board.title, board.content,
                        board.createdAt, board.modifiedAt, board.views, board.likes, board.replyCnt, board.isAnonymous,
                        board.staff.staffNum, board.staff.name))
                .from(board)
                .where(board.id.gt(id))
                .orderBy(board.id.asc())
                .fetchFirst();
        if(boardDetailsForm == null){
            return null;
        }

        List<ReplyResponseForm> replyList = getReplyList(boardDetailsForm.getId());

        List<NestedReplyResponseForm> childrenReplyList = jpaQueryFactory.select(Projections.fields(NestedReplyResponseForm.class,
                        reply.parent.id.as("parentId"), reply.id, reply.staff.staffNum, reply.staff.name,
                        reply.content, reply.createdAt, reply.modifiedAt, reply.isAnonymous))
                .from(reply)
                .where(reply.parent.id.isNotNull()
                        .and(reply.board.id.eq(boardDetailsForm.getId()))
                )
                .fetch();

        replyList.forEach(parent -> {
            parent.addChildren(childrenReplyList.stream()
                    .filter(child -> child.getParentId().equals(parent.getId()))
                    .collect(Collectors.toList()));
        });

        boardDetailsForm.addReply(replyList);
        return boardDetailsForm;
    }

    private BooleanExpression keywordLike(String keyword) {
        return keyword.equals("all") ? null
                : board.title.contains(keyword).or(board.content.contains(keyword));
    }

    private BooleanExpression dateEq(String date) {
        if(date.equals("all")){
            return null;
        }

        LocalDateTime startedAt = dateTimeUtil.getStartDateTime(date);
        LocalDateTime endedAt = dateTimeUtil.getEndDateTime(date);
        return board.modifiedAt.between(startedAt, endedAt);
    }
}
