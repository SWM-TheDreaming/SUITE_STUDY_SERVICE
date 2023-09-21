package com.suite.suite_study_service.attendance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suite.suite_study_service.attendance.dto.ReqAttendanceCreationDto;
import com.suite.suite_study_service.attendance.dto.ReqAttendanceDto;
import com.suite.suite_study_service.attendance.mockEntity.MockAttendance;
import com.suite.suite_study_service.attendance.repository.AttendanceRepository;
import com.suite.suite_study_service.common.dto.Message;
import com.suite.suite_study_service.common.mockEntity.MockDashBoard;
import com.suite.suite_study_service.dashboard.repository.DashBoardRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
class AttendanceControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired private MockMvc mockMvc;
    @Autowired private DashBoardRepository dashBoardRepository;
    @Autowired private AttendanceRepository attendanceRepository;

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
        MockAttendance mockAttendance = MockAttendance.builder()
                .memberId(1L)
                .suiteRoomId(1L)
                .status(true)
                .round(1)
                .code(456).build();
        attendanceRepository.save(mockAttendance.toAttendance());
    }

    @Test
    @DisplayName("스터디 그룹 출석 생성 - 방장")
    public void createAttendance() throws Exception {
        //given
        ReqAttendanceCreationDto reqAttendanceCreationDto = MockAttendance.getReqAttendanceCreateionDto();
        String body = mapper.writeValueAsString(reqAttendanceCreationDto);
        //when
        String responseBody = postRequest("/study/attendance/registration", YH_JWT, body);
        Message message = mapper.readValue(responseBody, Message.class);
        //then
        Assertions.assertAll(
                ()-> assertThat(message.getStatusCode()).isEqualTo(200)
        );
    }

    @Test
    @DisplayName("스터디 출석 진행 - 스터디원")
    public void registerAttendance() throws Exception {
        //given
        ReqAttendanceDto reqAttendanceDto = MockAttendance.getReqAttendanceDto();
        String body = mapper.writeValueAsString(reqAttendanceDto);
        //when
        String responseBody = postRequest("/study/attendance/", DR_JWT, body);
        Message message = mapper.readValue(responseBody, Message.class);
        //then
        Assertions.assertAll(
                ()-> assertThat(message.getStatusCode()).isEqualTo(200)
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
}