package com.suite.suite_study_service.mission.service;

import com.suite.suite_study_service.common.handler.CustomException;
import com.suite.suite_study_service.common.handler.StatusCode;
import com.suite.suite_study_service.common.security.dto.AuthorizerDto;

import com.suite.suite_study_service.dashboard.entity.DashBoard;
import com.suite.suite_study_service.dashboard.repository.DashBoardRepository;
import com.suite.suite_study_service.mission.dto.MissionType;
import com.suite.suite_study_service.mission.dto.ResMissionListDto;
import com.suite.suite_study_service.mission.entity.Mission;
import com.suite.suite_study_service.common.mockEntity.MockAuthorizer;
import com.suite.suite_study_service.common.mockEntity.MockDashBoard;

import com.suite.suite_study_service.mission.mockEntity.MockMission;
import com.suite.suite_study_service.mission.repository.MissionRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@Rollback
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

        makeMockMissionList("test", Timestamp.valueOf("2023-10-15 18:00:00"), MissionType.PROGRESS)
                .stream()
                .forEach(mission -> {
                            missionRepository.save(mission);
                        });
        List<Mission> assertionMissions = missionRepository.findAllBySuiteRoomId(1L);
        //then
        Assertions.assertAll(
                ()-> assertThat(assertionMissions.get(0).getClass()).isEqualTo(Mission.class),
                ()-> assertThat(assertionMissions.size()).isEqualTo(3),
                ()-> assertThat(assertionMissions.get(0).getMissionStatus()).isEqualTo(MissionType.PROGRESS)
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
        makeMockMissionList("test", Timestamp.valueOf("2023-10-15 18:00:00"), MissionType.PROGRESS)
                .stream()
                .forEach(mission -> {
                    missionRepository.save(mission);
                });
        AuthorizerDto missionReadAttempter = MockAuthorizer.YH();
        //when
        List<ResMissionListDto> assertionMissionsDto = missionRepository
                .findAllBySuiteRoomIdAndMissionStatusAndMemberId(1L, MissionType.PROGRESS, missionReadAttempter.getMemberId())
                .stream()
                .map(mission -> mission.toResMissionListDto())
                .collect(Collectors.toList());


        //then
        Assertions.assertAll(
                ()-> assertThat(assertionMissionsDto.get(0).getClass()).isNotEqualTo(Mission.class),
                ()-> assertThat(assertionMissionsDto.size()).isEqualTo(1)
        );

    }

    @Test
    @DisplayName("스터디 그룹 미션 목록 - 확인")
    public void getMissionsCheck() {
        //given
        makeMockMissionList("test", Timestamp.valueOf("2023-10-15 18:00:00"), MissionType.CHECKING)
                .stream()
                .forEach(mission -> {
                    missionRepository.save(mission);
                });
        AuthorizerDto missionReadAttempter = MockAuthorizer.YH();
        //when
        List<ResMissionListDto> assertionMissionsDto = missionRepository
                .findAllBySuiteRoomIdAndMissionStatusAndMemberId(1L, MissionType.CHECKING, missionReadAttempter.getMemberId())
                .stream()
                .map(mission -> mission.toResMissionListDto())
                .collect(Collectors.toList());
        //then
        Assertions.assertAll(
                ()-> assertThat(assertionMissionsDto.get(0).getClass()).isNotEqualTo(Mission.class),
                ()-> assertThat(assertionMissionsDto.size()).isEqualTo(1)
        );
    }

    @Test
    @DisplayName("스터디 그룹 미션 목록 - 완료")
    public void getMissionsComplete() {
        //given
        makeMockMissionList("test", Timestamp.valueOf("2023-10-15 18:00:00"), MissionType.COMPLETE)
                .stream()
                .forEach(mission -> {
                    missionRepository.save(mission);
                });
        AuthorizerDto missionReadAttempter = MockAuthorizer.YH();
        //when
        List<ResMissionListDto> assertionMissionsDto = missionRepository
                .findAllBySuiteRoomIdAndMissionStatusAndMemberId(1L, MissionType.COMPLETE, missionReadAttempter.getMemberId())
                .stream()
                .map(mission -> mission.toResMissionListDto())
                .collect(Collectors.toList());
        //then
        Assertions.assertAll(
                ()-> assertThat(assertionMissionsDto.get(0).getClass()).isNotEqualTo(Mission.class),
                ()-> assertThat(assertionMissionsDto.size()).isEqualTo(1)

        );
    }

    @Test
    @DisplayName("스터디 그룹 칸반 보드 관리 - 방장")
    public void getRequestedMissions() {
        //given
        makeMockMissionList("test", Timestamp.valueOf("2023-10-15 18:00:00"), MissionType.CHECKING)
                .stream()
                .forEach(mission -> {
                    missionRepository.save(mission);
                });
        AuthorizerDto missionReadAttempter = MockAuthorizer.YH();
        //when
        dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(1L, missionReadAttempter.getMemberId(), true).orElseThrow(
                () -> assertThrows(CustomException.class, () -> new CustomException(StatusCode.FORBIDDEN)));

        List<ResMissionListDto> assertionMissionsDto = missionRepository.findAllBySuiteRoomIdAndMissionStatus(1L, MissionType.CHECKING)
                .stream()
                .map(mission -> mission.toResMissionListDto())
                .collect(Collectors.toList());
        //then
        Assertions.assertAll(
                ()-> assertThat(assertionMissionsDto.get(0).getClass()).isNotEqualTo(Mission.class)


        );
    }

    @Test
    @DisplayName("스터디 그룹 미션 목록 - 기간 만료")
    public void timeOutMissions() {
        //given
        AuthorizerDto missionReadAttempter = MockAuthorizer.YH();
        makeMockMissionList("test", Timestamp.valueOf("2023-10-01 18:00:00"), MissionType.PROGRESS)
                .stream()
                .forEach(mission -> {
                    missionRepository.save(mission);
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
        List<Mission> m = makeMockMissionList("test", Timestamp.valueOf("2023-10-15 18:00:00"), MissionType.CHECKING)
                .stream()
                .map(mission ->
                        missionRepository.save(mission)).collect(Collectors.toList());
        //when
        Mission assertionMission = missionRepository.findByMissionIdAndMissionStatus(m.get(m.size() - 1).getMissionId(), MissionType.CHECKING).get();

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
        List<Mission> m = makeMockMissionList("test", Timestamp.valueOf("2023-10-15 18:00:00"), MissionType.CHECKING)
                .stream()
                .map(mission ->
                        missionRepository.save(mission)).collect(Collectors.toList());
        //when
        Boolean isHost = dashBoardRepository.findBySuiteRoomIdAndMemberId(1L, missionApprovalRequester.getMemberId()).get().isHost();
        Mission assertRequiredMission = missionRepository.findByMissionIdAndMissionStatus(m.get(m.size() - 1).getMissionId(), MissionType.CHECKING).get();

        if (isHost) assertRequiredMission.updateMissionStatus(MissionType.COMPLETE);
        //then
        Assertions.assertAll(
                ()-> assertThat(assertRequiredMission.getMissionStatus()).isEqualTo(MissionType.COMPLETE),
                ()-> assertThat(assertRequiredMission.getClass()).isEqualTo(Mission.class)
        );
    }

    @Test
    @DisplayName("스터디 그룹 미션 달성 요청 승인 - 방장")
    public void updateMissionStatusCheckingToCompleteHost() {
        //given
        AuthorizerDto missionApprovalRequester = MockAuthorizer.YH();
        List<Mission> m = makeMockMissionList("test", Timestamp.valueOf("2023-10-15 18:00:00"), MissionType.CHECKING)
                .stream()
                .map(mission ->
                        missionRepository.save(mission)).collect(Collectors.toList());
        //when
        dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(1L, missionApprovalRequester.getMemberId(), true).orElseThrow(
                () -> assertThrows(CustomException.class, () -> new CustomException(StatusCode.FORBIDDEN)));

        Mission assertRequiredMission = missionRepository.findByMissionIdAndMissionStatus(m.get(m.size() - 1).getMissionId(), MissionType.CHECKING).get();
        assertRequiredMission.updateMissionStatus(MissionType.COMPLETE);
        //then
        Assertions.assertAll(
                ()-> assertThat(assertRequiredMission.getMissionStatus()).isEqualTo(MissionType.COMPLETE),
                ()-> assertThat(assertRequiredMission.getClass()).isEqualTo(Mission.class)
        );
    }

    @Test
    @DisplayName("스터디 그룹 미션 달성 요청 승인 - 스터디원")
    public void updateMissionStatusCheckingToCompleteGuest() {
        //given
        AuthorizerDto missionApprovalRequester = MockAuthorizer.DH();
        makeMockMissionList("test", Timestamp.valueOf("2023-10-15 18:00:00"), MissionType.PROGRESS)
                .stream()
                .forEach(mission -> {
                    missionRepository.save(mission);
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
        makeMockMissionList("test", Timestamp.valueOf("2023-10-15 18:00:00"), MissionType.PROGRESS)
                .stream()
                .forEach(mission -> {
                    missionRepository.save(mission);
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
        makeMockMissionList("test", Timestamp.valueOf("2023-10-15 18:00:00"), MissionType.PROGRESS)
                .stream()
                .forEach(mission -> {
                    missionRepository.save(mission);
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
        AuthorizerDto missioncancelRequester = MockAuthorizer.YH();
        //when
        //then
        assertThrows(CustomException.class, () -> {
            dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(1L, missioncancelRequester.getMemberId(), false)
                    .orElseThrow(() -> new CustomException(StatusCode.FORBIDDEN));
        });
    }

    @Test
    @DisplayName("스터디 승인 요청 취소 / 반려 - 스터디원 / 방장")
    public void updateMissionStatusCheckingToProgress() {
        //given
        AuthorizerDto missioncancelRequester = MockAuthorizer.YH();
        List<Mission> m = makeMockMissionList("test", Timestamp.valueOf("2023-10-15 18:00:00"), MissionType.CHECKING)
                .stream()
                .map(mission ->
                    missionRepository.save(mission)).collect(Collectors.toList());
        //when
        Mission cancelRequiredMission = missionRepository.findByMissionIdAndMissionStatus(m.get(m.size() - 1).getMissionId(), MissionType.CHECKING).get();


        cancelRequiredMission.updateMissionStatus(MissionType.PROGRESS);
        //then
        Assertions.assertAll(
                ()-> assertThat(cancelRequiredMission.getMissionStatus()).isEqualTo(MissionType.PROGRESS),
                ()-> assertThat(cancelRequiredMission.getMissionName()).isEqualTo("test"),
                ()-> assertThat(cancelRequiredMission.getMemberId()).isEqualTo(3L)
        );

    }

    @Test
    @DisplayName("최종 미션 달성률 계산")
    public void getMissionRate() {
        //given
        AuthorizerDto missioncancelRequester = MockAuthorizer.DH();
        makeMockMissionList("complete & true mission", Timestamp.valueOf("2023-10-15 18:00:00"), MissionType.COMPLETE)
                .stream()
                .forEach(mission -> {
                    missionRepository.save(mission);
                });
        makeMockMissionList("fail mission 1", Timestamp.valueOf("2023-10-15 18:00:00"), MissionType.FAIL)
                .stream()
                .forEach(mission -> {
                    missionRepository.save(mission);
                });
        makeMockMissionList("complete & false mission2", Timestamp.valueOf("2023-10-15 18:00:00"), MissionType.FAIL)
                .stream()
                .forEach(mission -> {
                    missionRepository.save(mission);
                });
        //when

        float missionCount = missionRepository.findAllBySuiteRoomIdAndMemberId(1L, missioncancelRequester.getMemberId()).size();
        float missionTrueCount = missionRepository.findAllBySuiteRoomIdAndMissionStatusAndMemberId(1L, MissionType.COMPLETE, missioncancelRequester.getMemberId()).size();
        List<Mission> temp = missionRepository.findAllBySuiteRoomId(1L);


        int missionRate = (int) Math.ceil((missionTrueCount / missionCount) * 100);
        //then
        Assertions.assertAll(
                ()-> assertThat(missionCount).isEqualTo(3),
                ()-> assertThat(missionTrueCount).isEqualTo(1),
                ()-> assertThat(missionRate).isEqualTo(34)
        );

    }



    private List<Mission> makeMockMissionList(String missionName, Timestamp missionDeadLine, MissionType missionType) {
        List<DashBoard> dashBoards = dashBoardRepository.findAllBySuiteRoomId(1L);
        List<Mission> mockMissionList = new ArrayList<>();

        long idCounter = 1;
        for (DashBoard dashBoard : dashBoards) {
            Mission newMission = MockMission.newMission(idCounter, dashBoard.getMemberId() ,missionType, missionName, missionDeadLine);
            mockMissionList.add(newMission);
            idCounter++;
        }

        return mockMissionList;
    }




}
