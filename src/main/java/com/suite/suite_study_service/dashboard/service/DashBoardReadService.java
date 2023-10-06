package com.suite.suite_study_service.dashboard.service;

import com.suite.suite_study_service.dashboard.dto.ResDashBoardAvgDto;

public interface DashBoardReadService {

    ResDashBoardAvgDto getMemberStudyAvgInfo(Long memberId);
}
