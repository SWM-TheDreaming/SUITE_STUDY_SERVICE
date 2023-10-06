package com.suite.suite_study_service.dashboard.service;

import com.suite.suite_study_service.attendance.repository.AttendanceAggregationRepository;
import com.suite.suite_study_service.attendance.repository.AttendanceRepository;
import com.suite.suite_study_service.dashboard.dto.DashBoardAvgDto;
import com.suite.suite_study_service.dashboard.repository.DashBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashBoardReadServiceImpl implements DashBoardReadService {
    private final DashBoardRepository dashBoardRepository;

    @Override
    public void getMemberStudyAvgInfo(Long memberId) {
        List<DashBoardAvgDto> dashBoardAvgDtoList = dashBoardRepository.getDashBoardAvg(memberId);

    }
}
