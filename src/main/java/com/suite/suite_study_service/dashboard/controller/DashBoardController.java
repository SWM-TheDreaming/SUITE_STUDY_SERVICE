package com.suite.suite_study_service.dashboard.controller;

import com.suite.suite_study_service.common.dto.Message;
import com.suite.suite_study_service.common.handler.StatusCode;
import com.suite.suite_study_service.dashboard.service.DashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.suite.suite_study_service.common.security.JwtInfoExtractor.getSuiteAuthorizer;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study")
public class DashBoardController {
    private final DashBoardService dashBoardService;

    @PostMapping("/dashboard")
    public ResponseEntity<Message> lookUpListDashboard(@RequestBody Map<String, Long> suite) {
        return ResponseEntity.ok(new Message(StatusCode.OK, dashBoardService.getDashboard(suite.get("suiteRoomId"), getSuiteAuthorizer().getMemberId())));
    }

    @PostMapping("/hole")
    public ResponseEntity<Message> getCount(@RequestBody Map<String, Long> suite) {
        dashBoardService.getCount(suite.get("suiteRoomId"), getSuiteAuthorizer().getMemberId());
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }
}
