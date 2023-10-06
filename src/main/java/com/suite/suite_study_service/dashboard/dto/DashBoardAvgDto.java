package com.suite.suite_study_service.dashboard.dto;

import com.suite.suite_study_service.attendance.repository.AttendanceRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DashBoardAvgDto {
    private Long suiteRoomId;

    @Builder
    public DashBoardAvgDto(Long suiteRoomId) {
        this.suiteRoomId = suiteRoomId;
    }


    public Double toAttendanceRateList(Double attendanceRate) {
        return (attendanceRate != null) ? attendanceRate : 0;
    }
}
