package com.suite.suite_study_service.dashboard.service;

import com.suite.suite_study_service.attendance.repository.AttendanceRepository;
import com.suite.suite_study_service.common.handler.CustomException;
import com.suite.suite_study_service.common.handler.StatusCode;
import com.suite.suite_study_service.dashboard.dto.*;
import com.suite.suite_study_service.dashboard.entity.DashBoard;
import com.suite.suite_study_service.dashboard.repository.DashBoardRepository;
import com.suite.suite_study_service.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashBoardServiceImpl implements DashBoardService {
    private final DashBoardRepository dashBoardRepository;
    private final AttendanceRepository attendanceRepository;
    private final MissionRepository missionRepository;
    private final SuiteRoomService suiteRoomService;

    @Override
    public ResDashBoardDto getDashboard(long suiteRoomId, long memberId) {
        DashBoard leaderDashBoard = dashBoardRepository.findBySuiteRoomIdAndIsHost(suiteRoomId, true).orElseThrow(
                () -> new CustomException(StatusCode.FORBIDDEN));


        List<OtherDashBoardDto> otherDashBoardDto = dashBoardRepository.findBySuiteRoomId(suiteRoomId).stream().map(
                member -> {
                    return member.toOtherDashBoardDto(
                            Optional.ofNullable(attendanceRepository.getAttendanceRate(suiteRoomId, member.getMemberId(), leaderDashBoard.getMemberId())).map(AttendanceRateDto::getAttendanceRate).orElse(null),
                            Optional.ofNullable(missionRepository.getMissionRate(suiteRoomId, member.getMemberId())).map(MissionRateDto::getMissionRate).orElse(null));
                }).collect(Collectors.toList());

        ResSuiteRoomInfoDto resSuiteRoomInfoDto = suiteRoomService.getSuiteRoomInfo(suiteRoomId);
        return ResDashBoardDto.builder()
                .depositAmount(leaderDashBoard.getDepositAmount())
                .myMemberId(memberId)
                .otherDashBoardDto(otherDashBoardDto)
                .isStart(resSuiteRoomInfoDto.getIsStart())
                .studyDeadline(resSuiteRoomInfoDto.getStudyDeadline()).build();

    }
}
