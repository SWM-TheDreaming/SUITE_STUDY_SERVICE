package com.suite.suite_study_service.attendance.service;

import com.suite.suite_study_service.attendance.dto.ReqAttendanceCreationDto;

public interface AttendanceService {
    void createAttendanceHost(ReqAttendanceCreationDto reqAttendanceCreationDto, long memberId);
}
