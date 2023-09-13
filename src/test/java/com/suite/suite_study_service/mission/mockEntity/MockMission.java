package com.suite.suite_study_service.mission.mockEntity;

import com.suite.suite_study_service.mission.dto.MissionType;
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
    private Long suiteRoomId;
    private Long memberId;
    private String missionName;
    private Timestamp missionDeadLine;
    private MissionType missionStatus;
    private boolean result;

    @Builder
    public MockMission(Long suiteRoomId, Long memberId, String missionName, String missionDeadLine, MissionType missionStatus, boolean result) {
        this.suiteRoomId = suiteRoomId;
        this.memberId = memberId;
        this.missionName = missionName;
        this.missionDeadLine = getTimeStamp(missionDeadLine);
        this.missionStatus = missionStatus;
        this.result = result;
    }

    public Mission toMission() {
        return Mission.builder()
                .suiteRoomId(this.suiteRoomId)
                .memberId(this.memberId)
                .missionName(this.missionName)
                .missionDeadLine(this.missionDeadLine)
                .missionStatus(this.missionStatus)
                .result(this.result)
                .build();
    }

    private static Timestamp getTimeStamp(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(time, formatter);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Seoul"));
        return Timestamp.from(zonedDateTime.toInstant());
    }

}
