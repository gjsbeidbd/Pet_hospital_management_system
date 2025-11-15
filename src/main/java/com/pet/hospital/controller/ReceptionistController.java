package com.pet.hospital.controller;

import com.pet.hospital.entity.Receptionist;
import com.pet.hospital.service.ReceptionistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/receptionists")
@CrossOrigin(origins = {"http://localhost:5180"})
public class ReceptionistController {
    
    @Autowired
    private ReceptionistService receptionistService;
    
    /**
     * 获取所有前台列表
     * @return 前台列表
     */
    @GetMapping
    public ResponseEntity<?> getAllReceptionists() {
        try {
            List<Receptionist> receptionists = receptionistService.list();
            return ResponseEntity.ok(receptionists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("获取前台列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取前台信息
     * @param id 前台ID
     * @return 前台信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getReceptionistById(@PathVariable Long id) {
        try {
            Receptionist receptionist = receptionistService.getById(id);
            if (receptionist != null) {
                return ResponseEntity.ok(receptionist);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("前台不存在");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("获取前台信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建前台
     * @param receptionist 前台信息
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<?> createReceptionist(@RequestBody Receptionist receptionist) {
        try {
            boolean save = receptionistService.save(receptionist);
            if (save) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "创建前台成功");
                response.put("data", receptionist);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("创建前台失败");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("创建前台失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新前台信息
     * @param id 前台ID
     * @param receptionist 前台信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReceptionist(@PathVariable Long id, @RequestBody Receptionist receptionist) {
        try {
            receptionist.setId(id);
            // 如果密码为空，则不更新密码
            if (receptionist.getPassword() == null || receptionist.getPassword().isEmpty()) {
                Receptionist existing = receptionistService.getById(id);
                if (existing != null) {
                    receptionist.setPassword(existing.getPassword());
                }
            }
            boolean update = receptionistService.updateById(receptionist);
            if (update) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "更新前台信息成功");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("更新前台信息失败");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("更新前台信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除前台
     * @param id 前台ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReceptionist(@PathVariable Long id) {
        try {
            boolean delete = receptionistService.removeById(id);
            if (delete) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "删除前台成功");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("前台不存在");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("删除前台失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取接待员资料
     * @param receptionistId 接待员ID
     * @return 接待员资料
     */
    @GetMapping("/profile/{receptionistId}")
    public ResponseEntity<?> getReceptionistProfile(@PathVariable Long receptionistId) {
        try {
            // 查询接待员基本信息
            Receptionist receptionist = receptionistService.getById(receptionistId);
            
            if (receptionist != null) {
                Map<String, Object> profile = new HashMap<>();
                profile.put("id", receptionist.getId());
                profile.put("username", receptionist.getUsername());
                profile.put("realName", receptionist.getRealName());
                profile.put("phone", receptionist.getPhone());
                profile.put("email", receptionist.getEmail());
                
                return ResponseEntity.ok(profile);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("接待员不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取接待员资料失败：" + e.getMessage());
        }
    }
}