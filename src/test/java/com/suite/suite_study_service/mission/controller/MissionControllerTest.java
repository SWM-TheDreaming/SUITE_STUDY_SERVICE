package com.suite.suite_study_service.mission.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suite.suite_study_service.common.dto.Message;
import com.suite.suite_study_service.common.handler.CustomException;
import com.suite.suite_study_service.common.handler.StatusCode;
import com.suite.suite_study_service.dashboard.entity.DashBoard;
import com.suite.suite_study_service.dashboard.repository.DashBoardRepository;
import com.suite.suite_study_service.mission.dto.MissionType;
import com.suite.suite_study_service.mission.dto.ReqMissionApprovalDto;
import com.suite.suite_study_service.mission.dto.ReqMissionDto;
import com.suite.suite_study_service.mission.dto.ReqMissionListDto;
import com.suite.suite_study_service.mission.entity.Mission;
import com.suite.suite_study_service.common.mockEntity.MockDashBoard;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
        makeMockMissionList("test", "2023-10-15 18:00:00", MissionType.PROGRESS)
                .stream()
                .forEach(mockMission -> {
                    missionRepository.save(mockMission.toMission());
                });
    }

    @Test
    @DisplayName("스터디 그룹 미션 생성 - 방장")
    public void registerMission() throws Exception {
        //given
        missionRepository.deleteAll();

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
    @DisplayName("스터디 그룹 칸반 보드 승인 요청 목록 - 방장")
    public void listUpRequestedMissions() throws Exception {
        //given
        ReqMissionListDto reqMissionListDto = MockMission.getReqMissionListDto("CHECKING");
        String body = mapper.writeValueAsString(reqMissionListDto);
        //when
        String responseBody = postRequest("/study/mission/admin", YH_JWT, body);
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
        ReqMissionApprovalDto reqMissionApprovalDto = MockMission.getReqMissionApprovalDto("test", 1L);
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
        ReqMissionApprovalDto reqMissionApprovalDto = MockMission.getReqMissionApprovalDto("test", 2L);
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
        ReqMissionApprovalDto reqMissionApprovalDto = MockMission.getReqMissionApprovalDto("test", 1L);
        String body = mapper.writeValueAsString(reqMissionApprovalDto);
        updateMockMissionStatus();
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
        ReqMissionApprovalDto reqMissionApprovalDto = MockMission.getReqMissionApprovalDto("test", 2L);
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
                ).andReturn();
                /**
                 * 아래 대로 함수를 진행하면,
                 * 200코드가 아닌 다른 코드를 기대해야하는 테스트 코드가
                 * 기대하는 대로 동작하지 않아서 수정
                 * 확인했으면 주석 삭제 후 진행
                 * */
                //.andExpect(status().isOk()).andReturn();

        return result.getResponse().getContentAsString();
    }

    private List<MockMission> makeMockMissionList(String missionName, String missionDeadLine, MissionType missionType) {
        List<DashBoard> dashBoards = dashBoardRepository.findAllBySuiteRoomId(1L);
        List<MockMission> mockMissionList = new ArrayList<>();
        dashBoards.stream().forEach(dashBoard -> {
            MockMission mockMission = MockMission.builder()
                    .suiteRoomId(1L)
                    .memberId(dashBoard.getMemberId())
                    .missionName(missionName)
                    .missionDeadLine(missionDeadLine)
                    .missionStatus(missionType)
                    .result(false)
                    .build();
            mockMissionList.add(mockMission);

        });
        return mockMissionList;
    }

    private void updateMockMissionStatus() {
        List<DashBoard> dashBoards = dashBoardRepository.findAllBySuiteRoomId(1L);
        dashBoards.stream().forEach(dashBoard -> {
            Mission mission = missionRepository.findBySuiteRoomIdAndMissionNameAndMemberIdAndMissionStatus(1L, "test", dashBoard.getMemberId(), MissionType.PROGRESS)
                    .orElseThrow(()->new CustomException(StatusCode.NOT_FOUND));
            mission.updateMissionStatus(MissionType.CHECKING);
        });
    }

}
