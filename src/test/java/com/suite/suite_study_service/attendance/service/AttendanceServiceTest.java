package com.suite.suite_study_service.attendance.service;

import com.suite.suite_study_service.attendance.dto.GroupOfAttendanceDto;
import com.suite.suite_study_service.attendance.entity.Attendance;
import com.suite.suite_study_service.attendance.mockEntity.MockAttendance;
import com.suite.suite_study_service.attendance.repository.AttendanceRepository;
import com.suite.suite_study_service.common.handler.CustomException;
import com.suite.suite_study_service.common.handler.StatusCode;
import com.suite.suite_study_service.common.mockEntity.MockAuthorizer;
import com.suite.suite_study_service.common.mockEntity.MockDashBoard;
import com.suite.suite_study_service.common.security.dto.AuthorizerDto;
import com.suite.suite_study_service.dashboard.entity.DashBoard;
import com.suite.suite_study_service.dashboard.repository.DashBoardRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
class AttendanceServiceTest {
    @Autowired private DashBoardRepository dashBoardRepository;
    @Autowired private AttendanceRepository attendanceRepository;
    private final MockDashBoard hostMockDashBoard = MockDashBoard.builder()
                                                                .memberId(1L)
                                                                .isHost(true)
                                                                .build();
    private final int ATTENDANCE_CODE = 456;

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
                .code(ATTENDANCE_CODE).build();
        attendanceRepository.save(mockAttendance.toAttendance());
    }

    @Test
    @DisplayName("스터디 출석 생성 - 방장")
    public void createAttendanceHost() {
        //given
        AuthorizerDto attendanceCreationAttempter = MockAuthorizer.YH();
        //when
        DashBoard dashBoard = dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(hostMockDashBoard.toDashBoard().getSuiteRoomId(), attendanceCreationAttempter.getMemberId(), true).orElseThrow(
                () -> assertThrows(CustomException.class, () -> new CustomException(StatusCode.FORBIDDEN)));

        MockAttendance mockAttendance = MockAttendance.builder()
                .memberId(attendanceCreationAttempter.getMemberId())
                .suiteRoomId(dashBoard.getSuiteRoomId())
                .status(true)
                .round(attendanceRepository.filterByGroupBySuiteRoomIdAndRound(dashBoard.getSuiteRoomId()).size() + 1)
                .code(ATTENDANCE_CODE).build();
        Attendance attendance = mockAttendance.toAttendance();
        attendanceRepository.save(attendance);
        //then
        Assertions.assertAll(
                ()-> assertThat(attendance.getRound()).isEqualTo(2),
                ()-> assertThat(attendance.getCode()).isEqualTo(ATTENDANCE_CODE)
        );
    }

    @Test
    @DisplayName("스터디 출석 생성 - 스터디원")
    public void createAttendanceGuest() {
        //given
        AuthorizerDto attendanceCreationAttempter = MockAuthorizer.DH();
        //when
        assertThrows(CustomException.class, () -> {
            dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(1L, attendanceCreationAttempter.getMemberId(), true)
                    .orElseThrow(() -> new CustomException(StatusCode.FORBIDDEN));
        });
    }

    @Test
    @DisplayName("스터디 출석 진행 - 스터디원")
    public void registerAttendanceGuest() {
        //given
        AuthorizerDto attendanceAttempter = MockAuthorizer.DH();
        int inputCode = 456;
        //when
        DashBoard dashBoard = dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(hostMockDashBoard.toDashBoard().getSuiteRoomId(), attendanceAttempter.getMemberId(), false).orElseThrow(
                () -> assertThrows(CustomException.class, () -> new CustomException(StatusCode.FORBIDDEN)));
        List<GroupOfAttendanceDto> groupOfAttendanceDtoList = attendanceRepository.filterByGroupBySuiteRoomIdAndRound(dashBoard.getSuiteRoomId());
        int round = groupOfAttendanceDtoList.size();
        int code = groupOfAttendanceDtoList.get(round-1).getLastInsertedCode();

        attendanceRepository.findBySuiteRoomIdAndMemberIdAndRound(dashBoard.getSuiteRoomId(), attendanceAttempter.getMemberId(), round + 1).ifPresent(
                attendance -> {assertThrows(CustomException.class, () -> new CustomException(StatusCode.ALREADY_ATTENDANCE));});

        if(code != inputCode) assertThrows(CustomException.class, () -> new CustomException(StatusCode.INVALID_ATTENDANCE_CODE));
        compareAttendanceTime(dashBoard.getSuiteRoomId(), 1L, round);
        MockAttendance mockAttendance = MockAttendance.builder()
                .memberId(attendanceAttempter.getMemberId())
                .suiteRoomId(dashBoard.getSuiteRoomId())
                .status(true)
                .round(round)
                .code(inputCode).build();
        Attendance attendance = mockAttendance.toAttendance();
        attendanceRepository.save(attendance);
        //then
        Assertions.assertAll(
                ()-> assertThat(attendance.getMemberId()).isEqualTo(attendanceAttempter.getMemberId())
        );
    }
    private void compareAttendanceTime(Long suiteRoomId, Long hostId, int round) {
        Attendance curAttendance = attendanceRepository.findBySuiteRoomIdAndMemberIdAndRound(suiteRoomId, hostId, round).get();

        Date now = new Date();
        long differenceInMinutes = now.getTime() - curAttendance.getAttendanceTime().getTime() / (60 * 1000);
        if (differenceInMinutes < -5) assertThrows(CustomException.class, () -> new CustomException(StatusCode.TIMEOUT_ATTENDANCE));
    }

}