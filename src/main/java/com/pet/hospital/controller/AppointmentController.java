package com.pet.hospital.controller;

import com.pet.hospital.entity.Appointment;
import com.pet.hospital.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = {"http://localhost:5180"})
public class AppointmentController {
    
    @Autowired
    private AppointmentService appointmentService;
    
    /**
     * 获取所有预约列表
     * @return 预约列表
     */
    @GetMapping
    public ResponseEntity<?> getAllAppointments() {
        try {
            List<Appointment> appointments = appointmentService.list();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", appointments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取预约列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 根据客户ID获取预约列表
     * @param customerId 客户ID
     * @return 预约列表
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getAppointmentsByCustomerId(@PathVariable Long customerId) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByCustomerId(customerId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", appointments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取预约列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 根据医生ID获取预约列表
     * @param doctorId 医生ID
     * @return 预约列表
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getAppointmentsByDoctorId(@PathVariable Long doctorId) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByDoctorId(doctorId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", appointments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取预约列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 创建预约（带冲突检测和验证）
     * @param appointment 预约信息
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody Appointment appointment) {
        try {
            // 设置默认状态
            if (appointment.getStatus() == null || appointment.getStatus().isEmpty()) {
                appointment.setStatus("PENDING");
            }
            
            // 保存预约
            boolean save = appointmentService.save(appointment);
            if (save) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "预约创建成功");
                response.put("data", appointment);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "预约创建失败");
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建预约失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 取消预约
     * @param id 预约ID
     * @return 取消结果
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.getById(id);
            if (appointment != null) {
                appointment.setStatus("CANCELLED");
                boolean update = appointmentService.updateById(appointment);
                if (update) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "预约取消成功");
                    return ResponseEntity.ok(response);
                } else {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "预约取消失败");
                    return ResponseEntity.status(500).body(response);
                }
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "预约不存在");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "取消预约失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 确认预约（取号后）
     * @param id 预约ID
     * @return 确认结果
     */
    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirmAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.getById(id);
            if (appointment != null) {
                appointment.setStatus("CONFIRMED"); // 待就诊
                boolean update = appointmentService.updateById(appointment);
                if (update) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "预约确认成功");
                    return ResponseEntity.ok(response);
                } else {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "预约确认失败");
                    return ResponseEntity.status(500).body(response);
                }
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "预约不存在");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "确认预约失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 开始就诊
     * @param id 预约ID
     * @return 结果
     */
    @PutMapping("/{id}/start")
    public ResponseEntity<?> startAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.getById(id);
            if (appointment != null) {
                appointment.setStatus("IN_PROGRESS"); // 就诊中
                boolean update = appointmentService.updateById(appointment);
                if (update) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "开始就诊");
                    return ResponseEntity.ok(response);
                } else {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "状态更新失败");
                    return ResponseEntity.status(500).body(response);
                }
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "预约不存在");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 完成就诊
     * @param id 预约ID
     * @return 结果
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.getById(id);
            if (appointment != null) {
                appointment.setStatus("COMPLETED"); // 已就诊
                boolean update = appointmentService.updateById(appointment);
                if (update) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "就诊完成");
                    return ResponseEntity.ok(response);
                } else {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "状态更新失败");
                    return ResponseEntity.status(500).body(response);
                }
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "预约不存在");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取待确认的预约列表
     * @return 预约列表
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingAppointments() {
        try {
            List<Appointment> appointments = appointmentService.getPendingAppointments();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", appointments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取待确认预约列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 获取今日预约列表
     * @return 预约列表
     */
    /**
     * 获取今日预约列表
     * @return 预约列表
     */
    @GetMapping("/today")
    public ResponseEntity<?> getTodayAppointments() {
        try {
            List<Appointment> appointments = appointmentService.getTodayAppointments();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", appointments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取今日预约列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}