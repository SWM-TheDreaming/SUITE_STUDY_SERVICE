package com.suite.suite_study_service.dashboard.entity;

import com.suite.suite_study_service.dashboard.dto.OtherDashBoardDto;
import com.suite.suite_study_service.dashboard.dto.ResDashBoardDto;
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

    @Column(name = "name")
    private String name;

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
    public DashBoard(Long dashboardId, Long suiteRoomId, Long memberId, String email, String name, String nickName, boolean isHost, int depositAmount, int minAttendanceRate, int minMissionCompleteRate) {
        this.dashboardId = dashboardId;
        this.suiteRoomId = suiteRoomId;
        this.memberId = memberId;
        this.email = email;
        this.name = name;
        this.nickName = nickName;
        this.isHost = isHost;
        this.depositAmount = depositAmount;
        this.minAttendanceRate = minAttendanceRate;
        this.minMissionCompleteRate = minMissionCompleteRate;
    }
    public OtherDashBoardDto toOtherDashBoardDto(Double attendanceRate, Double missionRate) {
        return OtherDashBoardDto.builder()
                .memberId(memberId)
                .nickName(nickName)
                .attendanceRate(attendanceRate)
                .missionRate(missionRate).build();
    }

    public boolean checkAttendance(int memberAttendanceCount, int leaderAttendanceCount) {
        return memberAttendanceCount == leaderAttendanceCount;
    }

}
