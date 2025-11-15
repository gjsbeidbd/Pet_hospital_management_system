package com.pet.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pet.hospital.entity.Doctor;
import com.pet.hospital.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = {"http://localhost:5180"})
public class DoctorController {
    
    @Autowired
    private DoctorService doctorService;
    
    /**
     * 获取所有医生列表
     * @return 医生列表
     */
    @GetMapping
    public ResponseEntity<?> getAllDoctors() {
        try {
            List<Doctor> doctors = doctorService.list();
            return ResponseEntity.ok(doctors);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取医生列表失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取在职医生列表（用于用户挂号）
     * @return 在职医生列表
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveDoctors() {
        try {
            QueryWrapper<Doctor> wrapper = new QueryWrapper<>();
            wrapper.eq("status", 1); // 只查询在职医生
            List<Doctor> doctors = doctorService.list(wrapper);
            return ResponseEntity.ok(doctors);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取在职医生列表失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 根据ID获取医生信息
     * @param id 医生ID
     * @return 医生信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable Long id) {
        try {
            Doctor doctor = doctorService.getById(id);
            if (doctor != null) {
                return ResponseEntity.ok(doctor);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("医生不存在");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("获取医生信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建医生
     * @param doctor 医生信息
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<?> createDoctor(@RequestBody Doctor doctor) {
        try {
            boolean save = doctorService.save(doctor);
            if (save) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "创建医生成功");
                response.put("data", doctor);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("创建医生失败");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("创建医生失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新医生信息
     * @param id 医生ID
     * @param doctor 医生信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctor) {
        try {
            doctor.setId(id);
            // 如果密码为空，则不更新密码
            if (doctor.getPassword() == null || doctor.getPassword().isEmpty()) {
                Doctor existing = doctorService.getById(id);
                if (existing != null) {
                    doctor.setPassword(existing.getPassword());
                }
            }
            boolean update = doctorService.updateById(doctor);
            if (update) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "更新医生信息成功");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("更新医生信息失败");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("更新医生信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除医生
     * @param id 医生ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long id) {
        try {
            boolean delete = doctorService.removeById(id);
            if (delete) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "删除医生成功");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("医生不存在");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("删除医生失败：" + e.getMessage());
        }
    }
}