package com.suite.suite_study_service.attendance.repository;

import com.suite.suite_study_service.attendance.dto.GroupOfAttendanceDto;
import com.suite.suite_study_service.attendance.dto.ResAttendanceBoardDto;

import java.util.List;

public interface AttendanceAggregationRepository {
    List<GroupOfAttendanceDto> filterByGroupBySuiteRoomId(Long suiteRoomId);
    List<ResAttendanceBoardDto> filterByGroupByMemberId(Long suiteRoomId, Long memberId, Long leaderMemberId);
}
