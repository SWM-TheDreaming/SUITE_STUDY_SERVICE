package com.suite.suite_study_service.mission.repository;

import com.suite.suite_study_service.dashboard.dto.MissionAvgDto;
import com.suite.suite_study_service.dashboard.dto.MissionRateDto;
import org.springframework.stereotype.Repository;


@Repository
public interface MissionDslRepository {
    MissionRateDto getMissionRate(Long suiteRoomId, Long memberId);
    int getMissionCount(Long suiteRoomId, Long memberId);
    MissionAvgDto getMissionAvg(Long memberId);
}
