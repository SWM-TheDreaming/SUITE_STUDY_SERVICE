package com.suite.suite_study_service.dashboard.service;

import com.suite.suite_study_service.attendance.repository.AttendanceRepository;
import com.suite.suite_study_service.common.handler.CustomException;
import com.suite.suite_study_service.common.handler.StatusCode;
import com.suite.suite_study_service.dashboard.dto.*;
import com.suite.suite_study_service.dashboard.entity.DashBoard;
import com.suite.suite_study_service.dashboard.kafka.producer.SuiteStudyProducer;
import com.suite.suite_study_service.dashboard.repository.DashBoardRepository;
import com.suite.suite_study_service.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
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
    private final SuiteStudyProducer suiteStudyProducer;

    @Override
    public ResDashBoardDto getDashboard(long suiteRoomId, long memberId) {
        DashBoard leaderDashBoard = dashBoardRepository.findBySuiteRoomIdAndIsHost(suiteRoomId, true).orElseThrow(
                () -> new CustomException(StatusCode.FORBIDDEN));


        List<OtherDashBoardDto> otherDashBoardDto = dashBoardRepository.findBySuiteRoomId(suiteRoomId).stream().map(
                member -> member.toOtherDashBoardDto(
                        Optional.ofNullable(attendanceRepository.getAttendanceRate(suiteRoomId, member.getMemberId(), leaderDashBoard.getMemberId())).map(AttendanceRateDto::getAttendanceRate).orElse(null),
                        Optional.ofNullable(missionRepository.getMissionRate(suiteRoomId, member.getMemberId())).map(MissionRateDto::getMissionRate).orElse(null))
        ).collect(Collectors.toList());

        ResSuiteRoomInfoDto resSuiteRoomInfoDto = suiteRoomService.getSuiteRoomInfo(suiteRoomId);
        return ResDashBoardDto.builder()
                .depositAmount(leaderDashBoard.getDepositAmount())
                .myMemberId(memberId)
                .otherDashBoardDto(otherDashBoardDto)
                .isStart(resSuiteRoomInfoDto.getIsStart())
                .studyDeadline(resSuiteRoomInfoDto.getStudyDeadline()).build();

    }

    @Override
    public void terminateStudy(long suiteRoomId) {
        DashBoard leaderDashBoard = dashBoardRepository.findBySuiteRoomIdAndIsHost(suiteRoomId, true).orElseThrow(
                () -> new CustomException(StatusCode.FORBIDDEN));
        ResSuiteRoomInfoDto resSuiteRoomInfoDto = suiteRoomService.getSuiteRoomInfo(suiteRoomId);

        int leaderAttendanceCount = attendanceRepository.getAttendanceCountForMember(suiteRoomId, leaderDashBoard.getMemberId());

        List<Boolean> attendanceList = dashBoardRepository.findBySuiteRoomId(suiteRoomId).stream().map(
                member -> member.checkAttendance(attendanceRepository.getAttendanceCountForMember(suiteRoomId, member.getMemberId()), leaderAttendanceCount)).collect(Collectors.toList());

        boolean isAllAttendance = attendanceList.stream().allMatch(Boolean::booleanValue);
        boolean isAllMission = !missionRepository.existsBySuiteRoomIdAndResult(suiteRoomId, false);
        if(isAllAttendance && isAllMission) {
            List<Date> attendanceDateList = attendanceRepository.getAttendanceDatesBySuiteRoomIdAndMemberId(suiteRoomId, leaderDashBoard.getMemberId());
            Double attendanceFrequency = getAttendanceDateSum(attendanceDateList, getBetweenDate(resSuiteRoomInfoDto.getStudyStartDate(), resSuiteRoomInfoDto.getStudyDeadline()));
            int leaderMissionCount = missionRepository.getMissionCount(suiteRoomId, leaderDashBoard.getMemberId());
            int memberCount = dashBoardRepository.countBySuiteRoomId(suiteRoomId);
            suiteStudyProducer.selectHallOfFame(suiteRoomId, getHonorPoints(leaderAttendanceCount, leaderMissionCount, memberCount, attendanceFrequency));
        }
        //정산 진행


    }

    private Double getHonorPoints(int attendanceCount, int missionCount, int memberCount, Double attendanceFrequency) {
        return attendanceFrequency != 0 ? Math.ceil(((attendanceCount + missionCount + memberCount) / attendanceFrequency) * 100) / 100 : 0;
    }

    private Double getAttendanceDateSum(List<Date> attendanceDateList, Long studyDay) {
        int totalDaysDifference = 0;
        Date prevDate = null;

        for (Date currentDate : attendanceDateList) {
            if (prevDate != null) {
                Instant start = prevDate.toInstant();
                Instant end = currentDate.toInstant();

                long daysBetween = Duration.between(start, end).toDays();
                totalDaysDifference += daysBetween;
            }
            prevDate = currentDate;
        }
        Double avgAttendance = (double) totalDaysDifference / (attendanceDateList.size() - 1);

        return attendanceDateList.size() > 1 ? Math.ceil((avgAttendance / studyDay) * 1000) / 1000 : 0;
    }


    private Long getBetweenDate(Timestamp studyStartDate, Timestamp studyDeadline) {
        LocalDate start = studyStartDate.toLocalDateTime().toLocalDate();
        LocalDate end = studyDeadline.toLocalDateTime().toLocalDate();

        return ChronoUnit.DAYS.between(start, end);
    }

}
