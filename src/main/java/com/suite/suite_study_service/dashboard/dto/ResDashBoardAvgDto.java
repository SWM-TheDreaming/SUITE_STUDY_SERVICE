package com.suite.suite_study_service.dashboard.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResDashBoardAvgDto {
    private int attendanceCompleteCount;
    private int missionCompleteCount;
    private Double attendanceAvgRate;
    private Double missionAvgRate;


    @Builder
    public ResDashBoardAvgDto(AttendanceAvgDto attendanceAvgDto, MissionAvgDto missionAvgDto) {
        this.attendanceCompleteCount = attendanceAvgDto.getAttendanceCompleteCount();
        this.attendanceAvgRate = attendanceAvgDto.getAttendanceAvgRate();
        this.missionCompleteCount = missionAvgDto.getMissionCompleteCount();
        this.missionAvgRate = missionAvgDto.getMissionAvgRate();
    }
}
