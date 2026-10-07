package com.deepblue.deepbluerescue.mapper;

import com.deepblue.deepbluerescue.domain.Treatment;
import com.deepblue.deepbluerescue.dto.response.TreatmentResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TreatmentMapper {

    @Mapping(
            target = "animalCode",
            source = "animal.animalCode"
    )
    @Mapping(
            target = "specialistCode",
            source = "specialist.professionalCode"
    )
    TreatmentResponse toResponse(
            Treatment treatment
    );
}
