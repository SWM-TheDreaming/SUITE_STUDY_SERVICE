package com.suite.suite_study_service.mission.repository;

import com.suite.suite_study_service.mission.dto.MissionType;
import com.suite.suite_study_service.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findAllBySuiteRoomId(Long suiteRoomId);

    List<Mission> findAllBySuiteRoomIdAndMissionStatusAndMemberId(Long suiteRoomId, MissionType missionType, Long memberId);

    List<Mission> findAllBySuiteRoomIdAndMissionStatus(Long suiteRoomId, MissionType missionType);
    Optional<Mission> findBySuiteRoomIdAndMissionNameAndMemberId(Long suiteRoomId, String missionName, Long memberId);

    Optional<Mission> findBySuiteRoomIdAndMissionNameAndMemberIdAndMissionStatus(Long suiteRoomId, String missionName, Long memberId, MissionType missionType);
}
