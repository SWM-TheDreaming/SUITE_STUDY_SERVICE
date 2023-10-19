package com.suite.suite_study_service.dashboard.controller;

import com.suite.suite_study_service.dashboard.dto.ResDashBoardAvgDto;
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

    @RequestMapping("/study/study-info/{memberId}")
    public ResponseEntity<ResDashBoardAvgDto> getStudyInfo(@PathVariable Long memberId) {
        return ResponseEntity.ok(dashBoardReadService.getMemberStudyAvgInfo(memberId));
    }
}
