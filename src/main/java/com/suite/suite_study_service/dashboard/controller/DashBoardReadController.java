package com.suite.suite_study_service.dashboard.controller;

import com.suite.suite_study_service.common.dto.Message;
import com.suite.suite_study_service.common.handler.StatusCode;
import com.suite.suite_study_service.dashboard.service.DashBoardReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashBoardReadController {
    private final DashBoardReadService dashBoardReadService;

    @RequestMapping("/test/{memberId}")
    public ResponseEntity<Message> getData(@PathVariable Long memberId) {
        dashBoardReadService.getMemberStudyAvgInfo(memberId);
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }
}
