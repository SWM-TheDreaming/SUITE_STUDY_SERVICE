package com.suite.suite_study_service.attendance.repository;

import com.suite.suite_study_service.attendance.dto.AttendanceBoardDto;
import com.suite.suite_study_service.attendance.dto.GroupOfAttendanceDto;
import com.suite.suite_study_service.attendance.entity.Attendance;
import com.suite.suite_study_service.dashboard.dto.AttendanceRateDto;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

@RequiredArgsConstructor
public class AttendanceAggregationRepositoryImpl implements AttendanceAggregationRepository{

    private final MongoTemplate mongoTemplate;

    @Override
    public List<GroupOfAttendanceDto> filterByGroupBySuiteRoomIdAndRound(Long suiteRoomId) {
        MatchOperation matchSuiteRoomId = match(Criteria.where("suiteRoomId").is(suiteRoomId));
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
    public int getAllAttendanceCount(Long memberId) {
        MatchOperation matchOperation = match(Criteria.where("memberId").is(memberId));
        GroupOperation groupOperation = group("memberId").count().as("count");
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                groupOperation
        );
        Map<String, Integer> data = mongoTemplate.aggregate(aggregation, "attendance", Map.class).getUniqueMappedResult();
        return data != null ? data.get("count") : 0;
    }

    @Override
    public int filterByGroupBySuiteRoomIdAndMemberId(Long suiteRoomId, Long memberId) {
        MatchOperation matchOperation = match(Criteria.where("suiteRoomId").is(suiteRoomId).and("memberId").is(memberId));
        GroupOperation groupByMemberId = group("memberId").count().as("count");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                groupByMemberId
        );
        Map<String, Integer> data = mongoTemplate.aggregate(aggregation, "attendance", Map.class).getUniqueMappedResult();
        return data != null ? data.get("count") : 0;
    }

    @Override
    public int getAttendanceCountForMember(Long suiteRoomId, Long memberId) {
        MatchOperation matchOperation = match(Criteria.where("suiteRoomId").is(suiteRoomId).and("memberId").is(memberId));
        GroupOperation groupByMemberId = group("memberId").count().as("count");
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                groupByMemberId
        );

        // 집계 연산 수행
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "attendance", Map.class);

        // 결과 반환
        return results.getUniqueMappedResult() != null ? Integer.parseInt(results.getUniqueMappedResult().get("count").toString()) : 0;
    }

    @Override
    public List<Date> getAttendanceDatesBySuiteRoomIdAndMemberId(Long suiteRoomId, Long memberId) {
        MatchOperation matchOperation = Aggregation.match(Criteria.where("suiteRoomId").is(suiteRoomId).and("memberId").is(memberId));
        ProjectionOperation projectionOperation = Aggregation.project("attendanceTime");
        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Order.asc("attendanceTime")));
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                projectionOperation,
                sortOperation)
        ;
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "attendance", Document.class);

        return results.getMappedResults().stream()
                .map(doc -> doc.getDate("attendanceTime"))
                .collect(Collectors.toList());
    }

    @Override
    public AttendanceRateDto getAttendanceRate(Long suiteRoomId, Long memberId, Long leaderMemberId) {
        MatchOperation leaderMatch = match(
                Criteria.where("memberId").is(leaderMemberId)
                        .and("suiteRoomId").is(suiteRoomId)
        );
        Document group = new Document("$group", new Document("_id", null).append("leaderAttendanceCount", new Document("$sum", 1)));
        MongoOperation groupByIdSum = new MongoOperation(group);

        Document lookup = new Document("$lookup", new Document("from", "attendance")
                        .append("let", new Document("leaderAttendanceCount", "$leaderAttendanceCount"))
                        .append("pipeline", Arrays.asList(
                                new Document("$match", new Document("$expr", new Document("$and", Arrays.asList(new Document("$eq", Arrays.asList("$suiteRoomId", suiteRoomId)), new Document("$eq", Arrays.asList("$memberId", memberId)))))),
                                new Document("$group", new Document("_id", "$memberId").append("totalAttendance", new Document("$sum", 1))),
                                new Document("$project", new Document("_id", 0).append("memberId", "$_id").append("attendanceRate", new Document("$cond", new Document("if", new Document("$eq", Arrays.asList("$$leaderAttendanceCount", 0)))
                                        .append("then", 0).append("else", new Document("$round", Arrays.asList(new Document("$divide", Arrays.asList("$totalAttendance", "$$leaderAttendanceCount")), 2))))))
                        )).append("as", "attendanceRates"));
        MongoOperation lookUpAttendanceRate = new MongoOperation(lookup);

        Document unwind = new Document("$unwind", "$attendanceRates");
        MongoOperation unwindAttendanceRates = new MongoOperation(unwind);

        Document sort = new Document("$sort", new Document("attendanceRates.memberId", 1));
        MongoOperation sortMemberId = new MongoOperation(sort);

        Document replaceRoot = new Document("$replaceRoot", new Document("newRoot", "$attendanceRates"));
        MongoOperation replaceRootAttendanceRates = new MongoOperation(replaceRoot);

        Aggregation aggregation = Aggregation.newAggregation(
                leaderMatch, groupByIdSum, lookUpAttendanceRate, unwindAttendanceRates, sortMemberId, replaceRootAttendanceRates

        );
        return mongoTemplate.aggregate(aggregation, "attendance", AttendanceRateDto.class).getUniqueMappedResult();
    }

    @Override
    public List<AttendanceBoardDto> filterByGroupByMemberId(Long suiteRoomId, Long memberId, Long leaderMemberId) {

        /*MatchOperation leaderMatch = match(
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
        return mongoTemplate.aggregate(aggregation, "attendance", AttendanceBoardDto.class).getMappedResults();*/


        List<Attendance> leaderAttendances = mongoTemplate.find(Query.query(Criteria.where("memberId").is(leaderMemberId).and("suiteRoomId").is(suiteRoomId)), Attendance.class);

        List<AttendanceBoardDto> myAttendanceStatusList = new ArrayList<>();

        for(Attendance leaderAttendance : leaderAttendances) {
            Attendance myAttendance = mongoTemplate.findOne(Query.query(Criteria.where("memberId").is(memberId).and("suiteRoomId").is(suiteRoomId)
                    .and("round").is(leaderAttendance.getRound())), Attendance.class);
            AttendanceBoardDto attendanceBoardDto = AttendanceBoardDto.builder()
                    .suiteRoomId(suiteRoomId)
                    .round(leaderAttendance.getRound()).build();
            attendanceBoardDto.setAttendanceTime(myAttendance != null ? myAttendance.getAttendanceTime() : leaderAttendance.getAttendanceTime());

            myAttendanceStatusList.add(attendanceBoardDto);
        }

        return myAttendanceStatusList;
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








