package com.example.booking.api.dto;

import jakarta.validation.constraints.NotNull;

public record ReservationRequest(@NotNull Long slotId) {}
