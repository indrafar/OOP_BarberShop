package com.barbershop.barbershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppointmentTimeController {
    @GetMapping("/appointment-time")
    public String appointmentTimePage() {
        return "appointment-time";
    }
} 