package com.suite.suite_study_service.mission.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqDeleteMissionDto {
    private Long suiteRoomId;
    private String missionName;

    @Builder
    public ReqDeleteMissionDto(Long suiteRoomId, String missionName) {
        this.suiteRoomId = suiteRoomId;
        this.missionName = missionName;
    }
}
