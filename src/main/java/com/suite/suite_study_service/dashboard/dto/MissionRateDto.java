package com.suite.suite_study_service.dashboard.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@RequiredArgsConstructor
public class MissionRateDto {
    private Long memberId;
    private String nickName;
    private Double missionRate;

    @Builder
    public MissionRateDto(Long memberId, Double missionRate) {
        this.memberId = memberId;
        this.missionRate = missionRate;
    }

    public MissionRateDto setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }
}
