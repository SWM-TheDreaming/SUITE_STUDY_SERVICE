package com.suite.suite_study_service.mission.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqMissionApprovalDto {
    private Long suiteRoomId;
    private String missionName;

    @Builder
    public ReqMissionApprovalDto(Long suiteRoomId, String missionName) {
        this.suiteRoomId = suiteRoomId;
        this.missionName = missionName;
    }
}
