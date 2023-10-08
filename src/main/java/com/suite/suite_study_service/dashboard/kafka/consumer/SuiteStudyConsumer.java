package com.suite.suite_study_service.dashboard.kafka.consumer;


import com.suite.suite_study_service.dashboard.entity.DashBoard;
import com.suite.suite_study_service.dashboard.kafka.producer.SuiteStudyProducer;
import com.suite.suite_study_service.dashboard.repository.DashBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuiteStudyConsumer {
    private final DashBoardRepository dashBoardRepository;
    private final SuiteStudyProducer suiteStudyProducer;
    @Value("${topic.START_NOTIFICATION}") private String START_NOTIFICATION;
    @Value("${topic.SUITEROOM_START_ERROR}") private String SUITEROOM_START_ERROR;

    @Transactional
    @KafkaListener(topics = "${topic.SUITEROOM_START}", groupId = "suiteRoomStartConsumers", containerFactory = "kafkaListenerDefaultContainerFactory")
    public void suiteRoomStartConsume(ConsumerRecord<String, String> record) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(record.value());
        JSONObject data = ((JSONObject) jsonObject.get("data"));
        Long suiteRoomId = Long.parseLong(data.get("suiteRoomId").toString());
        int depositAmount = Integer.parseInt(data.get("depositAmount").toString());
        int minAttendanceRate = Integer.parseInt(data.get("minAttendanceRate").toString());
        int minMissionCompleteRate = Integer.parseInt(data.get("minMissionCompleteRate").toString());

        JSONArray participants = ((JSONArray) data.get("participants"));

        try {
            addParticipantToDashBoard(suiteRoomId, depositAmount, minAttendanceRate, minMissionCompleteRate, participants);
            //알림 전송
            suiteStudyProducer.sendMessage(START_NOTIFICATION, record.value());
        } catch (Exception e) {
            e.printStackTrace();
            suiteStudyProducer.sendMessage(SUITEROOM_START_ERROR, record.value());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    private void addParticipantToDashBoard(Long suiteRoomId, int depositAmount, int minAttendanceRate, int minMissionCompleteRate, JSONArray participants) throws Exception {
        for(Object obj : participants) {
            JSONObject participant = (JSONObject) obj;
            dashBoardRepository.save(DashBoard.builder()
                    .suiteRoomId(suiteRoomId)
                    .minAttendanceRate(minAttendanceRate)
                    .minMissionCompleteRate(minMissionCompleteRate)
                    .depositAmount(depositAmount)
                    .memberId(Long.parseLong(participant.get("memberId").toString()))
                    .name(participant.get("name").toString())
                    .email(participant.get("email").toString())
                    .nickName(participant.get("nickName").toString())
                    .isHost(Boolean.parseBoolean(participant.get("host").toString())).build());

        }
    }
}