package com.suite.suite_study_service.attendance.controller;

import com.suite.suite_study_service.attendance.dto.ReqAttendanceCreationDto;
import com.suite.suite_study_service.attendance.dto.ReqAttendanceDto;
import com.suite.suite_study_service.attendance.service.AttendanceService;
import com.suite.suite_study_service.common.dto.Message;
import com.suite.suite_study_service.common.handler.StatusCode;
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
@RequestMapping("/study/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping("/registration")
    public ResponseEntity<Message> createAttendance(@RequestBody ReqAttendanceCreationDto reqAttendanceCreationDto) {
        attendanceService.createAttendanceHost(reqAttendanceCreationDto, getSuiteAuthorizer().getMemberId());
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }

    @PostMapping("/")
    public ResponseEntity<Message> registerAttendance(@RequestBody ReqAttendanceDto reqAttendanceDto) {
        attendanceService.registerAttendanceGuest(reqAttendanceDto, getSuiteAuthorizer().getMemberId());
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }

    @PostMapping("/board")
    public ResponseEntity<Message> lookUpListMyAttendanceBoard(@RequestBody Map<String, Long> suite) {
        return ResponseEntity.ok(new Message(StatusCode.OK, attendanceService.getAttendanceBoard(suite.get("suiteRoomId"), getSuiteAuthorizer().getMemberId())));
    }
}
