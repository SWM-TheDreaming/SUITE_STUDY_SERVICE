package com.suite.suite_study_service.attendance.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@Getter
@NoArgsConstructor
public class GroupOfAttendanceDto {

    private long count;
    private int lastInsertedCode;
}


