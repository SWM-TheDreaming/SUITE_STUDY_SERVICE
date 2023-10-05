package com.suite.suite_study_service.dashboard.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.suite.suite_study_service.dashboard.dto.DashBoardAvgDto;
import com.suite.suite_study_service.dashboard.entity.QDashBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import static com.suite.suite_study_service.dashboard.entity.QDashBoard.dashBoard;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashBoardDslRepositoryImpl implements DashBoardDslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public DashBoardAvgDto getDashBoardAvg(Long memberId) {
        QDashBoard d = dashBoard;

        return null;
    }
}
