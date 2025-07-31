package com.hotel.reservations.controller;

import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.service.ReservationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/hotel/reservation")
public class WebReservationController {

    private final ReservationService reservationService;

    public WebReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/form")
    public String showForm(Model model){
        model.addAttribute("reservation", new Reservation());
        return "reservation_form";
    }

    @PostMapping("/web")
    public String processReservation(@ModelAttribute("reservation") Reservation reservation, Model model){
        try {
            reservationService.createReservation(reservation);
            model.addAttribute("message","Reservation created successfully ✅");
        }catch (Exception e){
            model.addAttribute("message","Error to create reservation ✖️" + e.getMessage());
        }
        return "reservation_form";
    }

}
