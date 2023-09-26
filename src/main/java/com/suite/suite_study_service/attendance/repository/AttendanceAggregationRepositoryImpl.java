package com.suite.suite_study_service.attendance.repository;

import com.suite.suite_study_service.attendance.dto.GroupOfAttendanceDto;
import com.suite.suite_study_service.attendance.dto.AttendanceBoardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

@RequiredArgsConstructor
public class AttendanceAggregationRepositoryImpl implements AttendanceAggregationRepository{

    private final MongoTemplate mongoTemplate;

    @Override
    public List<GroupOfAttendanceDto> filterByGroupBySuiteRoomIdAndRound(Long suiteRoomId) {
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
    public int filterByGroupBySuiteRoomIdAndMemberId(Long suiteRoomId, Long memberId) {
        MatchOperation matchOperation = Aggregation.match(Criteria.where("suiteRoomId").is(suiteRoomId).and("memberId").is(memberId));
        GroupOperation groupByMemberId = group("memberId").count().as("count");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                groupByMemberId
        );
        Map<String, Integer> data = mongoTemplate.aggregate(aggregation, "attendance", Map.class).getUniqueMappedResult();
        return data != null ? data.get("count") : 0;
    }

    @Override
    public List<AttendanceBoardDto> filterByGroupByMemberId(Long suiteRoomId, Long memberId, Long leaderMemberId) {

        MatchOperation leaderMatch = Aggregation.match(
                Criteria.where("memberId").is(leaderMemberId)
                        .and("suiteRoomId").is(suiteRoomId)
        );

        Document lookupDocument = new Document("$lookup", new Document()
                .append("from", "attendance")
                .append("let", new Document("leaderRound", "$round"))
                .append("pipeline", Arrays.asList(
                        new Document("$match", new Document("$expr", new Document()
                                .append("$and", Arrays.asList(
                                        new Document("$eq", Arrays.asList("$memberId", memberId)),
                                        new Document("$eq", Arrays.asList("$suiteRoomId", suiteRoomId)),
                                        new Document("$eq", Arrays.asList("$round", "$$leaderRound"))
                                ))
                        )),
                        new Document("$project", new Document("_id", 0).append("status", 1))
                ))
                .append("as", "myAttendanceRecords")
        );

        MongoOperation customLookupOperation = new MongoOperation(lookupDocument);

        Document projectDoc = new Document("$project", new Document()
                .append("round", 1)
                .append("suiteRoomId", 1)
                .append("attendanceTime", 1)
                .append("status", new Document("$gt", Arrays.asList(new Document("$size", "$myAttendanceRecords"), 0)))
        );

        MongoOperation customProjectDocOperation = new MongoOperation(projectDoc);
        Aggregation aggregation = Aggregation.newAggregation(
                leaderMatch,
                customLookupOperation,
                customProjectDocOperation
        );
        return mongoTemplate.aggregate(aggregation, "attendance", AttendanceBoardDto.class).getMappedResults();
        /*List<Attendance> leaderAttendances = mongoTemplate.find(Query.query(Criteria.where("memberId").is(leaderMemberId).and("suiteRoomId").is(suiteRoomId)), Attendance.class);

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

        return myAttendanceStatusList;*/
    }

    /*
    * mongo Query
    db.attendance.aggregate([
    {
        $match: { memberId: 1, suiteRoomId: 1 }
    },
    {
        $lookup: {
            from: "attendance",
            let: { leaderRound: "$round" },
            pipeline: [
                {
                    $match: {
                        $expr: {
                            $and: [
                                { $eq: ["$memberId", 2] },
                                { $eq: ["$suiteRoomId", 1] },
                                { $eq: ["$round", "$$leaderRound"] }
                            ]
                        }
                    }
                },
                {
                    $project: {
                        _id: 0,  // _id 필드를 제외하고 출력
                        status: 1  // status 필드만 출력
                    }
                }
            ],
            as: "myAttendanceRecords"
        }
    },
    {
        $project: {
            round: 1,
            suiteRoomId: 1,
            attendanceTime: 1,  // 방장의 attendanceTime 사용
            status: { $gt: [{ $size: "$myAttendanceRecords" }, 0] }  // 출석 기록이 있으면 true, 그렇지 않으면 false
        }
    }
])


     */

}








