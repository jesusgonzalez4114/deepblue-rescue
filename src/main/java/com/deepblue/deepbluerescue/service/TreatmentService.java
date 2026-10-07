package com.deepblue.deepbluerescue.service;

import com.deepblue.deepbluerescue.dto.request.CreateTreatmentRequest;
import com.deepblue.deepbluerescue.dto.response.TreatmentResponse;

import java.util.List;

public interface TreatmentService {

    TreatmentResponse register(
            CreateTreatmentRequest request
    );

    List<TreatmentResponse> findByAnimalCode(
            String animalCode
    );
}