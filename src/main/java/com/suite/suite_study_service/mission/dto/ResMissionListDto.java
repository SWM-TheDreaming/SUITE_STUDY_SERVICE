package com.suite.suite_study_service.mission.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
public class ResMissionListDto {
    private Long missionId;
    private String missionName;
    private Timestamp missionDeadLine;
    private String nickName;

    @Builder
    public ResMissionListDto(Long missionId, String missionName, Timestamp missionDeadLine, String nickName) {
        this.missionId = missionId;
        this.missionName = missionName;
        this.missionDeadLine = missionDeadLine;
        this.nickName = nickName;
    }
}