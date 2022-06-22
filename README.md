# :pushpin: 사내 전사관리 시스템 'Billie' (ReadMe 수정중......)
> 사내 전사관리 시스템 REST API 개발
> http://www.billie.work  
> [공식 api 설계 문서](http://59.6.99.141:8080/billie/swagger-ui/index.html#/)

</br>

## 1. 제작 기간 & 참여 인원
- 2022년 2월 1일 ~ 6월 27일 (1차 런칭)
- 사내 사이드 프로젝트
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
<img width="650" alt="erd 수정" src="https://user-images.githubusercontent.com/73572543/174931878-7d99052e-15be-4436-8b20-57f71874c22d.png">


## 4. 핵심 기능
1차 런칭에서의 핵심기능은 예약기능입니다. 

회사업무 및 출장 등에서 필요한 **차량**, **법인카드**, **교통카드** 및 **회의실**을 

간편하게 웹 어플리케이션에서 차량 및 교통카드 등을 예약하고 반납할 수 있는 플랫폼을 개발했습니다.

<details>
<summary><b>핵심 기능 설명 펼치기</b></summary>
<div markdown="1">

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

</div>
</details>

</br>

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
### 6.1. 프론트엔드와 통신 시 Cors 오류

    
</br>

## 6. 회고 / 느낀점
>프로젝트 개발 회고 글:
