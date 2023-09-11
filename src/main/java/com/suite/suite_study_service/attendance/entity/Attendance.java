package com.suite.suite_study_service.attendance.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id", nullable = false)
    private Long attendanceId;

    @Column(name = "suite_room_id")
    private Long suiteRoomId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "round")
    private int round;

    @Column(name = "status")
    private boolean status;

    @Column(name = "attendance_time")
    private Timestamp attendanceTime;
}
