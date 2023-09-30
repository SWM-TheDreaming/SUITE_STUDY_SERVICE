package com.suite.suite_study_service.mission.repository;

import com.suite.suite_study_service.dashboard.dto.MissionRateDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissionDslRepository {
    MissionRateDto getMissionRate(Long suiteRoomId, Long memberId);
}
