package com.suite.suite_study_service.attendance.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@RequiredArgsConstructor
public class ResAttendanceBoardDto {
    private Long suiteRoomId;
    private int round;
    private boolean status;
    private Date attendanceTime;

    public void setAttendanceTime(Date attendanceTime) {
        this.attendanceTime = attendanceTime;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Builder
    public ResAttendanceBoardDto(Long suiteRoomId, int round, boolean status, Date attendanceTime) {
        this.suiteRoomId = suiteRoomId;
        this.round = round;
        this.status = status;
        this.attendanceTime = attendanceTime;
    }
}
