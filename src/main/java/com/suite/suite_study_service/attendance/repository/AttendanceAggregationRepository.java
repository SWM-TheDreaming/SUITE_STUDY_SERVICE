package com.suite.suite_study_service.attendance.repository;

import com.suite.suite_study_service.dashboard.dto.AttendanceRateDto;
import com.suite.suite_study_service.attendance.dto.GroupOfAttendanceDto;
import com.suite.suite_study_service.attendance.dto.AttendanceBoardDto;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AttendanceAggregationRepository {
    List<GroupOfAttendanceDto> filterByGroupBySuiteRoomIdAndRound(Long suiteRoomId);
    List<AttendanceBoardDto> filterByGroupByMemberId(Long suiteRoomId, Long memberId, Long leaderMemberId);
    int filterByGroupBySuiteRoomIdAndMemberId(Long suiteRoomId, Long memberId);
    AttendanceRateDto getAttendanceRate(Long suiteRoomId, Long memberId, Long leaderMemberId);
    int getAttendanceCountForMember(Long suiteRoomId, Long memberId);
    List<Date> getAttendanceDatesBySuiteRoomIdAndMemberId(Long suiteRoomId, Long memberId);
}
