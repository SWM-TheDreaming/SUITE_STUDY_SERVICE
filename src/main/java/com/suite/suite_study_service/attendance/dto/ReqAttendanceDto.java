package com.suite.suite_study_service.attendance.dto;


import com.suite.suite_study_service.attendance.entity.Attendance;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReqAttendanceDto {
    private Long suiteRoomId;
    private Long hostId;
    private int code;

    @Builder
    public ReqAttendanceDto(Long suiteRoomId, Long hostId, int code) {
        this.suiteRoomId = suiteRoomId;
        this.hostId = hostId;
        this.code = code;
    }

    public Attendance toAttendance(Long memberId, int round) {
        return Attendance.builder()
                .memberId(memberId)
                .suiteRoomId(suiteRoomId)
                .status(true)
                .round(round)
                .code(code).build();
    }
}
