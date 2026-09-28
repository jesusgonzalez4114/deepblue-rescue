package com.deepblue.deepbluerescue.dto.request;

import com.deepblue.deepbluerescue.domain.TreatmentType;

import java.time.LocalDateTime;

public record CreateTreatmentRequest(

        String animalCode,

        String specialistCode,

        LocalDateTime performedAt,

        TreatmentType type,

        String description

) {
}
