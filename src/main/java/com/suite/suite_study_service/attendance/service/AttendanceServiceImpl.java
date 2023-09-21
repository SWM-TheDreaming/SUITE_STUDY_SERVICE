package com.suite.suite_study_service.attendance.service;

import com.suite.suite_study_service.attendance.dto.GroupOfAttendanceDto;
import com.suite.suite_study_service.attendance.dto.ReqAttendanceCreationDto;
import com.suite.suite_study_service.attendance.dto.ReqAttendanceDto;
import com.suite.suite_study_service.attendance.entity.Attendance;
import com.suite.suite_study_service.attendance.repository.AttendanceRepository;
import com.suite.suite_study_service.common.handler.CustomException;
import com.suite.suite_study_service.common.handler.StatusCode;
import com.suite.suite_study_service.dashboard.entity.DashBoard;
import com.suite.suite_study_service.dashboard.repository.DashBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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

    @Override
    @Transactional
    public void registerAttendanceGuest(ReqAttendanceDto reqAttendanceDto, long memberId) {
        DashBoard dashBoard = dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(reqAttendanceDto.getSuiteRoomId(), memberId, false).orElseThrow(
                () -> new CustomException(StatusCode.FORBIDDEN));
        List<GroupOfAttendanceDto> groupOfAttendanceDtoList = attendanceRepository.filterBySuiteRoomIdAndGroupBySuiteRoomIdAndRound(dashBoard.getSuiteRoomId());
        int round = groupOfAttendanceDtoList.size();
        int code = groupOfAttendanceDtoList.get(round-1).getLastInsertedCode();

        attendanceRepository.findBySuiteRoomIdAndMemberIdAndRound(reqAttendanceDto.getSuiteRoomId(), memberId, round + 1).ifPresent(
                attendance -> {throw new CustomException(StatusCode.ALREADY_ATTENDANCE);});
        if(code != reqAttendanceDto.getCode()) throw new CustomException(StatusCode.INVALID_ATTENDANCE_CODE);
        compareAttendanceTime(reqAttendanceDto.getSuiteRoomId(), reqAttendanceDto.getHostId(), round);

        attendanceRepository.save(reqAttendanceDto.toAttendance(memberId, round));
    }


    private void compareAttendanceTime(Long suiteRoomId, Long hostId, int round) {
        Attendance curAttendance = attendanceRepository.findBySuiteRoomIdAndMemberIdAndRound(suiteRoomId, hostId, round).get();

        Date now = new Date();
        long differenceInMinutes = now.getTime() - curAttendance.getAttendanceTime().getTime() / (60 * 1000);
        if (differenceInMinutes < -5) throw new CustomException(StatusCode.TIMEOUT_ATTENDANCE);
    }
}
