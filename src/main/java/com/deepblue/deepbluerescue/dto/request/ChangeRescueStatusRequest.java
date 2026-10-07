package com.deepblue.deepbluerescue.dto.request;

import com.deepblue.deepbluerescue.domain.RescueStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeRescueStatusRequest(
        @NotNull(message = "Status is required")
        RescueStatus status
) {
}

