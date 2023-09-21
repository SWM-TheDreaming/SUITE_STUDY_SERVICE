package com.suite.suite_study_service.common.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum StatusCode {
    OK(200, "OK", HttpStatus.OK),
    ALREADY_EXISTS_MISSION(400, "이미 생성됐던 미션 이름입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_EXISTS_MISSION_REQUEST(400, "미션에 대한 요청 혹은 완료 건이 존재하여 삭제할 수 없습니다.", HttpStatus.BAD_REQUEST),
    USERNAME_OR_PASSWORD_NOT_FOUND (400, "아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_FOUND (400, "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    FORBIDDEN(403, "해당 요청에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),
    UNAUTHORIZED (400, "로그인 후 이용가능합니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_JWT(400, "기존 토큰이 만료되었습니다. 해당 토큰을 가지고 /token/refresh 링크로 이동 후 토큰을 재발급 받으세요.", HttpStatus.UNAUTHORIZED),
    NOT_FOUND(404, "일치하는 정보가 없습니다.", HttpStatus.NOT_FOUND),
    ALREADY_ATTENDANCE(400, "이미 출석하였습니다.", HttpStatus.BAD_REQUEST),
    INVALID_ATTENDANCE_CODE(401, "출석 번호가 올바르지 않습니다", HttpStatus.UNAUTHORIZED),
    TIMEOUT_ATTENDANCE(408, "출석 시간이 아닙니다.", HttpStatus.REQUEST_TIMEOUT),
    CREATE_ATTENDANCE_ERROR(400, "출석은 5분에 한번씩 만들 수 있습니다.", HttpStatus.BAD_REQUEST)
    ;
    @Getter
    private int statusCode;
    @Getter
    private String message;
    @Getter
    private HttpStatus status;

    StatusCode(int statusCode, String message, HttpStatus status) {
        this.statusCode = statusCode;
        this.message = message;
        this.status = status;
    }

    public String toString() {
        return "{" +
                "\"code\" : " + "\""+ statusCode +"\"" +
                "\"status\" : " + "\""+status+"\"" +
                "\"message\" : " + "\""+message+"\"" +
                "}";
    }
}
