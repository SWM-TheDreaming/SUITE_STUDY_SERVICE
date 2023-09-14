package com.suite.suite_study_service.mission.repository;

import com.suite.suite_study_service.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findAllBySuiteRoomId(Long suiteRoomId);
}
