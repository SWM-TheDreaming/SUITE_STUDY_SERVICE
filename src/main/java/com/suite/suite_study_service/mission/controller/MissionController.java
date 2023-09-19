package com.suite.suite_study_service.mission.controller;

import com.suite.suite_study_service.common.dto.Message;
import com.suite.suite_study_service.common.handler.StatusCode;
import com.suite.suite_study_service.mission.dto.ReqDeleteMissionDto;
import com.suite.suite_study_service.mission.dto.ReqMissionApprovalDto;
import com.suite.suite_study_service.mission.dto.ReqMissionDto;
import com.suite.suite_study_service.mission.dto.ReqMissionListDto;
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

    @PostMapping("/mission")
    public ResponseEntity<Message> listUpMissions(@RequestBody ReqMissionListDto reqMissionListDto) {
        return ResponseEntity.ok(new Message(StatusCode.OK,missionService.getMissions(reqMissionListDto)));
    }

    @PostMapping("/mission/admin")
    public ResponseEntity<Message> listUpRequestedMissions(@RequestBody ReqMissionListDto reqMissionListDto) {
        return ResponseEntity.ok(new Message(StatusCode.OK,missionService.getRequestedMissions(reqMissionListDto)));
    }

    @PostMapping("/mission/submission")
    public ResponseEntity<Message> requestApprovalMission(@RequestBody ReqMissionApprovalDto reqMissionApprovalDto) {
        missionService.updateMissionStatusProgressToChecking(reqMissionApprovalDto);
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }
    @PostMapping("/mission/approval")
    public ResponseEntity<Message> approvalRequestAcceptMission(@RequestBody ReqMissionApprovalDto reqMissionApprovalDto) {
        missionService.updateMissionStatusCheckingToComplete(reqMissionApprovalDto);
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }

    @PostMapping("/mission/delete")
    public ResponseEntity<Message> removeRequestMission(@RequestBody ReqDeleteMissionDto reqDeleteMissionDto) {
        missionService.deleteMission(reqDeleteMissionDto.getSuiteRoomId(), reqDeleteMissionDto.getMissionName());
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }

    @PostMapping("/mission/cancel")
    public ResponseEntity<Message> cancelMissionCompleteRequest(@RequestBody ReqMissionApprovalDto reqMissionApprovalDto) {
        missionService.updateMissionStatusCheckingToProgress(reqMissionApprovalDto.getSuiteRoomId(), reqMissionApprovalDto.getMissionName(), reqMissionApprovalDto.getMemberId());
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }



}
