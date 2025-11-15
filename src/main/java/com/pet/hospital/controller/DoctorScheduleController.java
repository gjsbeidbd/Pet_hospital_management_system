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
@RequestMapping("/api/schedules")
@CrossOrigin(origins = {"http://localhost:5180"})
public class DoctorScheduleController {
    
    private final DoctorScheduleService doctorScheduleService;
    
    public DoctorScheduleController(DoctorScheduleService doctorScheduleService) {
        this.doctorScheduleService = doctorScheduleService;
    }
    
    /**
     * 获取所有排班或根据条件筛选
     * @param doctorId 医生ID（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 排班列表
     */
    @GetMapping
    public ResponseEntity<?> getSchedules(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            List<DoctorSchedule> schedules;
            
            if (doctorId != null && startDate != null && endDate != null) {
                LocalDate start = LocalDate.parse(startDate);
                LocalDate end = LocalDate.parse(endDate);
                schedules = doctorScheduleService.getScheduleByDoctorIdAndDateRange(doctorId, start, end);
            } else if (doctorId != null) {
                schedules = doctorScheduleService.getScheduleByDoctorId(doctorId);
            } else if (startDate != null && endDate != null) {
                LocalDate start = LocalDate.parse(startDate);
                LocalDate end = LocalDate.parse(endDate);
                schedules = doctorScheduleService.getScheduleByDateRange(start, end);
            } else {
                schedules = doctorScheduleService.list();
            }
            
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取排班信息失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 根据ID获取排班信息
     * @param id 排班ID
     * @return 排班信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getScheduleById(@PathVariable Long id) {
        try {
            DoctorSchedule schedule = doctorScheduleService.getById(id);
            if (schedule != null) {
                return ResponseEntity.ok(schedule);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "排班不存在");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取排班信息失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取医生排班信息
     * @param doctorId 医生ID
     * @return 排班列表
     */
    @GetMapping("/doctor/{doctorId}")
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
    
    /**
     * 创建排班
     * @param schedule 排班信息
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<?> createSchedule(@RequestBody DoctorSchedule schedule) {
        try {
            boolean save = doctorScheduleService.save(schedule);
            if (save) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "排班创建成功");
                response.put("data", schedule);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "排班创建失败");
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建排班失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 更新排班
     * @param id 排班ID
     * @param schedule 排班信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSchedule(@PathVariable Long id, @RequestBody DoctorSchedule schedule) {
        try {
            schedule.setId(id);
            boolean update = doctorScheduleService.updateById(schedule);
            if (update) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "排班更新成功");
                response.put("data", schedule);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "排班不存在");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新排班失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 删除排班
     * @param id 排班ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        try {
            boolean remove = doctorScheduleService.removeById(id);
            if (remove) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "排班删除成功");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "排班不存在");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "删除排班失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}