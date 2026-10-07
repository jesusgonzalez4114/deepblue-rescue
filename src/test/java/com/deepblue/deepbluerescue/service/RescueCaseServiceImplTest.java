package com.deepblue.deepbluerescue.service;

import com.deepblue.deepbluerescue.domain.RescueCase;
import com.deepblue.deepbluerescue.domain.RescueStatus;
import com.deepblue.deepbluerescue.dto.request.ChangeRescueStatusRequest;
import com.deepblue.deepbluerescue.dto.response.RescueCaseResponse;
import com.deepblue.deepbluerescue.exception.BusinessRuleException;
import com.deepblue.deepbluerescue.exception.ResourceNotFoundException;
import com.deepblue.deepbluerescue.mapper.RescueCaseMapper;
import com.deepblue.deepbluerescue.repository.RescueCaseRepository;
import com.deepblue.deepbluerescue.service.impl.RescueCaseServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RescueCaseServiceImplTest {

    @Mock
    private RescueCaseRepository repository;

    @Mock
    private RescueCaseMapper mapper;

    @InjectMocks
    private RescueCaseServiceImpl service;

    @Test
    void shouldFindRescueCaseByCode() {

        RescueCase rescueCase = new RescueCase(
                "RES-001", LocalDate.now(), "Bahia Concha", RescueStatus.IN_REHABILITATION);

        RescueCaseResponse response = new RescueCaseResponse(
                1L, "RES-001", LocalDate.now(), "Bahia Concha",
                RescueStatus.IN_REHABILITATION, "DB-CAR", null);

        when(repository.findByCaseCode("RES-001"))
                .thenReturn(Optional.of(rescueCase));

        when(mapper.toResponse(rescueCase))
                .thenReturn(response);

        RescueCaseResponse result = service.findByCode("RES-001");

        assertThat(result).isEqualTo(response);

        verify(repository).findByCaseCode("RES-001");
        verify(mapper).toResponse(rescueCase);
    }

    @Test
    void shouldThrowWhenRescueCaseNotFound() {

        when(repository.findByCaseCode("RES-999"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByCode("RES-999"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(mapper, never()).toResponse(any());
    }

    @Test
    void shouldChangeStatusWhenTransitionIsValid() {

        RescueCase rescueCase = new RescueCase(
                "RES-002", LocalDate.now(), "Taganga", RescueStatus.ADMITTED);

        ChangeRescueStatusRequest request =
                new ChangeRescueStatusRequest(RescueStatus.UNDER_EVALUATION);

        RescueCaseResponse response = new RescueCaseResponse(
                2L, "RES-002", LocalDate.now(), "Taganga",
                RescueStatus.UNDER_EVALUATION, "DB-CAR", null);

        when(repository.findByCaseCode("RES-002"))
                .thenReturn(Optional.of(rescueCase));

        when(repository.save(rescueCase))
                .thenReturn(rescueCase);

        when(mapper.toResponse(rescueCase))
                .thenReturn(response);

        RescueCaseResponse result = service.changeStatus("RES-002", request);

        assertThat(result).isEqualTo(response);
        assertThat(rescueCase.getStatus()).isEqualTo(RescueStatus.UNDER_EVALUATION);

        verify(repository).save(rescueCase);
    }

    @Test
    void shouldThrowWhenTransitionIsInvalid() {

        RescueCase rescueCase = new RescueCase(
                "RES-003", LocalDate.now(), "Taganga", RescueStatus.ADMITTED);

        ChangeRescueStatusRequest request =
                new ChangeRescueStatusRequest(RescueStatus.READY_FOR_RELEASE);

        when(repository.findByCaseCode("RES-003"))
                .thenReturn(Optional.of(rescueCase));

        assertThatThrownBy(() -> service.changeStatus("RES-003", request))
                .isInstanceOf(BusinessRuleException.class);

        verify(repository, never()).save(any());
    }
}