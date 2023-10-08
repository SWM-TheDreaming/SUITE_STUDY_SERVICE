package com.suite.suite_study_service.dashboard.service;

import com.suite.suite_study_service.attendance.repository.AttendanceRepository;
import com.suite.suite_study_service.dashboard.dto.*;
import com.suite.suite_study_service.dashboard.repository.DashBoardRepository;
import com.suite.suite_study_service.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashBoardReadServiceImpl implements DashBoardReadService {
    private final DashBoardRepository dashBoardRepository;
    private final AttendanceRepository attendanceRepository;
    private final MissionRepository missionRepository;

    @Override
    public ResDashBoardAvgDto getMemberStudyAvgInfo(Long memberId) {
        List<Double> attendanceRateList = dashBoardRepository.getDashBoardAvg(memberId).stream().map(
                suiteRoom -> suiteRoom.toAttendanceRateList(
                        Optional.ofNullable(attendanceRepository.getAttendanceRate(
                                suiteRoom.getSuiteRoomId(),
                                memberId,
                                dashBoardRepository.findBySuiteRoomIdAndIsHost(suiteRoom.getSuiteRoomId(), true).get().getMemberId()
                                )).map(AttendanceRateDto::getAttendanceRate).orElse(null)
                )
        ).collect(Collectors.toList());

        double attendanceSum = attendanceRateList.stream().mapToDouble(Double::doubleValue).sum();
        double attendanceAvg = attendanceRateList.size() != 0 ? attendanceSum / attendanceRateList.size() : 0;

        AttendanceAvgDto attendanceAvgDto = AttendanceAvgDto.builder()
                .attendanceAvgRate(Math.ceil(attendanceAvg * 100) / 100)
                .attendanceCompleteCount(attendanceRepository.getAllAttendanceCount(memberId)).build();

        MissionAvgDto missionAvgDto = missionRepository.getMissionAvg(memberId);

        return ResDashBoardAvgDto.builder()
                .attendanceAvgDto(attendanceAvgDto)
                .missionAvgDto(missionAvgDto).build();

    }
}
