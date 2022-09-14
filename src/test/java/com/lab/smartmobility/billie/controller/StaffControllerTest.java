package com.lab.smartmobility.billie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.smartmobility.billie.global.config.JwtTokenProvider;
import com.lab.smartmobility.billie.staff.dto.DepartmentDTO;
import com.lab.smartmobility.billie.staff.dto.EmailForm;
import com.lab.smartmobility.billie.staff.dto.RankDTO;
import com.lab.smartmobility.billie.staff.repository.StaffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StaffControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtTokenProvider tokenProvider;
    @Autowired
    StaffRepository staffRepository;
    @Autowired
    WebApplicationContext ctx;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
    }

    String userToken;
    @BeforeEach
    void createUserToken(){
        this.userToken = tokenProvider.createTokenLogin("smtkdals94@gmail.com", "ROLE_USER");
    }

    String adminToken;
    @BeforeEach
    void createAdminToken(){
        this.adminToken = tokenProvider.createTokenLogin("smnyj7@gmail.com", "ROLE_ADMIN");
    }

    static final String tokenKey = "X-AUTH-TOKEN";

    @Test
    @DisplayName("이메일 토큰 전송 테스트")
    void sendEmail() throws Exception {
        String email = "smtkdals94@gmail.com";
        EmailForm emailForm = new EmailForm(email);

        mockMvc.perform(post("/send-email-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailForm)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("부서 목록 조회 테스트")
    void getDepartmentList() throws Exception {
        MvcResult result = mockMvc.perform(get("/department")
                        .header(tokenKey, adminToken)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        List<DepartmentDTO> dropdown = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), DepartmentDTO[].class));
        assertThat(dropdown.get(0).getDepartment()).isNotNull();
        assertThat(dropdown.size()).isEqualTo(4);
    }

    @Test
    @DisplayName("직급 목록 조회 테스트")
    void getRankList() throws Exception {
        MvcResult result = mockMvc.perform(get("/rank")
                        .header(tokenKey, adminToken)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        List<RankDTO> dropdown = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), RankDTO[].class));
        assertThat(dropdown.get(0).getRank()).isNotNull();
        assertThat(dropdown.size()).isEqualTo(10);
    }
}
