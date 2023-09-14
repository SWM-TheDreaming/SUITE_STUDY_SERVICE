package com.suite.suite_study_service.mission.service;

import com.suite.suite_study_service.common.handler.CustomException;
import com.suite.suite_study_service.common.handler.StatusCode;
import com.suite.suite_study_service.common.security.dto.AuthorizerDto;
import com.suite.suite_study_service.dashboard.entity.DashBoard;
import com.suite.suite_study_service.dashboard.repository.DashBoardRepository;
import com.suite.suite_study_service.mission.dto.MissionType;
import com.suite.suite_study_service.mission.dto.ReqMissionDto;
import com.suite.suite_study_service.mission.entity.Mission;
import com.suite.suite_study_service.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.suite.suite_study_service.common.security.JwtInfoExtractor.getSuiteAuthorizer;

@Service
@RequiredArgsConstructor
public class MissionServiceImpl implements MissionService{

    private final MissionRepository missionRepository;
    private final DashBoardRepository dashBoardRepository;

    @Override
    @Transactional
    public void createMission(ReqMissionDto reqMissionDto) {
        AuthorizerDto missionCreateAttempter = getSuiteAuthorizer();

        DashBoard isInDashBoard = dashBoardRepository.findByMemberId(missionCreateAttempter.getMemberId())
                .orElseThrow(() -> new CustomException(StatusCode.IS_NOT_PARTICIPATED));
        if(!isInDashBoard.isHost()) throw new CustomException(StatusCode.FORBIDDEN);


        dashBoardRepository.findAllBySuiteRoomId(reqMissionDto.getSuiteRoomId())
                .stream()
                .forEach(dashBoard -> {
                    Mission mission = reqMissionDto.toMission(dashBoard.getMemberId());
                    missionRepository.save(mission);
                });
    }

    @Override
    public List<Mission> listUpMission(Long suiteRoomId,String missionTypeString) {
        try {
            return missionRepository.findAllBySuiteRoomIdAndMissionStatus(suiteRoomId, MissionType.valueOf(missionTypeString));
        } catch (Exception exception) {
            throw new CustomException(StatusCode.NOT_FOUND);
        }

    }
}
