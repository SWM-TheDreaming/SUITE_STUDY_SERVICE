package com.suite.suite_study_service.dashboard.repository;

import com.suite.suite_study_service.dashboard.entity.DashBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DashBoardRepository extends JpaRepository<DashBoard, Long> {
    List<DashBoard> findAllBySuiteRoomId(Long suiteRoomId);
    Optional<DashBoard> findByMemberId(Long memberId);

    Optional<DashBoard> findBySuiteRoomIdAndMemberIdAndIsHost(Long suiteRoomId, Long memberId, Boolean isHost);

    Optional<DashBoard> findBySuiteRoomIdAndIsHost(Long suiteRoomId, Boolean isHost);

    Optional<DashBoard> findBySuiteRoomIdAndMemberId(Long suiteRoomId, Long memberId);
}