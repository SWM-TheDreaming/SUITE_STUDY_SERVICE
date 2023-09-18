package com.suite.suite_study_service.attendance.service;

import com.suite.suite_study_service.attendance.dto.ReqAttendanceCreationDto;
import com.suite.suite_study_service.attendance.entity.Attendance;
import com.suite.suite_study_service.attendance.repository.AttendanceRepository;
import com.suite.suite_study_service.common.handler.CustomException;
import com.suite.suite_study_service.common.handler.StatusCode;
import com.suite.suite_study_service.dashboard.entity.DashBoard;
import com.suite.suite_study_service.dashboard.repository.DashBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final DashBoardRepository dashBoardRepository;
    private final AttendanceRepository attendanceRepository;

    @Override
    public void createAttendanceHost(ReqAttendanceCreationDto reqAttendanceCreationDto, long memberId) {
        DashBoard dashBoard = dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(reqAttendanceCreationDto.getSuiteRoomId(), memberId, true).orElseThrow(
                ()-> new CustomException(StatusCode.FORBIDDEN));

        Attendance attendance = reqAttendanceCreationDto
                .toAttendance(
                        memberId,
                        attendanceRepository.filterBySuiteRoomIdAndGroupBySuiteRoomIdAndRound(dashBoard.getSuiteRoomId()).size() + 1);

        attendanceRepository.save(attendance);
    }
}
