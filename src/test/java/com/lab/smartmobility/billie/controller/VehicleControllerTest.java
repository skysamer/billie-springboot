/*
package com.lab.smartmobility.billie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.smartmobility.billie.config.JwtTokenProvider;
import com.lab.smartmobility.billie.dto.vehicle.VehicleDTO;
import com.lab.smartmobility.billie.entity.Vehicle;
import com.lab.smartmobility.billie.repository.vehicle.VehicleRepository;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VehicleControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired JwtTokenProvider tokenProvider;
    @Autowired VehicleRepository vehicleRepository;

    @Autowired
    private WebApplicationContext ctx;

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
    @DisplayName("전체 보유 차량 조회 테스트")
    void getPossessVehicleList() throws Exception {
        List<Vehicle> possessVehicleList = vehicleRepository.findAll();
        List<VehicleDTO> returnVehicle=new ArrayList<>();

        for(Vehicle vehicle : possessVehicleList){
            String[] vehicleName=vehicle.getVehicleName().split(" ");

            VehicleDTO vehicleDTO=VehicleDTO.builder()
                    .vehicleNum(vehicle.getVehicleNum())
                    .name(vehicleName[0])
                    .number(vehicleName[1]+" "+vehicleName[2])
                    .parkingLoc(vehicle.getParkingLoc())
                    .rentalStatus(vehicle.getRentalStatus())
                    .build();
            returnVehicle.add(vehicleDTO);
        }

        MvcResult result = mockMvc.perform(get("/vehicle/to-own")
                        .header(tokenKey, adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(returnVehicle)))
                .andDo(print())
                .andReturn();
    }
}
*/
