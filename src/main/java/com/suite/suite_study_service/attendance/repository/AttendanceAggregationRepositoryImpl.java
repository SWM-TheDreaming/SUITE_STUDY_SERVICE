package com.suite.suite_study_service.attendance.repository;

import com.suite.suite_study_service.attendance.dto.GroupOfAttendanceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;


@RequiredArgsConstructor
public class AttendanceAggregationRepositoryImpl implements AttendanceAggregationRepository{

    private final MongoTemplate mongoTemplate;

    @Override
    public List<GroupOfAttendanceDto> filterBySuiteRoomIdAndGroupBySuiteRoomIdAndRound(Long suiteRoomId) {
        MatchOperation matchRoundOne = Aggregation.match(Criteria.where("suiteRoomId").is(suiteRoomId));
        GroupOperation groupBySuiteRoomIdAndRound = group("suiteRoomId", "round").count().as("count");

        Aggregation aggregation = Aggregation.newAggregation(
                matchRoundOne,
                groupBySuiteRoomIdAndRound
        );

        return mongoTemplate.aggregate(aggregation, "attendance", GroupOfAttendanceDto.class).getMappedResults();
    }
}
