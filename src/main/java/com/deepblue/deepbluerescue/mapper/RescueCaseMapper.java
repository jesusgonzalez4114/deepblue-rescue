package com.deepblue.deepbluerescue.mapper;

import com.deepblue.deepbluerescue.domain.RescueCase;
import com.deepblue.deepbluerescue.dto.response.RescueCaseResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RescueCaseMapper {

    @Mapping(
            target = "centerCode",
            source = "rescueCenter.code"
    )
    @Mapping(
            target = "animalCode",
            source = "animal.animalCode"
    )
    RescueCaseResponse toResponse(
            RescueCase rescueCase
    );
}
