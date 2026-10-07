package com.deepblue.deepbluerescue.controller;



import com.deepblue.deepbluerescue.domain.AnimalSex;
import com.deepblue.deepbluerescue.domain.RescueStatus;
import com.deepblue.deepbluerescue.domain.TreatmentType;
import com.deepblue.deepbluerescue.dto.response.AnimalResponse;
import com.deepblue.deepbluerescue.dto.response.TreatmentResponse;
import com.deepblue.deepbluerescue.exception.GlobalExceptionHandler;
import com.deepblue.deepbluerescue.exception.ResourceNotFoundException;
import com.deepblue.deepbluerescue.service.AnimalService;
import com.deepblue.deepbluerescue.service.TreatmentService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnimalController.class)
@Import(GlobalExceptionHandler.class)
class AnimalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnimalService animalService;

    @MockitoBean
    private TreatmentService treatmentService;

    // IMPORTANTE: abre tu AnimalResponse.java y ajusta este metodo a sus campos reales.
    private AnimalResponse animal(String code) {
        return new AnimalResponse(
                1L,                                // id
                code,                              // animalCode
                "Green Sea Turtle",                // commonName
                "Chelonia mydas",                  // scientificName
                AnimalSex.values()[0],             // sex
                "RES-2026-001",                    // caseCode
                RescueStatus.IN_REHABILITATION     // rescueStatus
        );
    }

    // Si tu TreatmentResponse tiene otros campos, ajusta este metodo.
    private TreatmentResponse treatment(Long id) {
        return new TreatmentResponse(
                id,
                "AN-001",
                "SPEC-001",
                LocalDateTime.of(2026, 8, 21, 9, 0),
                TreatmentType.WOUND_CARE,
                "Cleaning and treatment of flipper injury."
        );
    }

    // GET animal por codigo -> 200
    @Test
    void shouldReturnAnimalByCode() throws Exception {
        when(animalService.findByCode("AN-001")).thenReturn(animal("AN-001"));

        mockMvc.perform(get("/api/animals/{code}", "AN-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.animalCode").value("AN-001"));

        verify(animalService).findByCode("AN-001");
    }

    // GET animal inexistente -> 404 + ErrorResponse
    @Test
    void shouldReturn404WhenAnimalDoesNotExist() throws Exception {
        when(animalService.findByCode("AN-999"))
                .thenThrow(new ResourceNotFoundException("Animal not found: AN-999"));

        mockMvc.perform(get("/api/animals/{code}", "AN-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Animal not found: AN-999"))
                .andExpect(jsonPath("$.details").isMap());
    }

    // GET animales en rehabilitacion -> 200 con 2 elementos
    @Test
    void shouldReturnAnimalsInRehabilitation() throws Exception {
        when(animalService.findAnimalsInRehabilitation())
                .thenReturn(List.of(animal("AN-001"), animal("AN-002")));

        mockMvc.perform(get("/api/animals/in-rehabilitation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(animalService).findAnimalsInRehabilitation();
    }

    // GET tratamientos del animal -> 200
    @Test
    void shouldReturnAnimalTreatments() throws Exception {
        when(treatmentService.findByAnimalCode("AN-001"))
                .thenReturn(List.of(treatment(100L), treatment(101L)));

        mockMvc.perform(get("/api/animals/{code}/treatments", "AN-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].animalCode").value("AN-001"));

        verify(treatmentService).findByAnimalCode("AN-001");
    }

    // GET elegibilidad -> 200
    @Test
    void shouldReturnTreatmentEligibility() throws Exception {
        when(animalService.canReceiveTreatment("AN-001")).thenReturn(true);

        mockMvc.perform(get("/api/animals/{code}/treatment-eligibility", "AN-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.animalCode").value("AN-001"))
                .andExpect(jsonPath("$.eligible").value(true));

        verify(animalService).canReceiveTreatment("AN-001");
    }

    // GET elegibilidad con animal inexistente -> 404
    @Test
    void shouldReturn404WhenEligibilityAnimalDoesNotExist() throws Exception {
        when(animalService.canReceiveTreatment("AN-999"))
                .thenThrow(new ResourceNotFoundException("Animal not found: AN-999"));

        mockMvc.perform(get("/api/animals/{code}/treatment-eligibility", "AN-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Animal not found: AN-999"));

        verify(animalService).canReceiveTreatment("AN-999");
    }

    // Error inesperado -> 500 (caso 18)
    @Test
    void shouldReturn500OnUnexpectedError() throws Exception {
        when(animalService.findByCode("AN-001"))
                .thenThrow(new RuntimeException("Database connection lost"));

        mockMvc.perform(get("/api/animals/{code}", "AN-001"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.details").isMap());
    }
}
