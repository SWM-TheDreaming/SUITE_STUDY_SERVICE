package com.suite.suite_study_service.attendance.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
public class ReqAttendanceDto {

    private Long suiteRoomId;
    private Long memberId;
    private int round;
    private boolean status;
    private Timestamp attendanceTime;
}
