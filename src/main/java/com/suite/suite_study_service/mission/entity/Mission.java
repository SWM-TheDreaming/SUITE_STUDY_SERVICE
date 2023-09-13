package com.suite.suite_study_service.mission.entity;

import com.suite.suite_study_service.mission.dto.MissionType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "mission")
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id", nullable = false)
    private Long missionId;

    @Column(name = "suite_room_id")
    private Long suiteRoomId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "mission_name")
    private String missionName;

    @Column(name = "mission_dead_line")
    private Timestamp missionDeadLine;

    @Enumerated(EnumType.STRING)
    @Column(name = "mission_status")
    private MissionType missionStatus;

    @Column(name = "result")
    private boolean result;

    @Builder
    public Mission(Long missionId, Long suiteRoomId, Long memberId, String missionName, Timestamp missionDeadLine, MissionType missionStatus, boolean result) {
        this.missionId = missionId;
        this.suiteRoomId = suiteRoomId;
        this.memberId = memberId;
        this.missionName = missionName;
        this.missionDeadLine = missionDeadLine;
        this.missionStatus = missionStatus;
        this.result = result;
    }
}
