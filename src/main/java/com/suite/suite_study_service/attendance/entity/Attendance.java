package com.suite.suite_study_service.attendance.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.util.Date;

@Document(collection = "attendance")
@Entity
@Getter
@NoArgsConstructor
public class Attendance {

    @Id
    private Long attendanceId;
    private Long suiteRoomId;
    private Long memberId;
    private int round;
    private boolean status;
    private int code;
    @CreatedDate
    private Date attendanceTime;

    @Builder
    public Attendance(Long attendanceId, Long suiteRoomId, Long memberId, int round, boolean status, int code) {
        this.attendanceId = attendanceId;
        this.suiteRoomId = suiteRoomId;
        this.memberId = memberId;
        this.round = round;
        this.status = status;
        this.code = code;
    }
}
