package com.suite.suite_study_service.dashboard.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttendanceAvgDto {
    private int attendanceCompleteCount;
    private Double attendanceAvgRate;

    @Builder
    public AttendanceAvgDto(int attendanceCompleteCount, Double attendanceAvgRate) {
        this.attendanceCompleteCount = attendanceCompleteCount;
        this.attendanceAvgRate = attendanceAvgRate;
    }
}
