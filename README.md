# :pushpin: Billie(ReadMe 수정중)
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
  
 
- **URL 정규식 체크** :pushpin: [코드 확인](https://github.com/skysamer/billie-springboot/blob/master/src/main/java/com/lab/smartmobility/billie/config/JwtAuthenticationFilter.java)
  
  - jwt 토큰을 검증하는 커스텀 filter를 적용합니다.
  - 로그인이 필요한 api를 요청할 경우, GenericFilterBean을 상속받은 jwtFilter클래스에서 토큰을 검증합니다.
  - 토큰이 유효할경우, 토큰에서 사용자정보를 추출하여 SecurityContextHolder 객체에 인증정보를 저장합니다.

</div>
</details>

</br>

## 5. 핵심 트러블 슈팅
### 5.1. 프론트엔드와 통신 시 Cors 오류
- 모바일 앱이 아닌 웹 프론트엔드와의 첫 협업이었기에 여러가지 어려움이 있었는데 그 중 하나가 Cors 이슈였습니다.
- 처음에는 @CrossOrigin 어노테이션을 활용하여 제어했으나, 이후에는 WebMvcConfigurer 인터페이스를 상속받은 config파일을 생성하여 Cors 이슈를 해결했습니다.
- 사실 이렇게 모든 url에 대하여 cors를 허용하는 것은 좋지 않은 설계라 생각하고 변경할 예정입니다.

<details>
<summary><b>기존 코드</b></summary>
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


</br>

## 6. 그 외 트러블 슈팅
<details>
<summary>npm run dev 실행 오류</summary>
<div markdown="1">

- Webpack-dev-server 버전을 3.0.0으로 다운그레이드로 해결
- `$ npm install —save-dev webpack-dev-server@3.0.0`

</div>
</details>

<details>
<summary>vue-devtools 크롬익스텐션 인식 오류 문제</summary>
<div markdown="1">
  
  - main.js 파일에 `Vue.config.devtools = true` 추가로 해결
  - [https://github.com/vuejs/vue-devtools/issues/190](https://github.com/vuejs/vue-devtools/issues/190)
  
</div>
</details>

<details>
<summary>ElementUI input 박스에서 `v-on:keyup.enter="메소드명"`이 정상 작동 안하는 문제</summary>
<div markdown="1">
  
  - `v-on:keyup.enter.native=""` 와 같이 .native 추가로 해결
  
</div>
</details>

<details>
<summary> Post 목록 출력시에 Member 객체 출력 에러 </summary>
<div markdown="1">
  
  - 에러 메세지(500에러)
    - No serializer found for class org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationConfig.SerializationFeature.FAIL_ON_EMPTY_BEANS)
  - 해결
    - Post 엔티티에 @ManyToOne 연관관계 매핑을 LAZY 옵션에서 기본(EAGER)옵션으로 수정
  
</div>
</details>
    
<details>
<summary> 프로젝트를 git init으로 생성 후 발생하는 npm run dev/build 오류 문제 </summary>
<div markdown="1">
  
  ```jsx
    $ npm run dev
    npm ERR! path C:\Users\integer\IdeaProjects\pilot\package.json
    npm ERR! code ENOENT
    npm ERR! errno -4058
    npm ERR! syscall open
    npm ERR! enoent ENOENT: no such file or directory, open 'C:\Users\integer\IdeaProjects\pilot\package.json'
    npm ERR! enoent This is related to npm not being able to find a file.
    npm ERR! enoent

    npm ERR! A complete log of this run can be found in:
    npm ERR!     C:\Users\integer\AppData\Roaming\npm-cache\_logs\2019-02-25T01_23_19_131Z-debug.log
  ```
  
  - 단순히 npm run dev/build 명령을 입력한 경로가 문제였다.
   
</div>
</details>    

<details>
<summary> 태그 선택후 등록하기 누를 때 `object references an unsaved transient instance - save the transient instance before flushing` 오류</summary>
<div markdown="1">
  
  - Post 엔티티의 @ManyToMany에 영속성 전이(cascade=CascadeType.ALL) 추가
    - JPA에서 Entity를 저장할 때 연관된 모든 Entity는 영속상태여야 한다.
    - CascadeType.PERSIST 옵션으로 부모와 자식 Enitity를 한 번에 영속화할 수 있다.
    - 참고
        - [https://stackoverflow.com/questions/2302802/object-references-an-unsaved-transient-instance-save-the-transient-instance-be/10680218](https://stackoverflow.com/questions/2302802/object-references-an-unsaved-transient-instance-save-the-transient-instance-be/10680218)
   
</div>
</details>    

<details>
<summary> JSON: Infinite recursion (StackOverflowError)</summary>
<div markdown="1">
  
  - @JsonIgnoreProperties 사용으로 해결
    - 참고
        - [http://springquay.blogspot.com/2016/01/new-approach-to-solve-json-recursive.html](http://springquay.blogspot.com/2016/01/new-approach-to-solve-json-recursive.html)
        - [https://stackoverflow.com/questions/3325387/infinite-recursion-with-jackson-json-and-hibernate-jpa-issue](https://stackoverflow.com/questions/3325387/infinite-recursion-with-jackson-json-and-hibernate-jpa-issue)
        
</div>
</details>  
    
<details>
<summary> H2 접속문제</summary>
<div markdown="1">
  
  - H2의 JDBC URL이 jdbc:h2:~/test 으로 되어있으면 jdbc:h2:mem:testdb 으로 변경해서 접속해야 한다.
        
</div>
</details> 
    
<details>
<summary> 컨텐츠수정 모달창에서 태그 셀렉트박스 드랍다운이 뒤쪽에 보이는 문제</summary>
<div markdown="1">
  
   - ElementUI의 Global Config에 옵션 추가하면 해결
     - main.js 파일에 `Vue.us(ElementUI, { zIndex: 9999 });` 옵션 추가(9999 이하면 안됌)
   - 참고
     - [https://element.eleme.io/#/en-US/component/quickstart#global-config](https://element.eleme.io/#/en-US/component/quickstart#global-config)
        
</div>
</details> 

<details>
<summary> HTTP delete Request시 개발자도구의 XHR(XMLHttpRequest )에서 delete요청이 2번씩 찍히는 이유</summary>
<div markdown="1">
  
  - When you try to send a XMLHttpRequest to a different domain than the page is hosted, you are violating the same-origin policy. However, this situation became somewhat common, many technics are introduced. CORS is one of them.

        In short, server that you are sending the DELETE request allows cross domain requests. In the process, there should be a **preflight** call and that is the **HTTP OPTION** call.

        So, you are having two responses for the **OPTION** and **DELETE** call.

        see [MDN page for CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/Access_control_CORS).

    - 출처 : [https://stackoverflow.com/questions/35808655/why-do-i-get-back-2-responses-of-200-and-204-when-using-an-ajax-call-to-delete-o](https://stackoverflow.com/questions/35808655/why-do-i-get-back-2-responses-of-200-and-204-when-using-an-ajax-call-to-delete-o)
        
</div>
</details> 

<details>
<summary> 이미지 파싱 시 og:image 경로가 달라서 제대로 파싱이 안되는 경우</summary>
<div markdown="1">
  
  - UserAgent 설정으로 해결
        - [https://www.javacodeexamples.com/jsoup-set-user-agent-example/760](https://www.javacodeexamples.com/jsoup-set-user-agent-example/760)
        - [http://www.useragentstring.com/](http://www.useragentstring.com/)
        
</div>
</details> 
    
<details>
<summary> 구글 로그인으로 로그인한 사용자의 정보를 가져오는 방법이 스프링 2.0대 버전에서 달라진 것</summary>
<div markdown="1">
  
  - 1.5대 버전에서는 Controller의 인자로 Principal을 넘기면 principal.getName(0에서 바로 꺼내서 쓸 수 있었는데, 2.0대 버전에서는 principal.getName()의 경우 principal 객체.toString()을 반환한다.
    - 1.5대 버전에서 principal을 사용하는 경우
    - 아래와 같이 사용했다면,

    ```jsx
    @RequestMapping("/sso/user")
    @SuppressWarnings("unchecked")
    public Map<String, String> user(Principal principal) {
        if (principal != null) {
            OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) principal;
            Authentication authentication = oAuth2Authentication.getUserAuthentication();
            Map<String, String> details = new LinkedHashMap<>();
            details = (Map<String, String>) authentication.getDetails();
            logger.info("details = " + details);  // id, email, name, link etc.
            Map<String, String> map = new LinkedHashMap<>();
            map.put("email", details.get("email"));
            return map;
        }
        return null;
    }
    ```

    - 2.0대 버전에서는
    - 아래와 같이 principal 객체의 내용을 꺼내 쓸 수 있다.

    ```jsx
    UsernamePasswordAuthenticationToken token =
                    (UsernamePasswordAuthenticationToken) SecurityContextHolder
                            .getContext().getAuthentication();
            Map<String, Object> map = (Map<String, Object>) token.getPrincipal();

            String email = String.valueOf(map.get("email"));
            post.setMember(memberRepository.findByEmail(email));
    ```
        
</div>
</details> 
    
<details>
<summary> 랭킹 동점자 처리 문제</summary>
<div markdown="1">
  
  - PageRequest의 Sort부분에서 properties를 "rankPoint"를 주고 "likeCnt"를 줘서 댓글수보다 좋아요수가 우선순위 갖도록 설정.
  - 좋아요 수도 똑같다면..........
        
</div>
</details> 
    
</br>

## 6. 회고 / 느낀점
>프로젝트 개발 회고 글: https://zuminternet.github.io/ZUM-Pilot-integer/
