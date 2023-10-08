package com.suite.suite_study_service.mission.service;

import com.suite.suite_study_service.mission.dto.ReqMissionApprovalDto;
import com.suite.suite_study_service.mission.dto.ReqMissionDto;
import com.suite.suite_study_service.mission.dto.ReqMissionListDto;
import com.suite.suite_study_service.mission.dto.ResMissionListDto;
import com.suite.suite_study_service.mission.entity.Mission;

import java.util.List;

public interface MissionService {
    void createMission(ReqMissionDto reqMissionDto);

    List<ResMissionListDto> getRequestedMissions(ReqMissionListDto reqMissionListDto);
    List<ResMissionListDto> getMissions(ReqMissionListDto reqMissionListDto);

    void deleteMission(Long suiteRoomId, String missionName);

    void updateMissionStatusCheckingToProgress(Long missionId);
    void updateMissionStatusProgressToChecking(ReqMissionApprovalDto reqMissionApprovalDto);

    void updateMissionStatusCheckingToComplete(ReqMissionApprovalDto reqMissionApprovalDto);
}
