package com.suite.suite_study_service.dashboard.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DashBoardAvgDto {
    private Long suiteRoomId;
    private Long memberId;

    @Builder
    public DashBoardAvgDto(Long suiteRoomId, Long memberId) {
        this.suiteRoomId = suiteRoomId;
        this.memberId = memberId;
    }
}
