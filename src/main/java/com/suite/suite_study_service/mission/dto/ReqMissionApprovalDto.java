package com.suite.suite_study_service.mission.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqMissionApprovalDto {
    private Long suiteRoomId;
    private Long missionId;

    @Builder
    public ReqMissionApprovalDto(Long suiteRoomId, Long missionId) {
        this.suiteRoomId = suiteRoomId;
        this.missionId = missionId;
    }
}
