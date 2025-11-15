package com.pet.hospital.controller;

import com.pet.hospital.entity.ConsultationQueue;
import com.pet.hospital.service.ConsultationQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consultation-queue")
@CrossOrigin(origins = {"http://localhost:5180"})
public class ConsultationQueueController {
    
    @Autowired
    private ConsultationQueueService consultationQueueService;
    
    /**
     * 获取医生的候诊队列
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getDoctorQueue(
            @PathVariable Long doctorId,
            @RequestParam(required = false) String date) {
        try {
            LocalDate queueDate = date != null ? LocalDate.parse(date) : LocalDate.now();
            List<ConsultationQueue> queue = consultationQueueService.getQueueByDoctorIdAndDate(doctorId, queueDate);
            return ResponseEntity.ok(queue);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("获取候诊队列失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建排队记录（前台取号）
     */
    @PostMapping
    public ResponseEntity<?> createQueue(@RequestBody ConsultationQueue queue) {
        try {
            // 生成队列号
            String queueNumber = consultationQueueService.generateQueueNumber(
                queue.getDoctorId(), 
                queue.getQueueDate()
            );
            queue.setQueueNumber(queueNumber);
            queue.setStatus("WAITING");
            
            boolean save = consultationQueueService.save(queue);
            if (save) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "取号成功");
                response.put("data", queue);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "取号失败");
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建排队记录失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 叫号（开始就诊）
     */
    @PutMapping("/{queueId}/call")
    public ResponseEntity<?> callQueue(@PathVariable Long queueId) {
        try {
            boolean success = consultationQueueService.callQueue(queueId);
            if (success) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "叫号成功");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "叫号失败");
                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "叫号失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 完成就诊
     */
    @PutMapping("/{queueId}/complete")
    public ResponseEntity<?> completeQueue(@PathVariable Long queueId) {
        try {
            boolean success = consultationQueueService.completeQueue(queueId);
            if (success) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "就诊完成");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "操作失败");
                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
