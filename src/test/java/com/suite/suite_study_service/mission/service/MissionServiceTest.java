package com.suite.suite_study_service.mission.service;

import com.suite.suite_study_service.common.handler.CustomException;
import com.suite.suite_study_service.common.handler.StatusCode;
import com.suite.suite_study_service.common.security.dto.AuthorizerDto;

import com.suite.suite_study_service.dashboard.entity.DashBoard;
import com.suite.suite_study_service.dashboard.repository.DashBoardRepository;
import com.suite.suite_study_service.mission.entity.Mission;
import com.suite.suite_study_service.mission.mockEntity.MockAuthorizer;
import com.suite.suite_study_service.mission.mockEntity.MockDashBoard;

import com.suite.suite_study_service.mission.mockEntity.MockMission;
import com.suite.suite_study_service.mission.repository.MissionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@DataJpaTest
public class MissionServiceTest {
    @Autowired private DashBoardRepository dashBoardRepository;
    @Autowired private MissionRepository missionRepository;


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
    @DisplayName("스터디 그룹 미션 생성 - 방장 O")
    public void createSuiteRoomHost() {
        //given
        AuthorizerDto missionCreationAttempter = MockAuthorizer.YH();
        //when
        DashBoard dashBoard = dashBoardRepository.findByMemberId(missionCreationAttempter.getMemberId()).orElseThrow(
                () -> {
                    return assertThrows(CustomException.class, () -> {
                        throw new CustomException(StatusCode.IS_NOT_PARTICIPATED);
                    });
                }
        );

        if(!dashBoard.isHost()) {
            assertThrows(CustomException.class, () -> {
                throw new CustomException(StatusCode.FORBIDDEN);
            });
        }

        makeMockMissionList("test", "2023-10-25 18:00:00")
                .stream()
                .forEach(mockMission -> {
                            missionRepository.save(mockMission.toMission());
                        });
        List<Mission> assertionMissions = missionRepository.findAllBySuiteRoomId(1L);
        //then
        Assertions.assertAll(
                ()-> assertThat(assertionMissions.get(0).getClass()).isEqualTo(Mission.class),
                ()-> assertThat(assertionMissions.size()).isEqualTo(3),
                ()-> assertThat(assertionMissions.get(0).getMissionStatus()).isEqualTo("PROGRESS"),
                ()-> assertThat(assertionMissions.get(0).isResult()).isEqualTo(false)
        );
    }

    @Test
    @DisplayName("스터디 그룹 미션 생성 - 방장 X")
    public void createSuiteRoomGuest() {
        //given
        AuthorizerDto missionCreationAttempter = MockAuthorizer.DH();
        //when
        dashBoardRepository.findByMemberId(missionCreationAttempter.getMemberId()).orElseThrow(
                () -> {
                    //then
                    return assertThrows(CustomException.class, () -> {
                        throw new CustomException(StatusCode.IS_NOT_PARTICIPATED);
                    });
                }
        );

    }

    private List<MockMission> makeMockMissionList(String missionName, String missionDeadLine) {
        List<DashBoard> dashBoards = dashBoardRepository.findAllBySuiteRoomId(1L);
        List<MockMission> mockMissionList = new ArrayList<>();
        dashBoards.stream().forEach(dashBoard -> {
            MockMission mockMission = MockMission.builder()
                    .suiteRoomId(1L)
                    .memberId(dashBoard.getMemberId())
                    .missionName(missionName)
                    .missionDeadLine(missionDeadLine)
                    .missionStatus("PROGRESS")
                    .result(false)
                    .build();
            mockMissionList.add(mockMission);

        });
        return mockMissionList;
    }




}