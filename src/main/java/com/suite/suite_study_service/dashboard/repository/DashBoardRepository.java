package com.suite.suite_study_service.dashboard.repository;

import com.suite.suite_study_service.dashboard.entity.DashBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DashBoardRepository extends JpaRepository<DashBoard, Long> {
    List<DashBoard> findAllBySuiteRoomId(Long suiteRoomId);
    Optional<DashBoard> findByMemberId(Long memberId);

}
