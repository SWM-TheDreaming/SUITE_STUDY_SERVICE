package com.suite.suite_study_service.mission.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.suite.suite_study_service.dashboard.dto.MissionRateDto;
import com.suite.suite_study_service.mission.dto.MissionType;
import com.suite.suite_study_service.mission.entity.QMission;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;


import static com.suite.suite_study_service.mission.entity.QMission.mission;


@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MissionDslRepositoryImpl implements MissionDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public MissionRateDto getMissionRate(Long suiteRoomId, Long memberId) {
        QMission m = mission;

        int cnt = jpaQueryFactory.select(m.count()).from(m).where(m.suiteRoomId.eq(suiteRoomId)).groupBy(m.suiteRoomId, m.memberId).fetchOne().intValue();
        if(cnt == 0) return null;
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
    public int getMissionCount(Long suiteRoomId, Long memberId) {
        QMission m = mission;
        return jpaQueryFactory.select(m.count()).from(m).where(m.suiteRoomId.eq(suiteRoomId), m.memberId.eq(memberId)).groupBy(m.suiteRoomId).fetchOne().intValue();
    }

}
