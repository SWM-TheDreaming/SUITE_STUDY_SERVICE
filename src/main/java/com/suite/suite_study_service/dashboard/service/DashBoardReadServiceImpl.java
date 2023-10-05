package com.suite.suite_study_service.dashboard.service;

import com.suite.suite_study_service.attendance.repository.AttendanceAggregationRepository;
import com.suite.suite_study_service.attendance.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashBoardReadServiceImpl implements DashBoardReadService {
    private AttendanceRepository attendanceRepository;

    @Override
    public void getMemberStudyAvgInfo(Long memberId) {

    }
}
