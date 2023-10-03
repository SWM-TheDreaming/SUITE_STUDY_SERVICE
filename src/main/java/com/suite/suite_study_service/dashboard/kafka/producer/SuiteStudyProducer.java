package com.suite.suite_study_service.dashboard.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuiteStudyProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${topic.HALLOFFAME-SELECTION}") private String HALLOFFAME_SELECTION;
    public void sendMessage(String topic, String data) {
        log.info("{} message : {}", topic, data);
        this.kafkaTemplate.send(topic, data);
    }

    public void selectHallOfFame(Long suiteRoomId, Double honorPoint) {
        Map<String, Object> data = Map.of("honorPoint", honorPoint, "suiteRoomId", suiteRoomId);
        log.info("SuiteRoom-Start message : {}", honorPoint);
        this.kafkaTemplate.send(HALLOFFAME_SELECTION, makeMessage(data));
    }

    private String makeMessage(Map<String, Object> data) {
        JSONObject obj = new JSONObject();
        obj.put("uuid", "SuiteRoomProducer/" + Instant.now().toEpochMilli());
        obj.put("data", data);
        return obj.toJSONString();
    }

}
