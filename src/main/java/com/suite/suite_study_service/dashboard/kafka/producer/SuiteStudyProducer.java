package com.suite.suite_study_service.dashboard.kafka.producer;

import com.suite.suite_study_service.attendance.repository.AttendanceAggregationRepository;
import com.suite.suite_study_service.dashboard.entity.DashBoard;
import com.suite.suite_study_service.mission.dto.MissionType;
import com.suite.suite_study_service.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuiteStudyProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MissionRepository missionRepository;
    private final AttendanceAggregationRepository attendanceAggregationRepository;

    @Value("${topic.HALLOFFAME-SELECTION}") private String HALLOFFAME_SELECTION;
    @Value("Stop-Study") private String STOP_STUDY;
    public void sendMessage(String topic, String data) {
        log.info("{} message : {}", topic, data);
        this.kafkaTemplate.send(topic, data);
    }

    public void selectHallOfFame(Long suiteRoomId, Double honorPoint) {
        Map<String, Object> data = Map.of("honorPoint", honorPoint, "suiteRoomId", suiteRoomId);
        log.info("SuiteRoom-Start message : {}", honorPoint);
        this.kafkaTemplate.send(HALLOFFAME_SELECTION, makeMessage(data));
    }

    public void stopStudyContract(Long suiteRoomId, String title, List<DashBoard> dashBoards, int leaderAttendanceCount) {
        List<String> participantIds = new ArrayList<>();
        List<String> participantNames = new ArrayList<>();
        List<Integer> participantMissions = new ArrayList<>();
        List<Integer> participantAttendances = new ArrayList<>();
        dashBoards.stream()
                .forEach(dashBoard -> {
                    participantIds.add(dashBoard.getEmail());
                    participantNames.add(dashBoard.getName());
                    participantMissions.add(getMissionRate(suiteRoomId, dashBoard.getMemberId()));
                    participantAttendances.add(getAttendanceRate(suiteRoomId, dashBoard.getMemberId(), leaderAttendanceCount));
                });
        this.kafkaTemplate.send(STOP_STUDY, makeMessage(makeData(suiteRoomId, title, participantIds, participantNames, participantMissions, participantAttendances)));
    }

    private int getMissionRate(Long suiteRoomId, Long memberId) {
        float missionCount = missionRepository.findAllBySuiteRoomIdAndMemberId(suiteRoomId, memberId).size();
        float missionTrueCount = missionRepository.findAllBySuiteRoomIdAndMissionStatusAndMemberIdAndResult(suiteRoomId, MissionType.COMPLETE, memberId, true).size();
        return (int) ((missionTrueCount / missionCount) * 100);
    }

    private int getAttendanceRate(Long suiteRoomId, Long memberId, int leaderAttendanceCount) {
        return (int) ((attendanceAggregationRepository.filterByGroupBySuiteRoomIdAndMemberId(suiteRoomId, memberId) / (float)leaderAttendanceCount) * 100);
    }

    private Map<String, Object> makeData(Long suiteRoomId, String title, List<String> participantIds, List<String> participantNames, List<Integer> participantMissions, List<Integer> participantAttendances) {
        Map<String, Object> data = new HashMap<>();
        data.put("suite_room_id", suiteRoomId);
        data.put("title", title);
        data.put("participant_ids", participantIds);
        data.put("participant_names", participantNames);
        data.put("participant_mission", participantMissions);
        data.put("participant_attendance", participantAttendances);

        return data;
    }

    private String makeMessage(Map<String, Object> data) {
        JSONObject obj = new JSONObject();
        obj.put("uuid", "SuiteRoomProducer/" + Instant.now().toEpochMilli());
        obj.put("data", data);
        return obj.toJSONString();
    }

}
