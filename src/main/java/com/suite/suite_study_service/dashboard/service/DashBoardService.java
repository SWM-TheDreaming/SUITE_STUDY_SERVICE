package com.suite.suite_study_service.dashboard.service;

import com.suite.suite_study_service.dashboard.dto.ResDashBoardDto;

import java.util.List;

public interface DashBoardService {
    ResDashBoardDto getDashboard(long suiteRoomId, long memberId);

    void terminateStudy(long suiteRoomId);
}
