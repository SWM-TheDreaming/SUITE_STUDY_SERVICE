package com.suite.suite_study_service.dashboard.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MissionAvgDto {
    private int missionCompleteCount;
    private Double missionAvgRate;

    @Builder
    public MissionAvgDto(int missionCompleteCount, Double missionAvgRate) {
        this.missionCompleteCount = missionCompleteCount;
        this.missionAvgRate = missionAvgRate;
    }
}
