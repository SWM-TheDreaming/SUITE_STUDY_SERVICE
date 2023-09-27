package com.suite.suite_study_service.dashboard.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttendanceRateDto {
    private Long memberId;
    private String nickName;
    private Double attendanceRate;

    @Builder
    public AttendanceRateDto(Long memberId, String nickName, Double attendanceRate) {
        this.memberId = memberId;
        this.nickName = nickName;
        this.attendanceRate = attendanceRate;
    }

    public AttendanceRateDto setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }
}
