package com.suite.suite_study_service.dashboard.repository;

import com.suite.suite_study_service.dashboard.dto.DashBoardAvgDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashBoardDslRepository {
    List<DashBoardAvgDto> getDashBoardAvg(Long memberId);
}
