package com.suite.suite_study_service.dashboard.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "dashboard")
public class DashBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dash_board_id", nullable = false)
    private Long dashboardId;

    @Column(name = "suite_room_id")
    private Long suiteRoomId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "email")
    private String email;

    @Column(name = "nickname")
    private String nickName;

    @Column(name = "is_host")
    private boolean isHost;

    @Column(name = "deposit_amount")
    private int depositAmount;

    @Column(name = "attendance_rate")
    private int minAttendanceRate;

    @Column(name = "mission_complete_rate")
    private int minMissionCompleteRate;


    @Builder
    public DashBoard(Long dashboardId, Long suiteRoomId, Long memberId, String email, String nickName, boolean isHost, int depositAmount, int minAttendanceRate, int minMissionCompleteRate) {
        this.dashboardId = dashboardId;
        this.suiteRoomId = suiteRoomId;
        this.memberId = memberId;
        this.email = email;
        this.nickName = nickName;
        this.isHost = isHost;
        this.depositAmount = depositAmount;
        this.minAttendanceRate = minAttendanceRate;
        this.minMissionCompleteRate = minMissionCompleteRate;
    }
}