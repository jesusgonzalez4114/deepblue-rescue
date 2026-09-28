package com.deepblue.deepbluerescue.dto.response;

import com.deepblue.deepbluerescue.domain.AnimalSex;
import com.deepblue.deepbluerescue.domain.RescueStatus;

public record AnimalResponse(

        Long id,

        String animalCode,

        String commonName,

        String scientificName,

        AnimalSex sex,

        String caseCode,

        RescueStatus rescueStatus

) {
}