package com.suite.suite_study_service.attendance.mockEntity;

import com.suite.suite_study_service.attendance.dto.ReqAttendanceCreationDto;
import com.suite.suite_study_service.attendance.entity.Attendance;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class MockAttendance {
    private Long attendanceId;
    private Long suiteRoomId;
    private Long memberId;
    private int round;
    private boolean status;
    private int code;
    private Timestamp attendanceTime;

    @Builder
    public MockAttendance(Long attendanceId, Long suiteRoomId, Long memberId, int round, boolean status, int code, String attendanceTime) {
        this.attendanceId = attendanceId;
        this.suiteRoomId = suiteRoomId;
        this.memberId = memberId;
        this.round = round;
        this.status = status;
        this.code = code;
        this.attendanceTime = getTimeStamp(attendanceTime);
    }

    public Attendance toAttendance() {
        return Attendance.builder()
                .attendanceId(attendanceId)
                .suiteRoomId(suiteRoomId)
                .memberId(memberId)
                .round(round)
                .status(status)
                .code(code)
                .attendanceTime(attendanceTime).build();
    }

    public static ReqAttendanceCreationDto getReqAttendanceCreateionDto() {
        return ReqAttendanceCreationDto.builder()
                .suiteRoomId(1L)
                .attendanceCode(456).build();
    }

    private static Timestamp getTimeStamp(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(time, formatter);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Seoul"));
        return Timestamp.from(zonedDateTime.toInstant());
    }
}
