package com.pet.hospital.controller;

import com.pet.hospital.entity.MedicalRecord;
import com.pet.hospital.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@CrossOrigin(origins = {"http://localhost:5180"})
public class MedicalRecordController {
    
    @Autowired
    private MedicalRecordService medicalRecordService;
    
    /**
     * 根据宠物ID获取病例记录
     * @param petId 宠物ID
     * @return 病例记录列表
     */
    @GetMapping("/pet/{petId}")
    public ResponseEntity<?> getPetMedicalRecords(@PathVariable Long petId) {
        try {
            List<MedicalRecord> records = medicalRecordService.getPetMedicalRecords(petId);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("获取病例记录失败: " + e.getMessage());
        }
    }
}