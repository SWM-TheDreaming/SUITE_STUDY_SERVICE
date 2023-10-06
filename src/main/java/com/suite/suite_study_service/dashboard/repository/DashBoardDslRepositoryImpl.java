package com.suite.suite_study_service.dashboard.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.suite.suite_study_service.dashboard.dto.DashBoardAvgDto;
import com.suite.suite_study_service.dashboard.entity.QDashBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.suite.suite_study_service.dashboard.entity.QDashBoard.dashBoard;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashBoardDslRepositoryImpl implements DashBoardDslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<DashBoardAvgDto> getDashBoardAvg(Long memberId) {
        QDashBoard d = dashBoard;
        QDashBoard sub = dashBoard;
        List<Long> participantSuiteRoomList = jpaQueryFactory.select(sub.suiteRoomId).from(sub).where(sub.memberId.eq(memberId)).fetch();

        return jpaQueryFactory
                .select(Projections.constructor(DashBoardAvgDto.class,
                                d.suiteRoomId))
                .from(d)
                .where(d.isHost.isTrue()
                        .and(d.suiteRoomId.in(participantSuiteRoomList))).fetch();
    }
}
