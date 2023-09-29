package com.suite.suite_study_service.dashboard.service;

import com.suite.suite_study_service.dashboard.dto.ResSuiteRoomInfoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

public class SuiteRoomService {
    private final String ANP_POINT_URI;
    private final RestTemplate restTemplate;

    public SuiteRoomService(String ANP_POINT_URI, RestTemplate restTemplate) {
        this.ANP_POINT_URI = ANP_POINT_URI;
        this.restTemplate = restTemplate;
    }

    public ResSuiteRoomInfoDto getSuiteRoomInfo(Long suiteRoomId) {
        String url = ANP_POINT_URI + suiteRoomId;
        ResponseEntity<ResSuiteRoomInfoDto> suiteRoomInfoDto = restTemplate.getForEntity(url, ResSuiteRoomInfoDto.class);
        return Objects.requireNonNull(suiteRoomInfoDto.getBody());

    }
}
