package com.suite.suite_study_service.mission.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqMissionListDto {
    private Long suiteRoomId;
    private String missionTypeString;

    @Builder
    public ReqMissionListDto(Long suiteRoomId, String missionTypeString) {
        this.suiteRoomId = suiteRoomId;
        this.missionTypeString = missionTypeString;
    }
}
