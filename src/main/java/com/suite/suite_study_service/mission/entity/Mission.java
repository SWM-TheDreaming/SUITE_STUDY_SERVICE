package com.suite.suite_study_service.mission.entity;

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

    @Column(name = "mission_status")
    private String missionStatus;

    @Column(name = "result")
    private boolean result;
}
