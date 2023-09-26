package com.suite.suite_study_service.attendance.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ResAttendanceBoardDto {
    private Double myAttendanceRate;
    private int depositAmount;
    private List<AttendanceBoardDto> attendanceBoardDtoList;

    @Builder
    public ResAttendanceBoardDto(Double myAttendanceRate, int depositAmount, List<AttendanceBoardDto> attendanceBoardDtoList) {
        this.myAttendanceRate = myAttendanceRate;
        this.depositAmount = depositAmount;
        this.attendanceBoardDtoList = attendanceBoardDtoList;
    }
}
