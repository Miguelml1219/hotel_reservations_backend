package com.hotel.reservations.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReservationRequestDTO {

    @NotBlank(message = "The name is mandatory")
    @Pattern(regexp = "^[a-zA-Z ]{1,30}$", message = "The name must be letters only and maximum 30 characters")
    private String name;

    @NotBlank(message = "ID is required")
    @Pattern(regexp = "^\\d{1,12}$", message = "ID must be numeric and maximum 12 digits")
    private String identification;

    @NotBlank(message = "The document type is mandatory")
    @Pattern(regexp = "^(CC|TI|CE|PASSPORT)$", message = "Invalid document type")
    private String documentType;

    @NotNull(message = "The date of entry is mandatory")
    private String dateEntry;

    @NotNull(message = "The departure date is mandatory")
    private String departureDate;

    @NotBlank(message = "The room ID is mandatory")
    private String roomId;
}
