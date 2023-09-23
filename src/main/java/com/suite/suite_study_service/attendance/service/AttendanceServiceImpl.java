package com.suite.suite_study_service.attendance.service;

import com.suite.suite_study_service.attendance.dto.GroupOfAttendanceDto;
import com.suite.suite_study_service.attendance.dto.ReqAttendanceCreationDto;
import com.suite.suite_study_service.attendance.dto.ReqAttendanceDto;
import com.suite.suite_study_service.attendance.dto.ResAttendanceBoardDto;
import com.suite.suite_study_service.attendance.entity.Attendance;
import com.suite.suite_study_service.attendance.repository.AttendanceRepository;
import com.suite.suite_study_service.common.handler.CustomException;
import com.suite.suite_study_service.common.handler.StatusCode;
import com.suite.suite_study_service.dashboard.entity.DashBoard;
import com.suite.suite_study_service.dashboard.repository.DashBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final DashBoardRepository dashBoardRepository;
    private final AttendanceRepository attendanceRepository;

    @Override
    public void createAttendanceHost(ReqAttendanceCreationDto reqAttendanceCreationDto, long memberId) {
        DashBoard dashBoard = dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(reqAttendanceCreationDto.getSuiteRoomId(), memberId, true).orElseThrow(
                ()-> new CustomException(StatusCode.FORBIDDEN));

        List<GroupOfAttendanceDto> groupOfAttendanceDtoList = attendanceRepository.filterByGroupBySuiteRoomId(dashBoard.getSuiteRoomId());
        int curRound = groupOfAttendanceDtoList.size();
        compareAttendanceTime(reqAttendanceCreationDto.getSuiteRoomId(), memberId, curRound, true);
        Attendance attendance = reqAttendanceCreationDto.toAttendance(memberId, curRound + 1);

        attendanceRepository.save(attendance);
    }

    @Override
    @Transactional
    public void registerAttendanceGuest(ReqAttendanceDto reqAttendanceDto, long memberId) {
        DashBoard dashBoard = dashBoardRepository.findBySuiteRoomIdAndMemberIdAndIsHost(reqAttendanceDto.getSuiteRoomId(), memberId, false).orElseThrow(
                () -> new CustomException(StatusCode.FORBIDDEN));
        List<GroupOfAttendanceDto> groupOfAttendanceDtoList = attendanceRepository.filterByGroupBySuiteRoomId(dashBoard.getSuiteRoomId());

        int round = groupOfAttendanceDtoList.size();
        if(round == 0) throw new CustomException(StatusCode.TIMEOUT_ATTENDANCE);
        int code = groupOfAttendanceDtoList.get(round-1).getLastInsertedCode();

        attendanceRepository.findBySuiteRoomIdAndMemberIdAndRound(reqAttendanceDto.getSuiteRoomId(), memberId, round).ifPresent(
                attendance -> {throw new CustomException(StatusCode.ALREADY_ATTENDANCE);});
        if(code != reqAttendanceDto.getCode()) throw new CustomException(StatusCode.INVALID_ATTENDANCE_CODE);
        compareAttendanceTime(reqAttendanceDto.getSuiteRoomId(), reqAttendanceDto.getHostId(), round, false);

        attendanceRepository.save(reqAttendanceDto.toAttendance(memberId, round));
    }

    @Override
    public List<ResAttendanceBoardDto> getAttendanceBoard(long suiteRoomId, long memberId) {
        DashBoard leader = dashBoardRepository.findBySuiteRoomIdAndIsHost(suiteRoomId, true).orElseThrow(
                () -> new CustomException(StatusCode.FORBIDDEN));

        return attendanceRepository.filterByGroupByMemberId(suiteRoomId, memberId, leader.getMemberId());
    }


    private void compareAttendanceTime(Long suiteRoomId, Long hostId, int round, boolean isHost) {
        Optional<Attendance> curAttendance = attendanceRepository.findBySuiteRoomIdAndMemberIdAndRound(suiteRoomId, hostId, round);
        if(curAttendance.isEmpty()) return;

        ZonedDateTime seoulTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        Date now = Date.from(seoulTime.toInstant());
        long differenceInMinutes = (now.getTime() - curAttendance.get().getAttendanceTime().getTime()) / (60 * 1000);

        if (differenceInMinutes >= 5 && !isHost) {
            throw new CustomException(StatusCode.TIMEOUT_ATTENDANCE);
        }else if (differenceInMinutes <= 5 && isHost) {
            throw new CustomException(StatusCode.CREATE_ATTENDANCE_ERROR);
        }
    }
}
