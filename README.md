# :pushpin: 사내 전사관리 시스템 'Billie'
> 사내 전사관리 시스템 REST API 개발
> http://www.billie.work  
> [사용자 메뉴얼](http://www.billie.work/manual)

</br>

## 1. 제작 기간 & 참여 인원
- 2022년 2월 1일 ~ 6월 27일 (1차 런칭)
- 사내 사이드 프로젝트
- 백엔드 1명(본인), 프론트엔드 1명, 기획 및 디자인 2명

</br>

## 2. 사용 기술
#### `Back-end`
  - Java 11
  - Spring Boot 2.6.x
  - Gradle
  - Spring Data JPA
  - QueryDSL
  - MariaDB
  - Spring Security
  - Swagger 3.0

</br>

## 3. ERD 설계
<img width="650" alt="erd 수정" src="https://user-images.githubusercontent.com/73572543/174931878-7d99052e-15be-4436-8b20-57f71874c22d.png">


## 4. 핵심 기능
1차 런칭에서의 핵심기능은 예약기능입니다. 

회사업무 및 출장 등에서 필요한 **차량**, **법인카드**, **교통카드** 및 **회의실**을 

간편하게 웹 어플리케이션에서 차량 및 교통카드 등을 예약하고 반납할 수 있는 플랫폼을 개발했습니다.

### 4.1. Spring Security와 JWT를 활용한 로그인 및 회원가입
- **jwt 토큰 생성** :pushpin: [코드 확인](https://github.com/skysamer/billie-springboot/blob/master/src/main/java/com/lab/smartmobility/billie/config/JwtTokenProvider.java)
  
  - 로그인 시 DB정보가 일치할 경우 이메일 및 권한정보와 설정파일에 저장된 secretKey로 jwt 토큰을 생성합니다.
  - secretKey는 애플리케이션 구동 시, Base64방식으로 인코딩하여 초기화합니다.
  - 생성된 토큰을 헤더에 넣어 응답값과 같이 전송합니다.
  
 
- **jwt 토큰 검증** :pushpin: [코드 확인](https://github.com/skysamer/billie-springboot/blob/master/src/main/java/com/lab/smartmobility/billie/config/JwtAuthenticationFilter.java)
  
  - jwt 토큰을 검증하는 커스텀 filter를 적용합니다.
  - 로그인이 필요한 api를 요청할 경우, GenericFilterBean을 상속받은 jwtFilter클래스에서 토큰을 검증합니다.
  - 토큰이 유효할경우, 토큰에서 사용자정보를 추출하여 SecurityContextHolder 객체에 인증정보를 저장합니다.
  
  
### 4.2. Spring Scheduler를 활용한 대여상태 변경 기능
- **대여상태 변경 기능** :pushpin: [코드 확인](https://github.com/skysamer/billie-springboot/blob/master/src/main/java/com/lab/smartmobility/billie/task/VehicleScheduler.java)
  
  - 차량 및 교통카드는 매 30분 단위로 예약할 수 있습니다.
  - 따라서 30분 단위로 동작하는 스케줄러를 등록하여 30분마다 해당시각에 예약정보가 존재하는 경우, 차량 및 교통카드의 대여상태를 변경하는 기능을 추가했습니다.
  
  
### 4.3. 승인기능
- **부서장과 관리자의 법인카드 사용 승인 기능** :pushpin: [코드 확인](https://github.com/skysamer/billie-springboot/blob/master/src/main/java/com/lab/smartmobility/billie/service/CorporationCardService.java)
  - 법인카드는 일반직원이 함부로 사용할 수 없습니다. 반드시 부서장 및 관리부의 승인을 받아야합니다.
  - 법인카드 사용을 신청할 경우, 자신이 속한 부서의 부서장에게 승인요청 및 실시간 알림이 전송됩니다.
  - 부서장이 사용을 승인할 경우 다시 관리부에게 승인 요청이 전송되고, 관리부가 최종적으로 승인하면 승인 플로우가 종료되고 예약 일정에 노출됩니다.
  - 부서장이 신청하면 바로 관리부에게 승인요청이 전송됩니다. 


### 4.4. 실시간 알림기능
- **승인 요청 시, 실시간 알림 기능** :pushpin: [코드 확인](https://github.com/skysamer/billie-springboot/blob/master/src/main/java/com/lab/smartmobility/billie/util/SseEmitterSender.java)
  - 실시간 알림의 경우, 프론트엔드에서 일방적으로 수신하면 되었기에 일반적인 웹소켓보다는 실시간 단방향 통신인 SSE(server-sent-event) 기능을 활용했습니다.
  - 우선 로그인 시, 반환받은 토큰을 사용하여 서버 sse를 구독합니다.
  - 유저의 pk값을 활용하여 SseEmitter 객체를 저장합니다.
  - 이후 실시간 알림전송이 필요한 로직에서 SseEmitter.send() 메서드를 활용하여 실시간 알림 메시지를 전송합니다.
  
### 4.5. 동적 조건 검색
- **QueryDSL을 활용한 동적 조건 검색 기능** :pushpin: [코드 확인](https://github.com/skysamer/billie-springboot/blob/master/src/main/java/com/lab/smartmobility/billie/repository/corporation/CorporationReturnRepositoryImpl.java)
  - 법인카드의 반납이력 목록의 경우, 날짜(연/월), 해당 카드, 폐기된 카드 정보 포함 여부 등을 동적으로 조건절에 추가하여 데이터를 뿌려야 했습니다.
  - 이에 QueryDSL과 BooleanExpression을 반환하는 메서드를 생성하여 동적으로 쿼리에 조건절을 추가하도록 했습니다.
  - Projections 객체를 활용하여 데이터를 반납이력 폼에 맞게 추출했습니다.
  
### 4.6 구글 SMTP 이메일 전송
- **비밀번호 초기화 기능** :pushpin: [코드 확인](https://github.com/skysamer/billie-springboot/blob/master/src/main/java/com/lab/smartmobility/billie/service/StaffService.java)
  - 비밀번호를 잊어버렸을 경우, 랜덤하게 10자리 문자열을 생성하여 비밀번호를 초기화 해주고 javaMailSender로 임의의 문자열을 전송하는 기능을 구현했습니다.
  
- **이메일 인증** :pushpin: [코드 확인](https://github.com/skysamer/billie-springboot/blob/master/src/main/java/com/lab/smartmobility/billie/service/StaffService.java)
  - 저희 웹 애플리케이션은 사내 직원만 이용할 수 있게끔 만들어야 했습니다.
  - 관리부에서 직원을 등록할 수 있는 폼을 만든 다음, 거기에 각 직원의 이메일 정보를 입력하도록 했습니다.
  - 이때 입력된 이메일로 UUID로 만든 임의의 문자열 토큰을 전송하여 직원 확인 및 이메일을 인증하는 로직을 구현했습니다.
  - 이때 생성시간을 저장하여 10분 이내로 인증하지 않으면 인증에 실패하도록 했습니다.
  
### 4.7 중복시간체크
  - **신규예약시간이 기존예약시간과 겹치는지 체크하는 로직** :pushpin: [코드 확인](https://github.com/skysamer/billie-springboot/blob/master/src/main/java/com/lab/smartmobility/billie/service/VehicleService.java)
    - 신규 예약 시간이 기존 예약시간과 겹치는 경우 예약을 금지시켜야 했습니다.
    - 중복이되는 케이스는 총 4가지로 구분 할 수 있었습니다.
      1. 시작 ~ 종료 안에 :시작, :종료가 포함되는 경우
      2. 시작 ~ 종료 안에 :시작이 포함되는 경우
      3. 시작 ~ 종료 안에 :종료가 포함되는 경우
      4. 시작 ~ 종료 를 :시작, :종료가 포함하는 경우
    - 기존에는 위 케이스를 모두 or로 묶어서 관리했지만, 하나의 코드로 해결이 가능하다는 것을 알았습니다.
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

    - 따라서 위의 쿼리문을 쿼리메소드 및 QueryDSL로 변환하여 처리했습니다.  


## 5. 트러블 슈팅
### 5.1. 프론트엔드와 통신 시 Cors 오류
- 모바일 앱이 아닌 웹 프론트엔드와의 첫 협업이었기에 여러가지 어려움이 있었는데 그 중 하나가 Cors 이슈였습니다.
- 처음에는 @CrossOrigin 어노테이션을 활용하여 제어했으나, 이후에는 WebMvcConfigurer 인터페이스를 상속받은 config파일을 생성하여 Cors 이슈를 해결했습니다.

<details>
<summary><b>코드</b></summary>
<div markdown="1">

~~~java
  
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .exposedHeaders("*")
                .allowCredentials(true);
    }
}
  
~~~

</div>
</details>

### 5.2. preflight request 이슈
- 웹 브라우저에서는 실제로 요청하려는 경로와 같은 URL에 대해 서버에 OPTIONS 메서드로 사전 요청을 보내고 요청을 할 수 있는 권한이 있는지 확인합니다.
- 그러나 Spring Security에서 preflight request로 요청한 option 메서드 요청을 리다이렉트 처리한다는 것을 알았습니다.
- 따라서 프론트엔드에서 get요청은 정상작동하는데 post요청이 오작동하였습니다.
- 결론적으로 시큐리티 설정파일에서 option메서드를 허용하여 문제를 해결했습니다.

<details>
<summary><b>코드</b></summary>
<div markdown="1">

~~~java
  
@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .cors().disable()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .authorizeRequests((requests) ->
                requests.antMatchers(HttpMethod.OPTIONS, "/**/*").permitAll()
                        .antMatchers("/login", "/sign-up", "/findPassword", "/check-login", "/send-email-token", "/verify-email-token", "/swagger-ui/index.html").permitAll()
  .and()
  .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                   UsernamePasswordAuthenticationFilter.class)
  
~~~

</div>
</details>


</br>

## 6. 리팩토링
### 6.1. 날짜와 시간을 다루는 로직의 개별 클래스화
- 특정 오브젝트를 예약하고 반납하는 기능이 핵심 기능이었기에 시간과 날짜를 다루는 로직이 매우 많았습니다.
- 원래는 각 도메인 별 서비스 단에 이러한 날짜 제어 로직을 전부 집어 넣었지만 이러한 설계방식이 단일책임원칙(SRP)을 위배한다는 사실을 깨달았습니다.
- 따라서 날짜 제어를 다루는 dateTimeUtil 클래스를 추가하고 각 메서드 별로 분기하여 필요한 날짜 제어 로직을 추가했습니다.

<details>
<summary><b>코드</b></summary>
<div markdown="1">

~~~java
  
@Component
public class DateTimeUtil {
    private final Calendar cal=Calendar.getInstance();

    public LocalDateTime getStartDateTime(String baseDate){
        int year=Integer.parseInt(baseDate.substring(0, baseDate.indexOf("-")));
        int month=Integer.parseInt(baseDate.substring(baseDate.indexOf("-")+1));
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH,month);
        cal.set(year, month-1, 1);

        return LocalDateTime.of(year, month, 1, 0, 0, 0);
    }

    public LocalDateTime getEndDateTime(String baseDate){
        int year=Integer.parseInt(baseDate.substring(0, baseDate.indexOf("-")));
        int month=Integer.parseInt(baseDate.substring(baseDate.indexOf("-")+1));
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH,month);
        cal.set(year, month-1, 1);
        int end=cal.getActualMaximum(Calendar.DATE);

        return LocalDateTime.of(year, month, end, 23, 59, 59);
    }

    public List<LocalDate> getStartDateAndEndDate(LocalDate baseDate){
        int year=baseDate.getYear();
        int month=baseDate.getMonthValue();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH,month);
        cal.set(year, month-1, 1);
        int end=cal.getActualMaximum(Calendar.DATE);
        LocalDate startDate=LocalDate.of(year, month, 1);
        LocalDate endDate=LocalDate.of(year, month, end);

        return new ArrayList<>(List.of(startDate, endDate));
    }

    public LocalDate getStartDate(String baseDate){
        int year=Integer.parseInt(baseDate.substring(0, baseDate.indexOf("-")));
        int month=Integer.parseInt(baseDate.substring(baseDate.indexOf("-")+1));
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH,month);
        cal.set(year, month-1, 1);

        return LocalDate.of(year, month, 1);
    }

    public LocalDate getEndDate(String baseDate){
        int year=Integer.parseInt(baseDate.substring(0, baseDate.indexOf("-")));
        int month=Integer.parseInt(baseDate.substring(baseDate.indexOf("-")+1));
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH,month);
        cal.set(year, month-1, 1);
        int end=cal.getActualMaximum(Calendar.DATE);

        return LocalDate.of(year, month, end);
    }

    public LocalDateTime combineDateAndTime(LocalDate date, LocalTime time){
        return LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(),
                time.getHour(), time.getMinute(), 0);
    }
}

~~~

</div>
</details>

### 6.2. setter 함수의 제거
- 시작 당시에는 별 생각없이 각 도메인마다 getter, setter 함수를 추가하고 setter 함수로 속성값을 변경하는 로직을 짰습니다.
- 그러나 이러한 설계방식은 개방-폐쇄원칙(OCP)을 위배한다는 사실을 알았습니다. 곳곳에서 원인모를 변경이 발생할 수 있기 때문입니다.
- 따라서, setter함수를 제거하고 도메인 내부에 변경을 위한 메서드를 생성하여 이 메서드를 통해서만 속성값을 수정할 수 있도록 변경했습니다.
- 현재 전부 변경하지는 못했고 지속적으로 변경 중에 있는 작업 내용입니다.
  
### 6.3. 주석제거 및 네이밍 컨벤션의 통일성
- 주석은 소스코드에 영향을 미치지 않는다는 점이 오히려 코드 전체에 악영향을 미칠수 있다는 것을 깨닫고 최대한 제거하려 했습니다
- 메서드위에 있는 큰 주석은 남기되, 메서드 안에 존재하는 자잘한 주석은 최대한 제거하고 네이밍컨벤션을 더욱 신경써서 코드 자체가 설계문서로 활용될 수 있도록 했습니다.
  
### 6.4. 클래스 내 중복 코드의 비공개 메서드화
  - 차량과 교통카드 예약의 경우, 신규 예약, 예약 수정 및 관리자 예약 수정 api에 공통적으로 기존에 등록된 예약날짜 및 시간과 겹치는지 체크하는 로직이 존재합니다.
  - 따라서 이 로직을 공통 메서드화 하여 중복을 최대한 제거하려고 노력했습니다.

<details>
<summary><b>코드</b></summary>
<div markdown="1">

~~~java

/*신규 예약이 기존 예약 날짜 및 시간과 겹치는지 체크*/
private boolean checkReservationIsDuplicate(Long reservationNum, LocalDateTime rentedAt, TrafficCard trafficCard){
        List<TrafficCardReservation> reservationList=reservationRepository.findAllByReturnStatus(0);
        if(reservationNum != null){
            reservationList.remove(reservationRepository.findByReservationNum(reservationNum));
        }

        for(TrafficCardReservation reservation : reservationList){
            if(((reservation.getRentedAt().isBefore(rentedAt) || reservation.getRentedAt().isEqual(rentedAt)) &&
                    (reservation.getReturnedAt().isAfter(rentedAt)))
                    && trafficCard.getCardNum().equals(reservation.getTrafficCard().getCardNum())){
                return true;
            }
        }
        return false;
}

~~~

</div>
</details>

### 6.5. 실시간 알림 전송 및 알림 데이터 저장 로직을 이벤트 리스너로 제어
  - 법인카드 및 승인 절차에는 실시간 알림 전송로직이 포함되어 있었습니다.
  - 그러나 법인카드 서비스단의 각 승인 로직 메서드에 알림관련 로직이 포함되어 있는 것은 단일책임원칙에 위배된다고 생각했습니다.
  - 따라서 위 로직을 이벤트 리스너로 제어해서 처리했습니다.


<details>
<summary><b>코드</b></summary>
<div markdown="1">

~~~java

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {
    private final NotificationRepository notificationRepository;
    private final SseEmitterSender sseEmitterSender;

    @EventListener
    public void corporationNotificationEvent(NotificationEventDTO notificationEventDTO){
        Notification notification=Notification.builder()
                .requester(notificationEventDTO.getRequester())
                .receiver(notificationEventDTO.getReceiver())
                .type("corporation")
                .approveStatus(notificationEventDTO.getApprovalStatus())
                .build();

        notificationRepository.save(notification);
        sseEmitterSender.sendSseEmitter(notificationEventDTO.getApproval());
    }
}

~~~

</div>
</details>
	
	

### 6.6. 클래스를 각 역할 별로 세분화
  - 기존에는 차량, 교통카드, 법인카드, 회의실 4개의 도메인 별로만 클래스가 분리되어있었습니다.
  - 이러다보니 각 클래스의 길이가 많게는 600줄이상 되는 경우도 있었습니다.
  - 따라서 각 클래스를 역할에 맞게 좀 더 세분화하여 클래스를 나누었습니다.
  - 가령, 법인카드의 경우 법인카드, 법인카드 예약, 법인카드 승인, 법인카드 반납의 4가지 역할로 세분하여 클래스를 재설계했습니다.

### 6.7. url의 간소화 및 각 api를 restful 하도록 설계(신규)
  - http에 대해 지속적으로 공부하면서 좀 더 restful한 설계에 대해 고민했습니다.
  - url에는 리소스만 표현해야하고 구체적 행위는 http 메서드로 제어해야 한다는 것을 배웠습니다.
  - 또한 모두 200 상태코드를 반환하는 것이 아닌 상황에 따라 적절한 http 상태 코드를 반환해야 한다는 것을 배웠습니다.
  - 따라서 신규 기능은 이러한 설계원칙을 고려하여 개발중이고, 이후 기존 기능또한 리팩토링할 예정입니다.
    
</br>
	

## 8. 2차 개발 및 추후 리팩토링 시 고려사항
- api 반환타입을 ResponseEntity로 설정
- 세밀한 트랜잭션 관리 (서비스 단으로 트랜잭션 이관 및 리포지토리를 readonly 설정)
- 가독성이 떨어지는 경우, 쿼리메소드를 지양하고 queryDSL 고려
- 명확한 책임의 분리

