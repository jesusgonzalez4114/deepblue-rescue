package com.deepblue.deepbluerescue.dto.response;

public record TreatmentEligibilityResponse(
        String animalCode,
        boolean eligible
) {
}

