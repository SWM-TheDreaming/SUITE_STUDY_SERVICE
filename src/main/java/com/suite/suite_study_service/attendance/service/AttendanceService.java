package com.suite.suite_study_service.attendance.service;

import com.suite.suite_study_service.attendance.dto.ReqAttendanceCreationDto;
import com.suite.suite_study_service.attendance.dto.ReqAttendanceDto;

public interface AttendanceService {
    void createAttendanceHost(ReqAttendanceCreationDto reqAttendanceCreationDto, long memberId);

    void registerAttendanceGuest(ReqAttendanceDto reqAttendanceDto, long memberId);
}
