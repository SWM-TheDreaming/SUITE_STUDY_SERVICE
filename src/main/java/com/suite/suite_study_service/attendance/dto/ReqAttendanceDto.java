package com.suite.suite_study_service.attendance.dto;

import java.sql.Timestamp;

public class ReqAttendanceDto {

    private Long suiteRoomId;
    private Long memberId;
    private int round;
    private boolean status;
    private Timestamp attendanceTime;
}
