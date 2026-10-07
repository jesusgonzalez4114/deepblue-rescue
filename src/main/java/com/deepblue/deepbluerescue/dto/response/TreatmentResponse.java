package com.deepblue.deepbluerescue.dto.response;

import com.deepblue.deepbluerescue.domain.TreatmentType;

import java.time.LocalDateTime;

public record TreatmentResponse(

        Long id,

        String animalCode,

        String specialistCode,

        LocalDateTime performedAt,

        TreatmentType type,

        String description

) {
}
