package com.suite.suite_study_service.mission.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suite.suite_study_service.common.dto.Message;
import com.suite.suite_study_service.common.handler.CustomException;
import com.suite.suite_study_service.common.handler.StatusCode;
import com.suite.suite_study_service.dashboard.entity.DashBoard;
import com.suite.suite_study_service.dashboard.repository.DashBoardRepository;
import com.suite.suite_study_service.mission.dto.*;
import com.suite.suite_study_service.mission.entity.Mission;
import com.suite.suite_study_service.common.mockEntity.MockDashBoard;
import com.suite.suite_study_service.mission.mockEntity.MockMission;
import com.suite.suite_study_service.mission.repository.MissionRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired private EntityManager entityManager;
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
        makeMockMissionList("test", Timestamp.valueOf("2023-10-01 18:00:00"), MissionType.PROGRESS)
                .stream()
                .forEach(mission -> {
                    missionRepository.save(mission);
                });
    }


    @Test
    @DisplayName("스터디 그룹 미션 생성 - 방장")
    public void registerMission() throws Exception {
        //given
        missionRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
        List<Mission> missionList = missionRepository.findAll();
        makeMockMissionList("test2", Timestamp.valueOf("2023-10-01 18:00:00"), MissionType.PROGRESS)
                .stream()
                .forEach(mission -> {
                    missionRepository.save(mission);
                });
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
        Mission missionOne = missionRepository.findAll(PageRequest.of(0,3)).get().collect(Collectors.toList()).get(0);
        ReqMissionApprovalDto reqMissionApprovalDto = MockMission.getReqMissionApprovalDto(missionOne.getMissionId());

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
        Mission missionOne = missionRepository.findAll(PageRequest.of(0,3)).get().collect(Collectors.toList()).get(1);
        ReqMissionApprovalDto reqMissionApprovalDto = MockMission.getReqMissionApprovalDto(missionOne.getMissionId());
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
        Mission missionOne = missionRepository.findAll(PageRequest.of(0,3)).get().collect(Collectors.toList()).get(0);
        ReqMissionApprovalDto reqMissionApprovalDto = MockMission.getReqMissionApprovalDto(missionOne.getMissionId());
        String body = mapper.writeValueAsString(reqMissionApprovalDto);
        updateMockMissionStatus(missionOne.getMissionId());
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
        ReqMissionApprovalDto reqMissionApprovalDto = MockMission.getReqMissionApprovalDto( 2L);
        String body = mapper.writeValueAsString(reqMissionApprovalDto);
        //when
        String responseBody = postRequest("/study/mission/approval", DR_JWT, body);
        Message message = mapper.readValue(responseBody, Message.class);

        //then
        Assertions.assertAll(
                () -> assertThat(message.getStatusCode()).isEqualTo(403)
        );
    }

    @Test
    @DisplayName("스터디 그룹 미션 삭제 - 방장")
    public void removeRequestMissionHost() throws Exception {
        //given
        ReqDeleteMissionDto reqDeleteMissionDto = MockMission.getReqDeleteMissionDto(1L, "test");
        String body = mapper.writeValueAsString(reqDeleteMissionDto);
        //when
        String responseBody = postRequest("/study/mission/delete", YH_JWT, body);
        Message message = mapper.readValue(responseBody, Message.class);

        //then
        Assertions.assertAll(
                () -> assertThat(message.getStatusCode()).isEqualTo(200)
        );
    }
    @Test
    @DisplayName("스터디 그룹 미션 삭제 - 스터디원")
    public void removeRequestMissionGuest() throws Exception {
        //given
        ReqDeleteMissionDto reqDeleteMissionDto = MockMission.getReqDeleteMissionDto(1L, "test");
        String body = mapper.writeValueAsString(reqDeleteMissionDto);
        //when
        String responseBody = postRequest("/study/mission/delete", DR_JWT, body);
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


        return result.getResponse().getContentAsString();
    }

    private List<Mission> makeMockMissionList(String missionName, Timestamp missionDeadLine, MissionType missionType) {
        List<DashBoard> dashBoards = dashBoardRepository.findAllBySuiteRoomId(1L);
        List<Mission> mockMissionList = new ArrayList<>();

        long idCounter = 1;
        for (DashBoard dashBoard : dashBoards) {
            Mission newMission = MockMission.newMission(idCounter, dashBoard.getMemberId(),missionType, missionName, missionDeadLine);
            mockMissionList.add(newMission);
            idCounter++;
        }

        return mockMissionList;
    }

    private void updateMockMissionStatus(Long missionId) {
//        List<DashBoard> dashBoards = dashBoardRepository.findAllBySuiteRoomId(1L);
        Mission mission = missionRepository.findByMissionIdAndMissionStatus(missionId, MissionType.PROGRESS).get();
        mission.updateMissionStatus(MissionType.CHECKING);

//        dashBoards.stream().forEach(dashBoard -> {
//            Mission mission = missionRepository.findByMissionIdAndMissionStatus(dashBoard.getDashboardId(), MissionType.PROGRESS)
//                    .orElseThrow(()->new CustomException(StatusCode.NOT_FOUND));
//            mission.updateMissionStatus(MissionType.CHECKING);
//        });
    }

}
