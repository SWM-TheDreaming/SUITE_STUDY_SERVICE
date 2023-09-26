package com.suite.suite_study_service.attendance.repository;

import com.suite.suite_study_service.attendance.dto.GroupOfAttendanceDto;
import com.suite.suite_study_service.attendance.dto.AttendanceBoardDto;

import java.util.List;

public interface AttendanceAggregationRepository {
    List<GroupOfAttendanceDto> filterByGroupBySuiteRoomIdAndRound(Long suiteRoomId);
    List<AttendanceBoardDto> filterByGroupByMemberId(Long suiteRoomId, Long memberId, Long leaderMemberId);
    int filterByGroupBySuiteRoomIdAndMemberId(Long suiteRoomId, Long memberId);

}
