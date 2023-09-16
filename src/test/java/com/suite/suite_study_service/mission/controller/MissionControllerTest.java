package com.suite.suite_study_service.mission.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suite.suite_study_service.common.dto.Message;
import com.suite.suite_study_service.dashboard.repository.DashBoardRepository;
import com.suite.suite_study_service.mission.dto.ReqMissionApprovalDto;
import com.suite.suite_study_service.mission.dto.ReqMissionDto;
import com.suite.suite_study_service.mission.dto.ReqMissionListDto;
import com.suite.suite_study_service.mission.mockEntity.MockDashBoard;
import com.suite.suite_study_service.mission.mockEntity.MockMission;
import com.suite.suite_study_service.mission.repository.MissionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class MissionControllerTest {

    @Autowired private ObjectMapper mapper;
    @Autowired private MockMvc mockMvc;
    @Autowired private MissionRepository missionRepository;
    @Autowired private DashBoardRepository dashBoardRepository;

    @Value("${token.YH}")
    private String YH_JWT;
    @Value("${token.DR}")
    private String DR_JWT;
    @Value("${token.JH}")
    private String JH_JWT;

    private final MockDashBoard hostMockDashBoard = MockDashBoard.builder()
            .memberId(1L)
            .isHost(true)
            .build();
    @BeforeEach
    public void setUp() {
        dashBoardRepository.save(hostMockDashBoard.toDashBoard());
        for(Long i = 2L; i < 4L; i++) {
            MockDashBoard guestDashBoard = MockDashBoard.builder()
                    .memberId(i)
                    .isHost(false)
                    .build();
            dashBoardRepository.save(guestDashBoard.toDashBoard());
        }
    }

    @Test
    @DisplayName("스터디 그룹 미션 생성 - 방장")
    public void registerMission() throws Exception {
        //given
        ReqMissionDto reqMissionDto = MockMission.getReqMissionDto();
        String body = mapper.writeValueAsString(reqMissionDto);
        //when
        String responseBody = postRequest("/study/mission/registration", YH_JWT, body);
        Message message = mapper.readValue(responseBody, Message.class);
        //then
        Assertions.assertAll(
                () -> assertThat(message.getStatusCode()).isEqualTo(200)
        );
    }

    @Test
    @DisplayName("스터디 그룹 미션 목록 - PROGRESS")
    public void listUpMissionsProgress() throws Exception {
        //given
        ReqMissionListDto reqMissionListDto = MockMission.getReqMissionListDto("PROGRESS");
        String body = mapper.writeValueAsString(reqMissionListDto);
        //when
        String responseBody = postRequest("/study/mission", YH_JWT, body);
        Message message = mapper.readValue(responseBody, Message.class);
        //then
        Assertions.assertAll(
                () -> assertThat(message.getStatusCode()).isEqualTo(200)
        );
    }

    @Test
    @DisplayName("스터디 그룹 미션 목록 - CHECKING")
    public void listUpMissionsChecking() throws Exception {
        //given
        ReqMissionListDto reqMissionListDto = MockMission.getReqMissionListDto("CHECKING");
        String body = mapper.writeValueAsString(reqMissionListDto);
        //when
        String responseBody = postRequest("/study/mission", YH_JWT, body);
        Message message = mapper.readValue(responseBody, Message.class);
        //then
        Assertions.assertAll(
                () -> assertThat(message.getStatusCode()).isEqualTo(200)
        );
    }

    @Test
    @DisplayName("스터디 그룹 미션 목록 - COMPLETE")
    public void listUpMissionsComplete() throws Exception {
        //given
        ReqMissionListDto reqMissionListDto = MockMission.getReqMissionListDto("COMPLETE");
        String body = mapper.writeValueAsString(reqMissionListDto);
        //when
        String responseBody = postRequest("/study/mission", YH_JWT, body);
        Message message = mapper.readValue(responseBody, Message.class);
        //then
        Assertions.assertAll(
                () -> assertThat(message.getStatusCode()).isEqualTo(200)
        );
    }

    @Test
    @DisplayName("스터디 그룹 미션 달성 요청 - 방장")
    public void requestApprovalMissionHost() throws Exception {
        //given
        ReqMissionApprovalDto reqMissionApprovalDto = MockMission.getReqMissionApprovalDto("test");
        String body = mapper.writeValueAsString(reqMissionApprovalDto);
        //when
        String responseBody = postRequest("/study/mission/submission", YH_JWT, body);
        Message message = mapper.readValue(responseBody, Message.class);
        //then
        Assertions.assertAll(
                () -> assertThat(message.getStatusCode()).isEqualTo(200)
        );
    }

    @Test
    @DisplayName("스터디 그룹 미션 달성 요청 - 스터디원")
    public void requestApprovalMissionGuest() throws Exception {
        //given
        ReqMissionApprovalDto reqMissionApprovalDto = MockMission.getReqMissionApprovalDto("test");
        String body = mapper.writeValueAsString(reqMissionApprovalDto);
        //when
        String responseBody = postRequest("/study/mission/submission", DR_JWT, body);
        Message message = mapper.readValue(responseBody, Message.class);
        //then
        Assertions.assertAll(
                () -> assertThat(message.getStatusCode()).isEqualTo(200)
        );
    }

    @Test
    @DisplayName("스터디 그룹 미션 달성 승인 - 방장")
    public void approvalRequestAcceptMissionHost() throws Exception {
        //given
        ReqMissionApprovalDto reqMissionApprovalDto = MockMission.getReqMissionApprovalDto("test");
        String body = mapper.writeValueAsString(reqMissionApprovalDto);
        //when
        String responseBody = postRequest("/study/mission/approval", YH_JWT, body);
        Message message = mapper.readValue(responseBody, Message.class);
        //then
        Assertions.assertAll(
                () -> assertThat(message.getStatusCode()).isEqualTo(200)
        );
    }

    @Test
    @DisplayName("스터디 그룹 미션 달성 승인 - 스터디원")
    public void approvalRequestAcceptMissionGuest() throws Exception {
        //given
        ReqMissionApprovalDto reqMissionApprovalDto = MockMission.getReqMissionApprovalDto("test");
        String body = mapper.writeValueAsString(reqMissionApprovalDto);
        //when
        String responseBody = postRequest("/study/mission/approval", DR_JWT, body);
        Message message = mapper.readValue(responseBody, Message.class);
        //then
        Assertions.assertAll(
                () -> assertThat(message.getStatusCode()).isEqualTo(403)
        );
    }

    private String postRequest(String url, String jwt, String body) throws Exception {
        MvcResult result = mockMvc.perform(post(url)
                        .content(body) //HTTP body에 담는다.
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk()).andReturn();

        return result.getResponse().getContentAsString();
    }

}
