package com.suite.suite_study_service.mission.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.suite.suite_study_service.dashboard.dto.MissionAvgDto;
import com.suite.suite_study_service.dashboard.dto.MissionRateDto;
import com.suite.suite_study_service.mission.dto.MissionType;
import com.suite.suite_study_service.mission.entity.QMission;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static com.suite.suite_study_service.mission.entity.QMission.mission;


@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MissionDslRepositoryImpl implements MissionDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public MissionRateDto getMissionRate(Long suiteRoomId, Long memberId) {
        QMission m = mission;

        Long countResult = jpaQueryFactory.select(m.count()).from(m).where(m.suiteRoomId.eq(suiteRoomId).and(m.memberId.eq(memberId))).groupBy(m.suiteRoomId, m.memberId).fetchOne();
        int cnt = Optional.ofNullable(countResult).orElse(0L).intValue();
        if(cnt == 0 ) return null;
        return jpaQueryFactory
                .select(Projections.constructor(MissionRateDto.class,
                        m.memberId,
                        MathExpressions.round(
                            new CaseBuilder()
                                    .when(m.missionStatus.eq(MissionType.COMPLETE)).then(1)
                                    .otherwise(0)
                                    .sum().divide(cnt).doubleValue(), 2)))
                .from(m)
                .where(m.suiteRoomId.eq(suiteRoomId), m.memberId.eq(memberId))
                .groupBy(m.suiteRoomId, m.memberId)
                .orderBy(m.memberId.asc())
                .fetchOne();
    }

    @Override
    public MissionAvgDto getMissionAvg(Long memberId) {
        QMission m = mission;

        Long countResult = jpaQueryFactory.select(m.count()).from(m).where(m.memberId.eq(memberId)).groupBy(m.memberId).fetchOne();
        int cnt = Optional.ofNullable(countResult).orElse(0L).intValue();

        if(cnt == 0) return MissionAvgDto.builder().missionAvgRate(0.0).missionCompleteCount(0).build();
        return jpaQueryFactory
                .select(Projections.constructor(MissionAvgDto.class,
                        MathExpressions.round(
                                new CaseBuilder()
                                        .when(m.missionStatus.eq(MissionType.COMPLETE)).then(1)
                                        .otherwise(0)
                                        .sum().coalesce(0)
                        ),
                        MathExpressions.round(
                                new CaseBuilder()
                                        .when(m.missionStatus.eq(MissionType.COMPLETE)).then(1)
                                        .otherwise(0)
                                        .sum().divide(cnt).doubleValue(), 2).coalesce(0.0)))
                .from(m)
                .where(m.memberId.eq(memberId))
                .groupBy(m.memberId).fetchOne();
    }

    @Override
    public int getMissionCount(Long suiteRoomId, Long memberId) {
        QMission m = mission;
        Long countResult = jpaQueryFactory.select(m.count()).from(m).where(m.suiteRoomId.eq(suiteRoomId).and(m.memberId.eq(memberId))).groupBy(m.suiteRoomId, m.memberId).fetchOne();
        return Optional.ofNullable(countResult).orElse(0L).intValue();
    }

}
