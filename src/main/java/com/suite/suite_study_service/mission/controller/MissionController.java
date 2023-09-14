package com.suite.suite_study_service.mission.controller;

import com.suite.suite_study_service.common.dto.Message;
import com.suite.suite_study_service.common.handler.StatusCode;
import com.suite.suite_study_service.mission.dto.ReqMissionDto;
import com.suite.suite_study_service.mission.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study")
public class MissionController {

    private final MissionService missionService;

    @PostMapping("/mission/registration")
    public ResponseEntity<Message> registerMission(@RequestBody ReqMissionDto reqMissionDto) {
        missionService.createMission(reqMissionDto);
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }

}
