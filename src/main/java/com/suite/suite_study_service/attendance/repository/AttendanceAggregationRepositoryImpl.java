package com.suite.suite_study_service.attendance.repository;

import com.suite.suite_study_service.attendance.dto.GroupOfAttendanceDto;
import com.suite.suite_study_service.attendance.entity.Attendance;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;


@RequiredArgsConstructor
public class AttendanceAggregationRepositoryImpl implements AttendanceAggregationRepository{

    private final MongoTemplate mongoTemplate;

    @Override
    public List<GroupOfAttendanceDto> filterBySuiteRoomIdAndGroupBySuiteRoomIdAndRound(Long suiteRoomId) {
        MatchOperation matchRoundOne = Aggregation.match(Criteria.where("suiteRoomId").is(suiteRoomId));
        SortOperation sortByCreatedAt = Aggregation.sort(Sort.by(Sort.Order.asc("attendanceTime")));
        GroupOperation groupBySuiteRoomIdAndRound = group("suiteRoomId", "round")
                .last("code").as("lastInsertedCode")
                .last("attendanceTime").as("lastAttendanceTime")
                .count().as("count");

        SortOperation sortByLastAttendanceAt = Aggregation.sort(Sort.by(Sort.Order.asc("lastAttendanceTime")));

        Aggregation aggregation = Aggregation.newAggregation(
                matchRoundOne,
                sortByCreatedAt,
                groupBySuiteRoomIdAndRound,
                sortByLastAttendanceAt
        );

        return mongoTemplate.aggregate(aggregation, "attendance", GroupOfAttendanceDto.class).getMappedResults();
    }


}
