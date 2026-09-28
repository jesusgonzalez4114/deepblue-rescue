package com.deepblue.deepbluerescue.service;

import com.deepblue.deepbluerescue.dto.response.AnimalResponse;

import java.util.List;

public interface AnimalService {

    AnimalResponse findByCode(String animalCode);

    List<AnimalResponse> findAnimalsInRehabilitation();

    boolean canReceiveTreatment(String animalCode);
}