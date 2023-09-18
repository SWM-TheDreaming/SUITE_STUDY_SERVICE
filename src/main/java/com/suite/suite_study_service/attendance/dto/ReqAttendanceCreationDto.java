package com.suite.suite_study_service.attendance.dto;

import com.suite.suite_study_service.attendance.entity.Attendance;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReqAttendanceCreationDto {
    private long suiteRoomId;
    private int attendanceCode;

    public ReqAttendanceCreationDto(long suiteRoomId, int attendanceCode) {
        this.suiteRoomId = suiteRoomId;
        this.attendanceCode = attendanceCode;
    }

    public Attendance toAttendance(long memberId, int round) {
        return Attendance.builder()
                .memberId(memberId)
                .suiteRoomId(suiteRoomId)
                .round(round)
                .code(attendanceCode).build();
    }
}
