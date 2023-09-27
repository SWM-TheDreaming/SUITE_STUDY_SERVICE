package com.suite.suite_study_service.mission.repository;

import com.suite.suite_study_service.mission.dto.MissionType;
import com.suite.suite_study_service.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long>, MissionDslRepository {
    List<Mission> findAllBySuiteRoomId(Long suiteRoomId);

    List<Mission> findAllBySuiteRoomIdAndMissionStatusAndMemberId(Long suiteRoomId, MissionType missionType, Long memberId);

    List<Mission> findBySuiteRoomIdAndMissionNameAndMissionStatus(Long suiteRoomId, String missionName, MissionType missionType);
    List<Mission> findAllBySuiteRoomIdAndMissionStatus(Long suiteRoomId, MissionType missionType);

    List<Mission> findAllBySuiteRoomIdAndMissionName(Long suiteRoomId, String missionName);
    Optional<Mission> findBySuiteRoomIdAndMissionNameAndMemberId(Long suiteRoomId, String missionName, Long memberId);

    Optional<Mission> findBySuiteRoomIdAndMissionNameAndMemberIdAndMissionStatus(Long suiteRoomId, String missionName, Long memberId, MissionType missionType);


}
