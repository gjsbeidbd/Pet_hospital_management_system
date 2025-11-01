package com.pet.hospital.controller;

import com.pet.hospital.entity.Appointment;
import com.pet.hospital.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = {"http://localhost:5180"})
public class AppointmentController {
    
    @Autowired
    private AppointmentService appointmentService;
    
    /**
     * 根据客户ID获取预约列表
     * @param customerId 客户ID
     * @return 预约列表
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getAppointmentsByCustomerId(@PathVariable Long customerId) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByCustomerId(customerId);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("获取预约列表失败: " + e.getMessage());
        }
    }
}