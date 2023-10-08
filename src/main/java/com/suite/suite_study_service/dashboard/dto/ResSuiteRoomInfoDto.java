package com.suite.suite_study_service.dashboard.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@Getter
@RequiredArgsConstructor
public class ResSuiteRoomInfoDto {
    private Long suiteRoomId;
    private Boolean isStart;
    private String title;
    private Timestamp studyStartDate;
    private Timestamp studyDeadline;

    @Builder
    public ResSuiteRoomInfoDto(Long suiteRoomId, Boolean isStart, String title, Timestamp studyStartDate, Timestamp studyDeadline) {
        this.suiteRoomId = suiteRoomId;
        this.isStart = isStart;
        this.title = title;
        this.studyStartDate = studyStartDate;
        this.studyDeadline = studyDeadline;
    }
}