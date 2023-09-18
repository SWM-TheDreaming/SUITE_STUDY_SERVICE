package com.suite.suite_study_service.mission.service;

import com.suite.suite_study_service.common.handler.CustomException;
import com.suite.suite_study_service.common.handler.StatusCode;
import com.suite.suite_study_service.common.security.dto.AuthorizerDto;

import com.suite.suite_study_service.dashboard.repository.DashBoardRepository;
import com.suite.suite_study_service.mission.dto.MissionType;
import com.suite.suite_study_service.mission.dto.ReqMissionApprovalDto;
import com.suite.suite_study_service.mission.dto.ReqMissionDto;
import com.suite.suite_study_service.mission.dto.ReqMissionListDto;
import com.suite.suite_study_service.mission.entity.Mission;
import com.suite.suite_study_service.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.suite.suite_study_service.common.security.JwtInfoExtractor.getSuiteAuthorizer;

@Service
@RequiredArgsConstructor
public class MissionServiceImpl implements MissionService{

    private final MissionRepository missionRepository;
    private final DashBoardRepository dashBoardRepository;

    @Override
    @Transactional
    public void createMission(ReqMissionDto reqMissionDto) {
        AuthorizerDto missionCreateAttempter = getSuiteAuthorizer();

        dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(reqMissionDto.getSuiteRoomId(), missionCreateAttempter.getMemberId(), true)
                .orElseThrow(() -> new CustomException(StatusCode.FORBIDDEN));


        missionRepository.findBySuiteRoomIdAndMissionNameAndMemberId(1L, reqMissionDto.getMissionName(), missionCreateAttempter.getMemberId())
                .ifPresent(result -> {throw new CustomException(StatusCode.ALREADY_EXISTS_MISSION);});


        dashBoardRepository.findAllBySuiteRoomId(reqMissionDto.getSuiteRoomId())
                .stream()
                .forEach(dashBoard -> {
                    missionRepository.save(reqMissionDto.toMission(dashBoard.getMemberId()));
                });
    }

    @Override
    @Transactional
    public List<Mission> getRequestedMissions(ReqMissionListDto reqMissionListDto) {
        try {
            AuthorizerDto missionReadAttemper = getSuiteAuthorizer();
            dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(reqMissionListDto.getSuiteRoomId(), missionReadAttemper.getMemberId(), true)
                    .orElseThrow(() -> new CustomException(StatusCode.FORBIDDEN));

            Timestamp now = new Timestamp(System.currentTimeMillis());
            List<Mission> missionList = missionRepository.findAllBySuiteRoomIdAndMissionStatus(reqMissionListDto.getSuiteRoomId(), MissionType.CHECKING)
                    .stream()
                    .filter(mission -> isTimeOutMissions(mission, now))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());


            return missionList;
        } catch (Exception exception) {
            throw new CustomException(StatusCode.NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public List<Mission> getMissions(ReqMissionListDto reqMissionListDto) {
        try {
            AuthorizerDto missionReadAttemper = getSuiteAuthorizer();
            Timestamp now = new Timestamp(System.currentTimeMillis());
            List<Mission> missionList = missionRepository.findAllBySuiteRoomIdAndMissionStatusAndMemberId(reqMissionListDto.getSuiteRoomId(), MissionType.valueOf(reqMissionListDto.getMissionTypeString()), missionReadAttemper.getMemberId())
                    .stream()
                    .filter(mission -> isTimeOutMissions(mission, now))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());


            return missionList;
        } catch (Exception exception) {
            throw new CustomException(StatusCode.NOT_FOUND);
        }

    }

    @Override
    @Transactional
    public void updateMissionStatusProgressToChecking(ReqMissionApprovalDto reqMissionApprovalDto) {
        AuthorizerDto missionApprovalRequester = getSuiteAuthorizer();
        Boolean isHost = dashBoardRepository.findBySuiteRoomIdAndMemberId(reqMissionApprovalDto.getSuiteRoomId(), missionApprovalRequester.getMemberId()).get().isHost();
        Mission mission = missionRepository.findBySuiteRoomIdAndMissionNameAndMemberIdAndMissionStatus(reqMissionApprovalDto.getSuiteRoomId(), reqMissionApprovalDto.getMissionName(), missionApprovalRequester.getMemberId(), MissionType.PROGRESS)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND));

        if(isHost) mission.updateMissionStatusAndResult();
        else mission.updateMissionStatus(MissionType.CHECKING);
    }

    @Override
    @Transactional
    public void updateMissionStatusCheckingToComplete(ReqMissionApprovalDto reqMissionApprovalDto) {
        AuthorizerDto missionApprovalRequester = getSuiteAuthorizer();
        dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(reqMissionApprovalDto.getSuiteRoomId(), missionApprovalRequester.getMemberId(), true)
                .orElseThrow(() -> new CustomException(StatusCode.FORBIDDEN));

        Mission mission = missionRepository.findBySuiteRoomIdAndMissionNameAndMemberIdAndMissionStatus(reqMissionApprovalDto.getSuiteRoomId(), reqMissionApprovalDto.getMissionName(), reqMissionApprovalDto.getMemberId(), MissionType.CHECKING)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND));
        mission.updateMissionStatusAndResult();
    }

    @Override
    public void deleteMission(Long suiteRoomId, String missionName) {
        AuthorizerDto missionApprovalRequester = getSuiteAuthorizer();
        dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(suiteRoomId, missionApprovalRequester.getMemberId(), true).orElseThrow(
                () -> new CustomException(StatusCode.FORBIDDEN));

        missionRepository.findBySuiteRoomIdAndMissionNameAndMissionStatus(suiteRoomId, missionName, MissionType.PROGRESS)
                .stream()
                .forEach(mission -> {
                    missionRepository.delete(mission);
                });

    }

    @Override
    public void updateMissionStatusCheckingToProgress(Long suiteRoomId, String missionName, Long memberId) {
        Mission cancleRequiredMission = missionRepository.findBySuiteRoomIdAndMissionNameAndMemberIdAndMissionStatus(suiteRoomId, missionName, memberId, MissionType.CHECKING)
                .orElseThrow(() -> new CustomException(StatusCode.FORBIDDEN));
        cancleRequiredMission.updateMissionStatus(MissionType.PROGRESS);
    }

    private boolean isTimeOutMissions(Mission mission, Timestamp now) {
        if (mission.getMissionDeadLine().getTime() - now.getTime() < 0) {
            if(mission.getMissionStatus() == MissionType.COMPLETE) {
                return true;
            }
            mission.updateMissionStatus(MissionType.COMPLETE);
            return false;
        }
        return true;
    }
}
