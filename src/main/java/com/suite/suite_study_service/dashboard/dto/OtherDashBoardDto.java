package com.suite.suite_study_service.dashboard.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OtherDashBoardDto {
    private String nickName;
    private Double attendanceRate;
    private Double missionRate;

    @Builder
    public OtherDashBoardDto(String nickName, Double attendanceRate, Double missionRate) {
        this.nickName = nickName;
        this.attendanceRate = (attendanceRate != null) ? attendanceRate : 0;
        this.missionRate = (missionRate != null) ? missionRate : 0;
    }
}
