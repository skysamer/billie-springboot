/*
package com.lab.smartmobility.billie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.smartmobility.billie.config.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VehicleControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtTokenProvider tokenProvider;

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

    @Test
    @DisplayName("전체 보유 차량 조회 테스트")
    void getPossessVehicleList(){
    }
}*/
