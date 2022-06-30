/*
package com.lab.smartmobility.billie.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.smartmobility.billie.config.JwtTokenProvider;
import com.lab.smartmobility.billie.dto.staff.EmailTokenForm;
import com.lab.smartmobility.billie.dto.staff.LoginForm;
import com.lab.smartmobility.billie.dto.staff.SignUpForm;
import com.lab.smartmobility.billie.entity.HttpMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StaffTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired JwtTokenProvider tokenProvider;

    String token;
    @BeforeEach
    void createUserToken(){
        this.token = tokenProvider.createTokenLogin("smtkdals94@gmail.com", "ROLE_USER");
    }

    @DisplayName("이메일 인증 토큰 검증 - 실패")
    @Test
    void verifyEmailTokenTest_fail() throws Exception {
        EmailTokenForm emailTokenForm=new EmailTokenForm("smtkdals94@gmail.com", "0e6cacdc-4340-4a0e-9a68-1f2d077c23f");

        MvcResult result=mockMvc.perform(post("/verify-email-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailTokenForm)))
                .andExpect(status().isOk())
                .andReturn();
        HttpMessage response= (HttpMessage) result.getAsyncResult();
    }

    @DisplayName("이메일 인증 토큰 검증 - 성공")
    @Test
    void verifyEmailTokenTest_success() throws Exception {
        EmailTokenForm emailTokenForm=new EmailTokenForm("smtkdals94@gmail.com", "0e6cacdc-4340-4a0e-9a68-1f2d077c23f7");

        MvcResult result=mockMvc.perform(post("/verify-email-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailTokenForm)))
                .andExpect(status().isOk())
                .andReturn();
        String response=result.getResponse().getContentAsString();
        System.out.println(response);
    }

    @DisplayName("회원가입 - 성공")
    @Test
    void signUp_success() throws Exception {
        SignUpForm signUpForm=new SignUpForm("smtkdals94@gmail.com", "sm7415987");

        MvcResult result=mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpForm)))
                .andExpect(status().isOk())
                .andReturn();
        String response=result.getResponse().getContentAsString();
        System.out.println(response);
    }

    @DisplayName("회원가입 - 실패")
    @Test
    void signUp_fail() throws Exception {
        SignUpForm signUpForm=new SignUpForm("smtkdals94@gmail.com", "sm7415987");

        MvcResult result=mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpForm)))
                .andExpect(status().isOk())
                .andReturn();
        String response=result.getResponse().getContentAsString();
        System.out.println(response);
    }
}

*/
