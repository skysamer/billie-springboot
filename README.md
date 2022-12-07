> 공개가 허용된 코드임을 밝힙니다.

# :pushpin: 사내 전사관리 시스템 'Billie'
> 사내 전사관리 시스템 REST API 개발
> http://www.billie.work  
> [사용자 메뉴얼](http://www.billie.work/manual)

</br>

## 1. 제작 기간 & 참여 인원
- 2022년 2월 1일 ~ 6월 27일 (1차 런칭)
- 2022년 8월 22일 ~ 10월 14일 (2차 런칭)
- 2022년 10월 24일 ~ 11월 24일 (3차 런칭)
- 사내 프로젝트
- 백엔드 1명(본인), 프론트엔드 1명, 기획 및 디자인 2명

</br>

## 2. 사용 기술
#### `Back-end`
- Java 11
- Spring Boot 2.6.2
- Gradle
- Spring Data JPA
- QueryDSL
- MariaDB
- Spring Security
- Swagger 3.0

</br>

## 3. ERD 설계
<img width="650" alt="erd 수정" src="https://user-images.githubusercontent.com/73572543/205790990-b3c49570-3540-423e-a56c-b95cc13e6d7e.png">


## 4. 핵심 기능
### 4.1. 로그인 및 회원가입 기능
- Spring Security 및 JWT를 활용한 로그인 및 회원가입
- 미리 등록한 직원정보를 바탕으로 직원이 아닐경우, 회원가입 불가처리
- SMTP를 활용한 이메일 인증 및 비밀번호 초기화 기능
- 회원가입 시 직급에 따른 권한부여 (유저, 매니저, 어드민)

### 4.2. 예약 기능 (차량, 교통카드, 회의실)
- 원하는 날짜 및 시간에 차량, 교통카드 및 회의실을 예약하는 기능
- 해당 시각에 이미 예약이 있는 경우 예약을 반려하는 기능
- 30분마다 도는 스케줄러가 해당 도메인의 예약상태 변경
- 월별로 캘린더 표출 기능

### 4.3. 반납기능 (차량, 교통카드, 회의실, 법인카드)
- 반납 즉시, 해당 도메인의 대여상태 변경
- 반납을 하지 않을 경우, 해당 도메인은 예약시간이 지남에도 예약 불가

### 4.4. 게시판
- 조회수 기능
- 좋아요 기능
- 댓글, 대댓글 기능
- 익명 처리 기능
- 페이징 처리

### 4.5. 휴가/추가근무 관리
- 각 직원의 근속연수에 따라서, 매년 입사 월일마다 휴가 부여
- 매월 1일마다 추가근무 개수 초기화
- 휴가 이월기능 (남은 휴가개수가 없어도 휴가 신청 가능)
- 직원별 남은 휴가 개수 관리, 휴가 종류별로 소진되는 휴가개수 구분
- 24:00시 까지 신청가능한 추가근무

### 4.6. 승인기능
- 부서장승인, 최종 관리부 승인 절차로 이루어짐
- 휴가, 추가근무, 법인카드 및 경비청구 신청의 경우 승인절차가 필요

</br>


## 5. 트러블 슈팅

### 5.1. preflight request 이슈
- 웹 브라우저에서는 실제로 요청하려는 경로와 같은 URL에 대해 서버에 OPTIONS 메서드로 사전 요청을 보내고 요청을 할 수 있는 권한이 있는지 확인합니다.
- 그러나 Spring Security에서 preflight request로 요청한 option 메서드 요청을 리다이렉트 처리한다는 것을 알았습니다.
- 따라서 프론트엔드에서 get요청은 정상작동하는데 post요청이 오작동하였습니다.
- 결론적으로 SecurityContext에서 option메서드를 허용하여 문제를 해결했습니다.

</br>

### 5.2. 예약시간이 중복되는 경우를 체크해야 하는 문제
- 신규 예약 시간이 기존 예약시간과 겹치는 경우 예약을 금지시켜야 했습니다.
- 중복이되는 케이스는 총 4가지로 구분 할 수 있었습니다.  
  1. 시작 ~ 종료 안에 :시작, :종료가 포함되는 경우  
  2. 시작 ~ 종료 안에 :시작이 포함되는 경우  
  3. 시작 ~ 종료 안에 :종료가 포함되는 경우  
  4. 시작 ~ 종료 를 :시작, :종료가 포함하는 경우
- 사실 위 4가지는 '시작이 :종료보다 작고 종료는 :시작보다 큰 경우'로 통일됩니다.
- 따라서 다음의 sql 쿼리를 작성하여 중복예약시간을 체크하는 로직을 추가했습니다.

<details>
<summary><b>코드</b></summary>
<div markdown="1">

~~~sql
  
SELECT
	count(*) cnt
FROM
	`table`
WHERE
	`start` < :end
	AND
	`end` > :start
  
~~~

</div>
</details>

</br>

### 5.3. 게시글 조회 시 댓글 및 대댓글 쿼리 n + 1 문제(신규 기능)
- 신규 게시판 기능 개발 중, 게시판의 게시글을 조회 시 댓글과 대댓글을 모두 가져와야 하는 이슈가 있었습니다.
- 기존 게시글 조회 로직은 게시글 번호로 게시글을 가져온 다음, 게시글에 해당하는 부모 댓글을 가져옵니다.
- 부모댓글 목록에서 반복문을 돌려서 부모댓글에 매핑되는 자식 댓글이 있을 경우, 부모 댓글에 매핑하여 반환합니다.
- 이럴 경우 조회된 데이터 갯수(n) 만큼 연관관계의 조회 쿼리가 추가로 발생하는 n + 1문제가 발생하게 되었습니다. 즉, 부모댓글의 개수 만큼 자식 댓글을 조회하는 쿼리가 추가로 발생하는 것입니다.

<details>
<summary><b>기존코드</b></summary>
<div markdown="1">

~~~java

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

        replyList.forEach(replyResponseForm -> {
        List<NestedReplyResponseForm> children = jpaQueryFactory.select(Projections.fields(NestedReplyResponseForm.class,
        reply.id, reply.staff.staffNum, reply.staff.name, reply.content, reply.createdAt, reply.modifiedAt, reply.isAnonymous))
        .from(reply)
        .where(reply.parent.id.eq(replyResponseForm.getId())
        .and(reply.board.id.eq(id))
        )
        .fetch();
        replyResponseForm.addChildren(children);
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

~~~

</div>
</details>

- 쿼리를 줄이기 위해 우선 조회하려는 게시글에 매핑된 부모 댓글을 조회했습니다.
- 다음으로 게시글에 매핑되고, 부모댓글이 아닌 댓글을 모두 조회했습니다.
- 바로 부모 댓글 목록에서 iterator를 돌려서, 부모 댓글의 id와 자식 댓글 목록의 부모id가 매핑되는 경우 부모 댓글에 자식댓글 목록을 매핑하여 리턴했습니다.
- 이렇게 하면 게시글, 부모 댓글, 자식 댓글 총 3번의 쿼리만 나가게 되니 n+1 문제가 해결되었습니다.

<details>
<summary><b>개선된코드</b></summary>
<div markdown="1">

~~~java

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

~~~

</div>
</details>

</br>

### 5.4. 게시판의 현재 글에서 정렬된 순으로 이전글, 다음글이 표출되지 않는 문제(신규기능)
- 공지 게시판 목록은 메인공지 글을 우선적으로 정렬하고, 그 다음 순번의 역순으로 총 2번 정렬합니다.
- 또한 게시글이 중간에 삭제가 될 수 있기 때문에 현재글의 다음 번호 혹은 이전 번호를 가지고 이전글과 다음글을 조회하면 정렬된 순서와 맞지 않는 오류가 발생했습니다.
- 결국 정렬된 순서에 맞게 이전글과 다음글을 호출하려면, 정렬된 전체 리스트에서 별도의 인덱스를 부여하고 현재글의 인덱스에서 이전 혹은 다음 인덱스의 게시글을 호출해야 했습니다.
- 처음에는 서브쿼리를 고려하였으나, 서브쿼리 자체가 쿼리의 안티패턴이라는 이동욱 개발자님의 의견([https://jojoldu.tistory.com/379?category=637935])과(아마 성능상의 이슈 때문인 듯하다) 객체지향적으로 설계되어 있다면 서브쿼리 자체가 필요없다고 생각했기에, 더 이상 고려하지 않았습니다,
- 따라서 정렬된 게시글 목록을 List에 담아서 별도의 게시글 별 인덱스를 부여한 다음, 인덱스를 기준으로 이전글과 다음글을 찾는 로직을 작성했습니다.

<details>
<summary><b>정렬된 리스트 조회</b></summary>
<div markdown="1">

~~~java

public List<AnnouncementDetailsForm> getListOrderByIsMainAndId(){
        return jpaQueryFactory
        .select(Projections.fields(AnnouncementDetailsForm.class, announcement.id, announcement.title,
        announcement.content, announcement.isMain, announcement.type,
        announcement.createdAt, announcement.modifiedAt, announcement.likes, announcement.views))
        .from(announcement)
        .orderBy(announcement.isMain.desc(), announcement.id.desc())
        .fetch();
        }

~~~

</div>
</details>

<details>
<summary><b>인덱스 부여</b></summary>
<div markdown="1">

~~~java

public AnnouncementDetailsForm movePrev(Long id){
        List<AnnouncementDetailsForm> list = announcementRepositoryImpl.getListOrderByIsMainAndId();
        AnnouncementDetailsForm result = new AnnouncementDetailsForm();
        for(int i=0; i<list.size(); i++){
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
        return result;
        }

~~~

</div>
</details>

- 현재 글이 가장 이전글 혹은 가장 최근 글일 경우 여기서 다음 인덱스를 조회하면 `IndexOutOfBoundsException` 에러가 발생합니다.
- 따라서 위 구문을 try catch문으로 감싸서 에러가 캐치될 경우 이전 혹은 다음글이 없다는 메시지를 반환하도록 했습니다.


## 6. 그 외 트러블 슈팅
<details>
<summary>실시간으로 도메인의 대여 상태를 업데이트 해야하는 문제</summary>
<div markdown="1">

- 차량 및 교통카드는 매 30분 단위로 예약할 수 있습니다.
- 예약시간에 돌입하면 대여한 차량 또는 교통카드는 대여상태가 대여중으로 변해야 했습니다.
- 실시간으로 30분마다 체크되어 대여상태를 변경해야 했고, 각 도메인을 관리하는 테이블은 row수가 많지 않았으므로 간편한 spring scheduler를 활용하여 대여상태를 변경해주었습니다.

</div>
</details>

<details>
<summary>PasswordEncoder의 순환참조 문제</summary>
<div markdown="2">

- SecurityConfig 클래스에 PasswordEncoder 객체의 빈을 등록했더니, UserDetailsService 객체와 순환참조 오류를 일으켰습니다.
- 원인은 SecurityConfig 객체에서 UserDetailsService의 의존성을 주입받고, UserDetailsService에서도 SecurityConfig에서 빈을 등록한 PasswordEncoder의 의존성을 주입받고 있기 때문에 발생한 이슈였습니다.
- 그래서 별도의 설정파일을 생성하고 여기에 passwordEncoder의 빈을 등록하여 순환참조 오류를 해결했습니다.

</div>
</details>

<details>
<summary>프론트엔드와 통신 시 Cors 오류</summary>
<div markdown="3">

- 모바일 앱이 아닌 웹 프론트엔드와의 첫 협업이었기에 여러가지 어려움이 있었는데 그 중 하나가 Cors 이슈였습니다.
- 처음에는 @CrossOrigin 어노테이션을 활용하여 제어했으나, 이후에는 WebMvcConfigurer 인터페이스를 상속받은 config파일을 생성하여 Cors 이슈를 해결했습니다.

</div>
</details>

<details>
<summary>실시간 알림을 전송해야 하는 문제</summary>
<div markdown="3">

- 법인카드의 승인상태가 변경되었을 경우, 해당하는 유저에게 실시간으로 알림을 보내야 했습니다.
- 간단한 기능에 polling을 사용하는 것은 불필요한 http 오버헤드를 발생시킬 여지가 있으므로 고려하지 않았습니다.
- WebSocket을 활용할 수도 있었지만 서버에서만 단방향으로 데이터를 전송하면 충분했으므로, 단방향 통신인 sse를 사용했습니다.

</div>
</details>

<details>
<summary>데이터베이스 용량 full 문제</summary>
<div markdown="3">

- 기존 mariaDB는 루트 디렉토리에 데이터를 저장하고 있었습니다.
- 관리 중인 CentOS DB서버는 루트 디렉토리에 약 50G의 파티션을 할당중이었습니다.
- 따라서 데이터베이스에 할당된 용량도 50G이었고, 데이터 관련 사업을을 진행하면서 용량이 부족하게 되었습니다.
- 홈 디렉토리는 약 895G의 파티션이 할당되어 있었기에 이것을 활용하고자 했습니다.
- 처음에는 파티션 병합을 생각했으나, 이미 운영 중인 서버의 파티션을 병합하는 것은 매우 위험해보였고 더 이상 고려하지 않기로 했습니다.
- 따라서 홈 디렉토리에 기존 데이터베이스를 이관하기로 했고, 성공적으로 이관되었습니다.

</div>
</details>

</br>

<!-- ## 7. 리팩토링
### 7.1. 날짜와 시간을 다루는 로직의 개별 클래스화
- 특정 오브젝트를 예약하고 반납하는 기능이 핵심 기능이었기에 시간과 날짜를 다루는 로직이 매우 많았습니다.
- 가령, String으로 들어온 날짜 형식의 문자열을 LocalDate 혹은 시간까지 필요할 경우 LocalDateTime으로 변환하는 로직이나 기준 연월로 해당 월의 시작일과 종료일을 계산하는 로직이 그러했습니다.
- 원래는 각 도메인 별 서비스 단에 이러한 날짜 제어 로직을 전부 집어 넣었지만 이러한 설계방식이 단일책임원칙(SRP)을 위배한다는 사실을 깨달았습니다.
- 따라서 날짜 제어를 다루는 dateTimeUtil 클래스를 추가하고 각 메서드 별로 분기하여 필요한 날짜 제어 로직을 추가했습니다.

### 7.2. setter 함수의 제거
- 시작 당시에는 별 생각없이 각 도메인마다 getter, setter 함수를 추가하고 setter 함수로 속성값을 변경하는 로직을 짰습니다.
- 그러나 이러한 설계방식은 개방-폐쇄원칙(OCP)을 위배한다는 사실을 알았습니다. 곳곳에서 원인모를 변경이 발생할 수 있기 때문입니다.
- 따라서, setter함수를 제거하고 도메인 내부에 변경을 위한 메서드를 생성하여 이 메서드를 통해서만 속성값을 수정할 수 있도록 변경했습니다.
- 현재 전부 변경하지는 못했고 지속적으로 변경 중에 있는 작업 내용입니다.
  
### 7.3. 주석제거 및 네이밍 컨벤션의 통일성
- 주석은 소스코드에 영향을 미치지 않는다는 점이 오히려 코드 전체에 악영향을 미칠수 있다는 것을 깨닫고 최대한 제거하려 했습니다
- 메서드위에 있는 큰 주석은 남기되, 메서드 안에 존재하는 자잘한 주석은 최대한 제거하고 네이밍컨벤션을 더욱 신경써서 코드 자체가 설계문서로 활용될 수 있도록 했습니다.
  

### 7.4. 실시간 알림 전송 및 알림 데이터 저장 로직을 이벤트 리스너로 제어
- 법인카드 및 승인 절차에는 실시간 알림 전송로직이 포함되어 있었습니다.
- 그러나 법인카드 서비스단의 각 승인 로직 메서드에 알림관련 로직이 포함되어 있는 것은 단일책임원칙에 위배된다고 생각했습니다.
- 따라서 위 로직을 이벤트 리스너로 제어해서 처리했습니다.
	

### 7.5. 클래스를 각 역할 별로 세분화
- 기존에는 차량, 교통카드, 법인카드, 회의실 4개의 도메인 별로만 클래스가 분리되어있었습니다.
- 이러다보니 각 클래스의 길이가 많게는 600줄이상 되는 경우도 있었습니다.
- 따라서 각 클래스를 역할에 맞게 좀 더 세분화하여 클래스를 나누었습니다.
- 가령, 법인카드의 경우 법인카드, 법인카드 예약, 법인카드 승인, 법인카드 반납의 4가지 역할로 세분하여 클래스를 재설계했습니다.

### 7.6. url의 간소화 및 각 api를 restful 하도록 설계(신규기능 건)
- http에 대해 지속적으로 공부하면서 좀 더 restful한 설계에 대해 고민했습니다.
- url에는 리소스만 표현해야하고 구체적 행위는 http 메서드로 제어해야 한다는 것을 배웠습니다.
- 또한 모두 200 상태코드를 반환하는 것이 아닌 상황에 따라 적절한 http 상태 코드를 반환해야 한다는 것을 배웠습니다.
- 가령, 클라이언트쪽 오류를 캐치하여 적절한 400에러를 반환하고, 201 혹은 204를 상황에 맞게 사용하는 것입니다.
- 따라서 신규 기능은 이러한 설계원칙을 고려하여 개발중이고, 이후 기존 기능또한 리팩토링할 예정입니다. -->

</br>

## 7. 회고록
https://velog.io/@skysamer/%EC%82%AC%EB%82%B4%EC%A0%84%EC%82%AC%EA%B4%80%EB%A6%AC-%EC%8B%9C%EC%8A%A4%ED%85%9C-%EA%B0%9C%EB%B0%9C-%ED%9A%8C%EA%B3%A0%EB%A1%9D

