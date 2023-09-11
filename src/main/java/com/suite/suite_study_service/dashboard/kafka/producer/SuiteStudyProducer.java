package com.suite.suite_study_service.dashboard.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuiteStudyProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String data) {
        log.info("{} message : {}", topic, data);
        this.kafkaTemplate.send(topic, data);
    }
}
