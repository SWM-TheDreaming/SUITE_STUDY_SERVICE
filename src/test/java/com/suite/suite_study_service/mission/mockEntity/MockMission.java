package com.suite.suite_study_service.mission.mockEntity;

import com.suite.suite_study_service.mission.dto.*;
import com.suite.suite_study_service.mission.entity.Mission;
import lombok.Builder;
import org.mockito.Mock;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MockMission {
    private Long missionId;
    private Long suiteRoomId;
    private Long memberId;
    private String missionName;
    private Timestamp missionDeadLine;
    private MissionType missionStatus;




    @Builder
    public MockMission(Long missionId, Long suiteRoomId, Long memberId, String missionName, Timestamp missionDeadLine, MissionType missionStatus) {
        this.missionId = missionId;
        this.suiteRoomId = suiteRoomId;
        this.memberId = memberId;
        this.missionName = missionName;
        this.missionDeadLine = missionDeadLine;
        this.missionStatus = missionStatus;
    }

    public Mission toMission() {
        return Mission.builder()
                .missionId(this.missionId)
                .suiteRoomId(this.suiteRoomId)
                .memberId(this.memberId)
                .missionName(this.missionName)
                .missionDeadLine(this.missionDeadLine)
                .missionStatus(this.missionStatus)
                .build();
    }

    public static ReqMissionDto getReqMissionDto() {
        return ReqMissionDto.builder()
                .suiteRoomId(1L)
                .missionName("test")
                .missionDeadLine(Timestamp.valueOf("2023-10-15 18:00:00"))
                .build();
    }

    public static ReqMissionListDto getReqMissionListDto(String missionTypeString) {
        return ReqMissionListDto.builder()
                .suiteRoomId(1L)
                .missionTypeString(missionTypeString)
                .build();
    }

    public static ReqMissionApprovalDto getReqMissionApprovalDto(Long suiteRoomId, Long missionId) {
        return ReqMissionApprovalDto.builder()
                .suiteRoomId(1L)
                .missionId(missionId)
                .build();
    }

    public static ReqDeleteMissionDto getReqDeleteMissionDto(Long suiteRoomId, String missionName) {
        return ReqDeleteMissionDto.builder()
                .suiteRoomId(suiteRoomId)
                .missionName(missionName)
                .build();
    }

    public static Mission newMission (Long memberId, MissionType missionType, String missionName, Timestamp missionDeadLine) {
        return Mission.builder()
                .suiteRoomId(1L)
                .memberId(memberId)
                .missionName(missionName)
                .missionDeadLine(missionDeadLine)
                .missionStatus(missionType)
                .build();
    }

}
