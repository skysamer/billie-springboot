package com.lab.smartmobility.billie.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.smartmobility.billie.config.JwtTokenProvider;
import com.lab.smartmobility.billie.dto.staff.EmailForm;
import com.lab.smartmobility.billie.entity.Staff;
import com.lab.smartmobility.billie.repository.StaffRepository;
import com.lab.smartmobility.billie.repository.vehicle.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

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
}