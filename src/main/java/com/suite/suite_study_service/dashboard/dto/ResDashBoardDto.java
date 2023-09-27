package com.suite.suite_study_service.dashboard.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ResDashBoardDto {
    private int depositAmount;
    private Long myMemberId;
    private Boolean isStart;
    private List<OtherDashBoardDto> otherDashBoardDto;

    @Builder
    public ResDashBoardDto(int depositAmount, Long myMemberId, Boolean isStart, List<OtherDashBoardDto> otherDashBoardDto) {
        this.depositAmount = depositAmount;
        this.myMemberId = myMemberId;
        this.isStart = isStart;
        this.otherDashBoardDto = otherDashBoardDto;
    }
}
