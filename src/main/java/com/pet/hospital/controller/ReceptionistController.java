package com.pet.hospital.controller;

import com.pet.hospital.entity.Receptionist;
import com.pet.hospital.service.ReceptionistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/receptionist")
@CrossOrigin(origins = {"http://localhost:5180"})
public class ReceptionistController {
    
    @Autowired
    private ReceptionistService receptionistService;
    
    // 从application.properties中读取数据库连接配置
    @Value("${spring.datasource.url}")
    private String DB_URL;
    
    @Value("${spring.datasource.username}")
    private String DB_USER;
    
    @Value("${spring.datasource.password}")
    private String DB_PASSWORD;
    
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