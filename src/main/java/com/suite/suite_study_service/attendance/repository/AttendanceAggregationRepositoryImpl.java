package com.suite.suite_study_service.attendance.repository;

import com.suite.suite_study_service.attendance.dto.GroupOfAttendanceDto;
import com.suite.suite_study_service.attendance.dto.ResAttendanceBoardDto;
import com.suite.suite_study_service.attendance.entity.Attendance;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

@RequiredArgsConstructor
public class AttendanceAggregationRepositoryImpl implements AttendanceAggregationRepository{

    private final MongoTemplate mongoTemplate;

    @Override
    public List<GroupOfAttendanceDto> filterByGroupBySuiteRoomId(Long suiteRoomId) {
        MatchOperation matchSuiteRoomId = Aggregation.match(Criteria.where("suiteRoomId").is(suiteRoomId));
        SortOperation sortByCreatedAt = Aggregation.sort(Sort.by(Sort.Order.asc("attendanceTime")));
        GroupOperation groupBySuiteRoomIdAndRound = group("suiteRoomId", "round")
                .last("code").as("lastInsertedCode")
                .last("attendanceTime").as("lastAttendanceTime")
                .count().as("count");

        SortOperation sortByLastAttendanceAt = Aggregation.sort(Sort.by(Sort.Order.asc("lastAttendanceTime")));

        Aggregation aggregation = Aggregation.newAggregation(
                matchSuiteRoomId,
                sortByCreatedAt,
                groupBySuiteRoomIdAndRound,
                sortByLastAttendanceAt
        );

        return mongoTemplate.aggregate(aggregation, "attendance", GroupOfAttendanceDto.class).getMappedResults();
    }

    @Override
    public List<ResAttendanceBoardDto> filterByGroupByMemberId(Long suiteRoomId, Long memberId, Long leaderMemberId) {
        List<Attendance> leaderAttendances = mongoTemplate.find(Query.query(Criteria.where("memberId").is(leaderMemberId).and("suiteRoomId").is(suiteRoomId)), Attendance.class);

        List<ResAttendanceBoardDto> myAttendanceStatusList = new ArrayList<>();

        for(Attendance leaderAttendance : leaderAttendances) {
            Attendance myAttendance = mongoTemplate.findOne(Query.query(Criteria.where("memberId").is(memberId).and("suiteRoomId").is(suiteRoomId)
                    .and("round").is(leaderAttendance.getRound())), Attendance.class);
            ResAttendanceBoardDto resAttendanceBoardDto = ResAttendanceBoardDto.builder()
                    .round(leaderAttendance.getRound())
                    .suiteRoomId(suiteRoomId).build();
            resAttendanceBoardDto.setAttendanceTime(myAttendance != null ? myAttendance.getAttendanceTime() : leaderAttendance.getAttendanceTime());
            resAttendanceBoardDto.setStatus(myAttendance != null);
            myAttendanceStatusList.add(resAttendanceBoardDto);
        }

        return myAttendanceStatusList;
    }


}








