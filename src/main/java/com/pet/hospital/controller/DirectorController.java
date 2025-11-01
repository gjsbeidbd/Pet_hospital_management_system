package com.pet.hospital.controller;

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
import java.util.Map;

@RestController
@RequestMapping("/api/director")
@CrossOrigin(origins = {"http://localhost:5180"})
public class DirectorController {
    
    // 从application.properties中读取数据库连接配置
    @Value("${spring.datasource.url}")
    private String DB_URL;
    
    @Value("${spring.datasource.username}")
    private String DB_USER;
    
    @Value("${spring.datasource.password}")
    private String DB_PASSWORD;
    
    /**
     * 获取院长资料
     * @param directorId 院长ID
     * @return 院长资料
     */
    @GetMapping("/profile/{directorId}")
    public ResponseEntity<?> getDirectorProfile(@PathVariable Long directorId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // 查询院长基本信息（根据实际表结构）
            String sql = "SELECT id, username, real_name, phone, email, department FROM directors WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, directorId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                Map<String, Object> profile = new HashMap<>();
                profile.put("id", rs.getLong("id"));
                profile.put("username", rs.getString("username"));
                profile.put("realName", rs.getString("real_name"));
                profile.put("phone", rs.getString("phone"));
                profile.put("email", rs.getString("email"));
                profile.put("department", rs.getString("department"));
                
                return ResponseEntity.ok(profile);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("院长不存在");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取院长资料失败：" + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}