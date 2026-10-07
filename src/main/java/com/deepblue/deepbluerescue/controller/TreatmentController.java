package com.deepblue.deepbluerescue.controller;


import com.deepblue.deepbluerescue.dto.request.CreateTreatmentRequest;
import com.deepblue.deepbluerescue.dto.response.TreatmentResponse;
import com.deepblue.deepbluerescue.service.TreatmentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/treatments")
public class TreatmentController {

    private final TreatmentService service;

    public TreatmentController(TreatmentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TreatmentResponse> register(
            @Valid @RequestBody CreateTreatmentRequest request) {
        TreatmentResponse response = service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}