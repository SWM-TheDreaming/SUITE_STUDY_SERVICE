package com.suite.suite_study_service.attendance.repository;

import com.suite.suite_study_service.attendance.entity.Attendance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AttendanceRepository extends MongoRepository<Attendance, String>, AttendanceAggregationRepository {
    Optional<Attendance> findBySuiteRoomIdAndMemberIdAndRound(Long suiteRoomId, Long memberId, int round);
}
