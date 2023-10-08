package com.suite.suite_study_service.mission.entity;

import com.suite.suite_study_service.common.baseTime.BaseTimeEntity;
import com.suite.suite_study_service.mission.dto.MissionType;
import com.suite.suite_study_service.mission.dto.ResMissionListDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "mission")
public class Mission extends BaseTimeEntity {

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

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "mission_dead_line")
    private Timestamp missionDeadLine;

    @Enumerated(EnumType.STRING)
    @Column(name = "mission_status")
    private MissionType missionStatus;


    @Builder
    public Mission(Long missionId, Long suiteRoomId, Long memberId, String missionName, String nickName, Timestamp missionDeadLine, MissionType missionStatus) {
        this.missionId = missionId;
        this.suiteRoomId = suiteRoomId;
        this.memberId = memberId;
        this.missionName = missionName;
        this.nickName = nickName;
        this.missionDeadLine = missionDeadLine;
        this.missionStatus = missionStatus;
    }


    public void updateMissionStatus(MissionType missionType) {
        this.missionStatus = missionType;
    }

    public ResMissionListDto toResMissionListDto() {
        return ResMissionListDto.builder()
                .missionId(this.missionId)
                .missionName(this.missionName)
                .missionDeadLine(this.missionDeadLine)
                .nickName(this.nickName)
                .build();
    }
}
