package com.pet.hospital.controller;

import com.pet.hospital.entity.MedicalRecord;
import com.pet.hospital.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    /**
     * 创建病例记录
     */
    @PostMapping
    public ResponseEntity<?> createMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        try {
            boolean save = medicalRecordService.save(medicalRecord);
            if (save) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "病例保存成功");
                response.put("data", medicalRecord);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "病例保存失败");
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建病例失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}