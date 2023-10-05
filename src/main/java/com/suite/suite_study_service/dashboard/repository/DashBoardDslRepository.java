package com.suite.suite_study_service.dashboard.repository;

import com.suite.suite_study_service.dashboard.dto.DashBoardAvgDto;
import org.springframework.stereotype.Repository;

@Repository
public interface DashBoardDslRepository {
    DashBoardAvgDto getDashBoardAvg(Long memberId);
}
