package com.pet.hospital.controller;

import com.pet.hospital.entity.DoctorSchedule;
import com.pet.hospital.service.DoctorScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor/schedule")
@CrossOrigin(origins = {"http://localhost:5180"})
public class DoctorScheduleController {
    
    private final DoctorScheduleService doctorScheduleService;
    
    public DoctorScheduleController(DoctorScheduleService doctorScheduleService) {
        this.doctorScheduleService = doctorScheduleService;
    }
    
    /**
     * 获取医生排班信息
     * @param doctorId 医生ID
     * @return 排班列表
     */
    @GetMapping("/{doctorId}")
    public ResponseEntity<?> getScheduleByDoctorId(@PathVariable Long doctorId) {
        try {
            List<DoctorSchedule> schedules = doctorScheduleService.getScheduleByDoctorId(doctorId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", schedules);
            response.put("message", "获取排班信息成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取排班信息失败: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 根据日期范围获取医生排班信息
     * @param doctorId 医生ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 排班列表
     */
    @GetMapping("/range/{doctorId}")
    public ResponseEntity<?> getScheduleByDoctorIdAndDateRange(
            @PathVariable Long doctorId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            List<DoctorSchedule> schedules = doctorScheduleService.getScheduleByDoctorIdAndDateRange(doctorId, start, end);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", schedules);
            response.put("message", "获取排班信息成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取排班信息失败: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}