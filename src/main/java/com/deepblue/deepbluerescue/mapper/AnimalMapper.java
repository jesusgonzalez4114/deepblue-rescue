package com.deepblue.deepbluerescue.mapper;

import com.deepblue.deepbluerescue.domain.Animal;
import com.deepblue.deepbluerescue.dto.response.AnimalResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnimalMapper {

    @Mapping(
            target = "caseCode",
            source = "rescueCase.caseCode"
    )
    @Mapping(
            target = "rescueStatus",
            source = "rescueCase.status"
    )
    AnimalResponse toResponse(Animal animal);
}