package com.suite.suite_study_service.mission.dto;

import com.suite.suite_study_service.mission.entity.Mission;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
public class ReqMissionDto {

    private Long suiteRoomId;
    private String missionName;
    private Timestamp missionDeadLine;
    @Builder
    public ReqMissionDto(Long suiteRoomId, String missionName, Timestamp missionDeadLine) {
        this.suiteRoomId = suiteRoomId;
        this.missionName = missionName;
        this.missionDeadLine = missionDeadLine;
    }

    public Mission toMission(Long memberId, String nickName) {
        return Mission.builder()
                .suiteRoomId(this.suiteRoomId)
                .memberId(memberId)
                .nickName(nickName)
                .missionName(this.missionName)
                .missionDeadLine(this.missionDeadLine)
                .missionStatus(MissionType.PROGRESS)
                .build();
    }
}
