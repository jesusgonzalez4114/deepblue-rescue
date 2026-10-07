package com.deepblue.deepbluerescue.service.impl;

import com.deepblue.deepbluerescue.domain.Animal;
import com.deepblue.deepbluerescue.domain.RescueCase;
import com.deepblue.deepbluerescue.domain.RescueStatus;
import com.deepblue.deepbluerescue.domain.Specialist;
import com.deepblue.deepbluerescue.domain.Treatment;
import com.deepblue.deepbluerescue.dto.request.CreateTreatmentRequest;
import com.deepblue.deepbluerescue.dto.response.TreatmentResponse;
import com.deepblue.deepbluerescue.exception.BusinessRuleException;
import com.deepblue.deepbluerescue.exception.ResourceNotFoundException;
import com.deepblue.deepbluerescue.mapper.TreatmentMapper;
import com.deepblue.deepbluerescue.repository.AnimalRepository;
import com.deepblue.deepbluerescue.repository.SpecialistRepository;
import com.deepblue.deepbluerescue.repository.TreatmentRepository;
import com.deepblue.deepbluerescue.service.TreatmentService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TreatmentServiceImpl implements TreatmentService {

    private final AnimalRepository animalRepository;
    private final SpecialistRepository specialistRepository;
    private final TreatmentRepository treatmentRepository;
    private final TreatmentMapper mapper;

    public TreatmentServiceImpl(
            AnimalRepository animalRepository,
            SpecialistRepository specialistRepository,
            TreatmentRepository treatmentRepository,
            TreatmentMapper mapper) {

        this.animalRepository = animalRepository;
        this.specialistRepository = specialistRepository;
        this.treatmentRepository = treatmentRepository;
        this.mapper = mapper;
    }

    @Override
    public List<TreatmentResponse> findByAnimalCode(String animalCode) {

        return treatmentRepository
                .findByAnimalAnimalCodeOrderByPerformedAtAsc(animalCode)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TreatmentResponse register(CreateTreatmentRequest request) {

        Animal animal = animalRepository
                .findByAnimalCode(request.animalCode())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Animal not found: " + request.animalCode()
                        )
                );

        Specialist specialist = specialistRepository
                .findByProfessionalCode(request.specialistCode())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Specialist not found: " + request.specialistCode()
                        )
                );

        if (!specialist.isActive()) {
            throw new BusinessRuleException(
                    "Specialist " + request.specialistCode() + " is not active"
            );
        }

        RescueCase rescueCase = animal.getRescueCase();
        RescueStatus status = rescueCase.getStatus();

        if (status == RescueStatus.RELEASED || status == RescueStatus.CLOSED) {
            throw new BusinessRuleException(
                    "Cannot register treatment because the animal's case is " + status
            );
        }

        if (request.performedAt().toLocalDate().isBefore(rescueCase.getRescueDate())) {
            throw new BusinessRuleException(
                    "Treatment date cannot be before the rescue date"
            );
        }

        Treatment treatment = new Treatment(
                animal,
                specialist,
                request.performedAt(),
                request.type(),
                request.description()
        );

        Treatment saved = treatmentRepository.save(treatment);

        return mapper.toResponse(saved);
    }
}