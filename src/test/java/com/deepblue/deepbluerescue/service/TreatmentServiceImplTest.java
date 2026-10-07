package com.deepblue.deepbluerescue.service;

import com.deepblue.deepbluerescue.domain.Animal;
import com.deepblue.deepbluerescue.domain.AnimalSex;
import com.deepblue.deepbluerescue.domain.RescueCase;
import com.deepblue.deepbluerescue.domain.RescueStatus;
import com.deepblue.deepbluerescue.domain.Specialist;
import com.deepblue.deepbluerescue.domain.Treatment;
import com.deepblue.deepbluerescue.domain.TreatmentType;
import com.deepblue.deepbluerescue.dto.request.CreateTreatmentRequest;
import com.deepblue.deepbluerescue.dto.response.TreatmentResponse;
import com.deepblue.deepbluerescue.exception.BusinessRuleException;
import com.deepblue.deepbluerescue.mapper.TreatmentMapper;
import com.deepblue.deepbluerescue.repository.AnimalRepository;
import com.deepblue.deepbluerescue.repository.SpecialistRepository;
import com.deepblue.deepbluerescue.repository.TreatmentRepository;
import com.deepblue.deepbluerescue.service.impl.TreatmentServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TreatmentServiceImplTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private SpecialistRepository specialistRepository;

    @Mock
    private TreatmentRepository treatmentRepository;

    @Mock
    private TreatmentMapper mapper;

    @InjectMocks
    private TreatmentServiceImpl service;

    @Test
    void shouldRegisterTreatmentWhenValid() {

        RescueCase rescueCase = new RescueCase(
                "RES-100", LocalDate.of(2026, 8, 18), "Bahia Concha", RescueStatus.IN_REHABILITATION);

        Animal animal = new Animal("AN-001", "Green Sea Turtle", "Chelonia mydas", AnimalSex.FEMALE);
        rescueCase.assignAnimal(animal);

        Specialist specialist = new Specialist("SPEC-001", "Elena", "Vargas", "elena@deepblue.org");

        CreateTreatmentRequest request = new CreateTreatmentRequest(
                "AN-001", "SPEC-001",
                LocalDateTime.of(2026, 8, 19, 9, 0),
                TreatmentType.WOUND_CARE,
                "Cleaning of left front flipper"
        );

        Treatment savedTreatment = new Treatment(
                animal, specialist, request.performedAt(), request.type(), request.description());

        TreatmentResponse response = new TreatmentResponse(
                1L, "AN-001", "SPEC-001", request.performedAt(),
                TreatmentType.WOUND_CARE, "Cleaning of left front flipper");

        when(animalRepository.findByAnimalCode("AN-001"))
                .thenReturn(Optional.of(animal));

        when(specialistRepository.findByProfessionalCode("SPEC-001"))
                .thenReturn(Optional.of(specialist));

        when(treatmentRepository.save(any(Treatment.class)))
                .thenReturn(savedTreatment);

        when(mapper.toResponse(savedTreatment))
                .thenReturn(response);

        TreatmentResponse result = service.register(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void shouldThrowWhenSpecialistIsInactive() {

        RescueCase rescueCase = new RescueCase(
                "RES-101", LocalDate.of(2026, 8, 18), "Bahia Concha", RescueStatus.IN_REHABILITATION);

        Animal animal = new Animal("AN-002", "Loggerhead Turtle", "Caretta caretta", AnimalSex.MALE);
        rescueCase.assignAnimal(animal);

        Specialist inactiveSpecialist = new Specialist("SPEC-002", "Mateo", "Rios", "mateo@deepblue.org");
        inactiveSpecialist.setActive(false);

        CreateTreatmentRequest request = new CreateTreatmentRequest(
                "AN-002", "SPEC-002",
                LocalDateTime.of(2026, 8, 19, 9, 0),
                TreatmentType.OBSERVATION,
                "Routine check"
        );

        when(animalRepository.findByAnimalCode("AN-002"))
                .thenReturn(Optional.of(animal));

        when(specialistRepository.findByProfessionalCode("SPEC-002"))
                .thenReturn(Optional.of(inactiveSpecialist));

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(BusinessRuleException.class);

        verify(treatmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCaseIsReleased() {

        RescueCase rescueCase = new RescueCase(
                "RES-102", LocalDate.of(2026, 8, 18), "Bahia Concha", RescueStatus.RELEASED);

        Animal animal = new Animal("AN-001", "Green Sea Turtle", "Chelonia mydas", AnimalSex.FEMALE);
        rescueCase.assignAnimal(animal);

        Specialist specialist = new Specialist("SPEC-001", "Elena", "Vargas", "elena@deepblue.org");

        CreateTreatmentRequest request = new CreateTreatmentRequest(
                "AN-001", "SPEC-001",
                LocalDateTime.of(2026, 8, 19, 9, 0),
                TreatmentType.OBSERVATION,
                "Routine check"
        );

        when(animalRepository.findByAnimalCode("AN-001"))
                .thenReturn(Optional.of(animal));

        when(specialistRepository.findByProfessionalCode("SPEC-001"))
                .thenReturn(Optional.of(specialist));

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(BusinessRuleException.class);
    }
}
