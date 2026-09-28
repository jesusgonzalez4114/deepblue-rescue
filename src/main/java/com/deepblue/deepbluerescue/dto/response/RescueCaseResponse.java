package com.deepblue.deepbluerescue.dto.response;

import com.deepblue.deepbluerescue.domain.RescueStatus;

import java.time.LocalDate;

public record RescueCaseResponse(

        Long id,

        String caseCode,

        LocalDate rescueDate,

        String rescueLocation,

        RescueStatus status,

        String centerCode,

        String animalCode

) {
}

