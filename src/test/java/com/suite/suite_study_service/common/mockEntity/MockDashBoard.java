package com.suite.suite_study_service.common.mockEntity;

import com.suite.suite_study_service.dashboard.entity.DashBoard;
import lombok.Builder;

import javax.persistence.Column;

public class MockDashBoard {
    private Long suiteRoomId;
    private Long memberId;
    private String email;
    private String nickName;
    private boolean isHost;
    private int depositAmount;
    private int minAttendanceRate;
    private int minMissionCompleteRate;

    @Builder
    public MockDashBoard(Long suiteRoomId, Long memberId, String email, String nickName, boolean isHost, int depositAmount, int minAttendanceRate, int minMissionCompleteRate) {
        this.suiteRoomId = 1L;
        this.memberId = memberId;
        this.email = "STATIC EMAIL FOR TEST";
        this.nickName = "STATIC NICK NAME FOR TEST";
        this.isHost = isHost;
        this.depositAmount = 10000;
        this.minAttendanceRate = 80;
        this.minMissionCompleteRate = 80;
    }

    public DashBoard toDashBoard() {
        return DashBoard.builder()
                .suiteRoomId(this.suiteRoomId)
                .memberId(this.memberId)
                .email(this.email)
                .nickName(this.nickName)
                .isHost(this.isHost)
                .depositAmount(this.depositAmount)
                .minAttendanceRate(this.minAttendanceRate)
                .minMissionCompleteRate(this.minMissionCompleteRate)
                .build();
    }


}
