package com.hotel.reservations.exception;

import com.hotel.reservations.controller.ReservationController;
import com.hotel.reservations.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@AutoConfigureMockMvc
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @Test
    void shouldHandleValidationError() throws Exception {
        mockMvc.perform(post("/api/hotel/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "name": "",
                            "documentType": "CC",
                            "identification": "1117350206",
                            "dateEntry": "2025-08-01T14:00:00",
                            "departureDate": "2025-08-05T12:00:00",
                            "roomId": "101"
                        }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").exists());
    }

}
