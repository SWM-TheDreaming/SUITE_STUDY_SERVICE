package com.suite.suite_study_service.attendance.repository;

import com.suite.suite_study_service.attendance.entity.Attendance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AttendanceRepository extends MongoRepository<Attendance, Long>, AttendanceAggregationRepository {

}
