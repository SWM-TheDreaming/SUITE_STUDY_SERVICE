package com.suite.suite_study_service.attendance.repository;

import com.suite.suite_study_service.attendance.dto.GroupOfAttendanceDto;

import java.util.List;

public interface AttendanceAggregationRepository {
    List<GroupOfAttendanceDto> filterBySuiteRoomIdAndGroupBySuiteRoomIdAndRound(Long suiteRoomId);
}
