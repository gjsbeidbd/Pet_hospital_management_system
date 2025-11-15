package com.pet.hospital.controller;

import com.pet.hospital.entity.Prescription;
import com.pet.hospital.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = {"http://localhost:5180"})
public class PrescriptionController {
    
    @Autowired
    private PrescriptionService prescriptionService;
    
    /**
     * 创建处方
     */
    @PostMapping
    public ResponseEntity<?> createPrescription(@RequestBody Prescription prescription) {
        try {
            boolean save = prescriptionService.save(prescription);
            if (save) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "处方创建成功");
                response.put("data", prescription);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "处方创建失败");
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建处方失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
