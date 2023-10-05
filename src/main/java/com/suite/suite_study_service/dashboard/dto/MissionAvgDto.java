package com.suite.suite_study_service.dashboard.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MissionAvgDto {
    private Long memberId;
    private String missionCompleteCount;
    private Double missionRate;

    @Builder
    public MissionAvgDto(Long memberId, String missionCompleteCount, Double missionRate) {
        this.memberId = memberId;
        this.missionCompleteCount = missionCompleteCount;
        this.missionRate = missionRate;
    }
}
