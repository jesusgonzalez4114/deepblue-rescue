package com.deepblue.deepbluerescue.service;

import com.deepblue.deepbluerescue.domain.RescueStatus;
import com.deepblue.deepbluerescue.dto.request.ChangeRescueStatusRequest;
import com.deepblue.deepbluerescue.dto.response.RescueCaseResponse;

import java.util.List;

public interface RescueCaseService {

    RescueCaseResponse findByCode(
            String caseCode
    );

    List<RescueCaseResponse> findByStatus(
            RescueStatus status
    );

    RescueCaseResponse changeStatus(
            String caseCode,
            ChangeRescueStatusRequest request
    );
}
