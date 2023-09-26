package com.suite.suite_study_service.mission.service;

import com.suite.suite_study_service.common.handler.CustomException;
import com.suite.suite_study_service.common.handler.StatusCode;
import com.suite.suite_study_service.common.security.dto.AuthorizerDto;

import com.suite.suite_study_service.dashboard.entity.DashBoard;
import com.suite.suite_study_service.dashboard.repository.DashBoardRepository;
import com.suite.suite_study_service.mission.dto.MissionType;
import com.suite.suite_study_service.mission.entity.Mission;
import com.suite.suite_study_service.common.mockEntity.MockAuthorizer;
import com.suite.suite_study_service.common.mockEntity.MockDashBoard;

import com.suite.suite_study_service.mission.mockEntity.MockMission;
import com.suite.suite_study_service.mission.repository.MissionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@SpringBootTest
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
    @DisplayName("스터디 그룹 미션 생성 - 방장")
    public void createMissionHost() {
        //given
        AuthorizerDto missionCreationAttempter = MockAuthorizer.YH();
        //when
        dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(1L, missionCreationAttempter.getMemberId(), true).orElseThrow(
                () -> assertThrows(CustomException.class, () -> new CustomException(StatusCode.FORBIDDEN)));

        missionRepository.findBySuiteRoomIdAndMissionNameAndMemberId(1L, "test",missionCreationAttempter.getMemberId()).ifPresent(result -> {
                    assertThrows(CustomException.class, () -> {
                        throw new CustomException(StatusCode.ALREADY_EXISTS_MISSION);
                    });
                });

        makeMockMissionList("test", "2023-10-15 18:00:00", MissionType.PROGRESS)
                .stream()
                .forEach(mockMission -> {
                            missionRepository.save(mockMission.toMission());
                        });
        List<Mission> assertionMissions = missionRepository.findAllBySuiteRoomId(1L);
        //then
        Assertions.assertAll(
                ()-> assertThat(assertionMissions.get(0).getClass()).isEqualTo(Mission.class),
                ()-> assertThat(assertionMissions.size()).isEqualTo(3),
                ()-> assertThat(assertionMissions.get(0).getMissionStatus()).isEqualTo(MissionType.PROGRESS),
                ()-> assertThat(assertionMissions.get(0).isResult()).isEqualTo(false)
        );
    }

    @Test
    @DisplayName("스터디 그룹 미션 생성 - 스터디원")
    public void createMissionGuest() {
        //given
        AuthorizerDto missionCreationAttempter = MockAuthorizer.DH();
        //when
        assertThrows(CustomException.class, () -> {
            dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(1L, missionCreationAttempter.getMemberId(), true)
                    .orElseThrow(() -> new CustomException(StatusCode.FORBIDDEN));
        });

    }

    @Test
    @DisplayName("스터디 그룹 미션 목록 - 진행중")
    public void getMissionsProgress() {
        //given
        makeMockMissionList("test", "2023-10-15 18:00:00", MissionType.PROGRESS)
                .stream()
                .forEach(mockMission -> {
                    missionRepository.save(mockMission.toMission());
                });
        AuthorizerDto missionReadAttempter = MockAuthorizer.YH();
        //when
        List<Mission> assertionMissions = missionRepository.findAllBySuiteRoomIdAndMissionStatusAndMemberId(1L, MissionType.PROGRESS, missionReadAttempter.getMemberId());
        //then
        Assertions.assertAll(
                ()-> assertThat(assertionMissions.get(0).getClass()).isEqualTo(Mission.class),
                ()-> assertThat(assertionMissions.size()).isEqualTo(1),
                ()-> assertThat(assertionMissions.get(0).getMemberId()).isEqualTo(missionReadAttempter.getMemberId()),
                ()-> assertThat(assertionMissions.get(0).getMissionStatus()).isEqualTo(MissionType.PROGRESS),
                ()-> assertThat(assertionMissions.get(0).isResult()).isEqualTo(false)
        );

    }

    @Test
    @DisplayName("스터디 그룹 미션 목록 - 확인")
    public void getMissionsCheck() {
        //given
        makeMockMissionList("test", "2023-10-15 18:00:00", MissionType.CHECKING)
                .stream()
                .forEach(mockMission -> {
                    missionRepository.save(mockMission.toMission());
                });
        AuthorizerDto missionReadAttempter = MockAuthorizer.YH();
        //when
        List<Mission> assertionMissions = missionRepository.findAllBySuiteRoomIdAndMissionStatusAndMemberId(1L, MissionType.CHECKING, missionReadAttempter.getMemberId());
        //then
        Assertions.assertAll(
                ()-> assertThat(assertionMissions.get(0).getClass()).isEqualTo(Mission.class),
                ()-> assertThat(assertionMissions.size()).isEqualTo(1),
                ()-> assertThat(assertionMissions.get(0).getMemberId()).isEqualTo(missionReadAttempter.getMemberId()),
                ()-> assertThat(assertionMissions.get(0).getMissionStatus()).isEqualTo(MissionType.CHECKING),
                ()-> assertThat(assertionMissions.get(0).isResult()).isEqualTo(false)
        );
    }

    @Test
    @DisplayName("스터디 그룹 미션 목록 - 완료")
    public void getMissionsComplete() {
        //given
        makeMockMissionList("test", "2023-10-15 18:00:00", MissionType.COMPLETE)
                .stream()
                .forEach(mockMission -> {
                    missionRepository.save(mockMission.toMission());
                });
        AuthorizerDto missionReadAttempter = MockAuthorizer.YH();
        //when
        List<Mission> assertionMissions = missionRepository.findAllBySuiteRoomIdAndMissionStatusAndMemberId(1L, MissionType.COMPLETE, missionReadAttempter.getMemberId());
        //then
        Assertions.assertAll(
                ()-> assertThat(assertionMissions.get(0).getClass()).isEqualTo(Mission.class),
                ()-> assertThat(assertionMissions.size()).isEqualTo(1),
                ()-> assertThat(assertionMissions.get(0).getMemberId()).isEqualTo(missionReadAttempter.getMemberId()),
                ()-> assertThat(assertionMissions.get(0).getMissionStatus()).isEqualTo(MissionType.COMPLETE),
                ()-> assertThat(assertionMissions.get(0).isResult()).isEqualTo(false)
        );
    }

    @Test
    @DisplayName("스터디 그룹 칸반 보드 관리 - 방장")
    public void getRequestedMissions() {
        //given
        makeMockMissionList("test", "2023-10-15 18:00:00", MissionType.CHECKING)
                .stream()
                .forEach(mockMission -> {
                    missionRepository.save(mockMission.toMission());
                });
        AuthorizerDto missionReadAttempter = MockAuthorizer.YH();
        //when
        dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(1L, missionReadAttempter.getMemberId(), true).orElseThrow(
                () -> assertThrows(CustomException.class, () -> new CustomException(StatusCode.FORBIDDEN)));

        List<Mission> assertionMissions = missionRepository.findAllBySuiteRoomIdAndMissionStatus(1L, MissionType.CHECKING);
        //then
        Assertions.assertAll(
                ()-> assertThat(assertionMissions.get(0).getClass()).isEqualTo(Mission.class),
                ()-> assertThat(assertionMissions.get(0).getMissionStatus()).isEqualTo(MissionType.CHECKING),
                ()-> assertThat(assertionMissions.get(0).isResult()).isEqualTo(false)
        );
    }

    @Test
    @DisplayName("스터디 그룹 미션 목록 - 기간 만료")
    public void timeOutMissions() {
        //given
        AuthorizerDto missionReadAttempter = MockAuthorizer.YH();
        makeMockMissionList("test", "2023-09-14 18:00:00", MissionType.PROGRESS)
                .stream()
                .forEach(mockMission -> {
                    missionRepository.save(mockMission.toMission());
                });
        //when
        Timestamp now = new Timestamp(System.currentTimeMillis());
        List<Mission> assertionMissions = missionRepository.findAllBySuiteRoomIdAndMissionStatusAndMemberId(1L, MissionType.valueOf("PROGRESS"), missionReadAttempter.getMemberId())
                .stream()
                .filter(mission -> {

                    if (mission.getMissionDeadLine().getTime() - now.getTime() < 0) {
                        mission.updateMissionStatus(MissionType.COMPLETE);
                        return false;
                    }
                    return true;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        //then
        Assertions.assertAll(
                ()-> assertThat(assertionMissions.size()).isEqualTo(0)
        );
    }

    @Test
    @DisplayName("스터디 그룹 미션 달성 요청 - 스터디원")
    public void updateMissionStatusProgressToCheckingGuest() {
        //given
        AuthorizerDto missionApprovalRequester = MockAuthorizer.DH();
        makeMockMissionList("test", "2023-10-15 18:00:00", MissionType.PROGRESS)
                .stream()
                .forEach(mockMission -> {
                    missionRepository.save(mockMission.toMission());
                });
        //when
        Mission assertionMission = missionRepository.findBySuiteRoomIdAndMissionNameAndMemberIdAndMissionStatus(1L, "test", missionApprovalRequester.getMemberId(), MissionType.PROGRESS)
                .orElseThrow(() -> assertThrows(CustomException.class, () -> new CustomException(StatusCode.NOT_FOUND)));

        assertionMission.updateMissionStatus(MissionType.CHECKING);

        //then
        Assertions.assertAll(
                ()-> assertThat(assertionMission.getMissionStatus()).isEqualTo(MissionType.CHECKING),
                ()-> assertThat(assertionMission.getClass()).isEqualTo(Mission.class)
        );
    }

    @Test
    @DisplayName("스터디 그룹 미션 달성 요청 - 방장")
    public void updateMissionStatusProgressToCheckingHost() {
        //given
        AuthorizerDto missionApprovalRequester = MockAuthorizer.YH();
        makeMockMissionList("test", "2023-10-15 18:00:00", MissionType.PROGRESS)
                .stream()
                .forEach(mockMission -> {
                    missionRepository.save(mockMission.toMission());
                });
        //when
        Boolean isHost = dashBoardRepository.findBySuiteRoomIdAndMemberId(1L, missionApprovalRequester.getMemberId()).get().isHost();

        Mission assertionMission = missionRepository.findBySuiteRoomIdAndMissionNameAndMemberIdAndMissionStatus(1L, "test", missionApprovalRequester.getMemberId(), MissionType.PROGRESS)
                .orElseThrow(() -> assertThrows(CustomException.class, () -> new CustomException(StatusCode.NOT_FOUND)));

        if (isHost) assertionMission.updateMissionStatus(MissionType.COMPLETE);
        //then
        Assertions.assertAll(
                ()-> assertThat(assertionMission.getMissionStatus()).isEqualTo(MissionType.COMPLETE),
                ()-> assertThat(assertionMission.getClass()).isEqualTo(Mission.class)
        );
    }

    @Test
    @DisplayName("스터디 그룹 미션 달성 요청 승인 - 방장")
    public void updateMissionStatusCheckingToCompleteHost() {
        //given
        AuthorizerDto missionApprovalRequester = MockAuthorizer.YH();
        makeMockMissionList("test", "2023-10-15 18:00:00", MissionType.CHECKING)
                .stream()
                .forEach(mockMission -> {
                    missionRepository.save(mockMission.toMission());
                });
        //when
        dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(1L, missionApprovalRequester.getMemberId(), true).orElseThrow(
                () -> assertThrows(CustomException.class, () -> new CustomException(StatusCode.FORBIDDEN)));

        Mission assertionMission = missionRepository.findBySuiteRoomIdAndMissionNameAndMemberIdAndMissionStatus(1L, "test", missionApprovalRequester.getMemberId(), MissionType.CHECKING)
                .orElseThrow(() -> assertThrows(CustomException.class, () -> new CustomException(StatusCode.NOT_FOUND)));

        assertionMission.updateMissionStatusAndResult();
        //then
        Assertions.assertAll(
                ()-> assertThat(assertionMission.getMissionStatus()).isEqualTo(MissionType.COMPLETE),
                ()-> assertThat(assertionMission.isResult()).isEqualTo(true),
                ()-> assertThat(assertionMission.getClass()).isEqualTo(Mission.class)
        );
    }

    @Test
    @DisplayName("스터디 그룹 미션 달성 요청 승인 - 스터디원")
    public void updateMissionStatusCheckingToCompleteGuest() {
        //given
        AuthorizerDto missionApprovalRequester = MockAuthorizer.DH();
        makeMockMissionList("test", "2023-10-15 18:00:00", MissionType.PROGRESS)
                .stream()
                .forEach(mockMission -> {
                    missionRepository.save(mockMission.toMission());
                });
        //when

        assertThrows(CustomException.class, () -> {
            dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(1L, missionApprovalRequester.getMemberId(), true)
                    .orElseThrow(() -> new CustomException(StatusCode.FORBIDDEN));
        });

    }

    @Test
    @DisplayName("스터디 미션 삭제 - 방장")
    public void deleteMissionHost() {
        //given
        AuthorizerDto missionApprovalRequester = MockAuthorizer.YH();
        makeMockMissionList("test", "2023-10-15 18:00:00", MissionType.PROGRESS)
                .stream()
                .forEach(mockMission -> {
                    missionRepository.save(mockMission.toMission());
                });
        //when
        dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(1L, missionApprovalRequester.getMemberId(), true).orElseThrow(
                () -> assertThrows(CustomException.class, () -> new CustomException(StatusCode.FORBIDDEN)));

        List<Mission> missionList = missionRepository.findBySuiteRoomIdAndMissionNameAndMissionStatus(1L, "test", MissionType.PROGRESS);
        List<Mission> countMissions = missionRepository.findAllBySuiteRoomIdAndMissionName(1L, "test");
        if (missionList.size() == countMissions.size()) {
            missionList.stream()
                    .forEach(mission -> missionRepository.delete(mission));
        } else {
            assertThrows(CustomException.class, () -> new CustomException(StatusCode.ALREADY_EXISTS_MISSION_REQUEST));
        }


        List<Mission> assertionMission = missionRepository.findBySuiteRoomIdAndMissionNameAndMissionStatus(1L, "test", MissionType.PROGRESS);
        //then
        Assertions.assertAll(
                ()-> assertThat(assertionMission.size()).isEqualTo(0)
        );

    }

    @Test
    @DisplayName("스터디 미션 삭제 - 스터디원")
    public void deleteMissionGuest() {
        //given
        AuthorizerDto missionApprovalRequester = MockAuthorizer.DH();
        makeMockMissionList("test", "2023-10-15 18:00:00", MissionType.PROGRESS)
                .stream()
                .forEach(mockMission -> {
                    missionRepository.save(mockMission.toMission());
                });
        //when
        //then
        assertThrows(CustomException.class, () -> {
            dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(1L, missionApprovalRequester.getMemberId(), true)
                    .orElseThrow(() -> new CustomException(StatusCode.FORBIDDEN));
        });
    }

    @Test
    @DisplayName("스터디 승인 요청 취소 - 방장")
    public void updateMissionStatusCheckingToProgressHost() {
        //given
        AuthorizerDto missionCancleRequester = MockAuthorizer.YH();
        //when
        //then
        assertThrows(CustomException.class, () -> {
            dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(1L, missionCancleRequester.getMemberId(), false)
                    .orElseThrow(() -> new CustomException(StatusCode.FORBIDDEN));
        });
    }

    @Test
    @DisplayName("스터디 승인 요청 취소 / 반려 - 스터디원 / 방장")
    public void updateMissionStatusCheckingToProgress() {
        //given
        AuthorizerDto missionCancleRequester = MockAuthorizer.YH();
        makeMockMissionList("test", "2023-10-15 18:00:00", MissionType.CHECKING)
                .stream()
                .forEach(mockMission -> {
                    missionRepository.save(mockMission.toMission());
                });
        //when
        Mission cancleRequiredMission = missionRepository.findBySuiteRoomIdAndMissionNameAndMemberIdAndMissionStatus(1L, "test", 1L, MissionType.CHECKING)
                .orElseThrow(() -> assertThrows(CustomException.class, () -> new CustomException(StatusCode.NOT_FOUND)));

        cancleRequiredMission.updateMissionStatus(MissionType.PROGRESS);
        //then
        Assertions.assertAll(
                ()-> assertThat(cancleRequiredMission.getMissionStatus()).isEqualTo(MissionType.PROGRESS),
                ()-> assertThat(cancleRequiredMission.getMissionName()).isEqualTo("test"),
                ()-> assertThat(cancleRequiredMission.getMemberId()).isEqualTo(1L)
        );

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




}
