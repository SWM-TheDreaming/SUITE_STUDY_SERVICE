package com.suite.suite_study_service.attendance.mockEntity;

import com.suite.suite_study_service.attendance.dto.ReqAttendanceCreationDto;
import com.suite.suite_study_service.attendance.entity.Attendance;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Date;

@Getter
@NoArgsConstructor
public class MockAttendance {
    private Long attendanceId;
    private Long suiteRoomId;
    private Long memberId;
    private int round;
    private boolean status;
    private int code;

    @Builder
    public MockAttendance(Long attendanceId, Long suiteRoomId, Long memberId, int round, boolean status, int code) {
        this.attendanceId = attendanceId;
        this.suiteRoomId = suiteRoomId;
        this.memberId = memberId;
        this.round = round;
        this.status = status;
        this.code = code;
    }

    public Attendance toAttendance() {
        return Attendance.builder()
                .attendanceId(attendanceId)
                .suiteRoomId(suiteRoomId)
                .memberId(memberId)
                .round(round)
                .status(status)
                .code(code).build();
    }

    public static ReqAttendanceCreationDto getReqAttendanceCreateionDto() {
        return ReqAttendanceCreationDto.builder()
                .suiteRoomId(1L)
                .attendanceCode(456).build();
    }

}
