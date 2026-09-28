package com.deepblue.deepbluerescue.dto.request;

import com.deepblue.deepbluerescue.domain.RescueStatus;

public record ChangeRescueStatusRequest(

        RescueStatus status

) {
}
