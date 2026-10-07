package com.deepblue.deepbluerescue.service;

import com.deepblue.deepbluerescue.domain.Animal;
import com.deepblue.deepbluerescue.domain.AnimalSex;
import com.deepblue.deepbluerescue.domain.RescueCase;
import com.deepblue.deepbluerescue.domain.RescueStatus;
import com.deepblue.deepbluerescue.repository.AnimalRepository;
import com.deepblue.deepbluerescue.service.impl.AnimalServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnimalServiceImplTest {

    @Mock
    private AnimalRepository repository;

    @Mock
    private com.deepblue.deepbluerescue.mapper.AnimalMapper mapper;

    @InjectMocks
    private AnimalServiceImpl service;

    @Test
    void shouldReturnTrueWhenAnimalCanReceiveTreatment() {

        RescueCase rescueCase = new RescueCase(
                "RES-200", LocalDate.now(), "Bahia Concha", RescueStatus.IN_REHABILITATION);

        Animal animal = new Animal("AN-200", "Turtle", null, AnimalSex.FEMALE);
        rescueCase.assignAnimal(animal);

        when(repository.findByAnimalCode("AN-200"))
                .thenReturn(Optional.of(animal));

        boolean result = service.canReceiveTreatment("AN-200");

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenAnimalCaseIsReleased() {

        RescueCase rescueCase = new RescueCase(
                "RES-201", LocalDate.now(), "Bahia Concha", RescueStatus.RELEASED);

        Animal animal = new Animal("AN-201", "Turtle", null, AnimalSex.MALE);
        rescueCase.assignAnimal(animal);

        when(repository.findByAnimalCode("AN-201"))
                .thenReturn(Optional.of(animal));

        boolean result = service.canReceiveTreatment("AN-201");

        assertThat(result).isFalse();
    }
}