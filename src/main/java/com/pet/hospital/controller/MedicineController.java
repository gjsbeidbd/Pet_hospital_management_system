package com.pet.hospital.controller;

import com.pet.hospital.entity.Medicine;
import com.pet.hospital.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medicines")
@CrossOrigin(origins = {"http://localhost:5180"})
public class MedicineController {
    
    @Autowired
    private MedicineService medicineService;
    
    /**
     * 获取所有药品
     */
    @GetMapping
    public ResponseEntity<?> getAllMedicines() {
        try {
            List<Medicine> medicines = medicineService.list();
            return ResponseEntity.ok(medicines);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("获取药品列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取药品
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getMedicineById(@PathVariable Long id) {
        try {
            Medicine medicine = medicineService.getById(id);
            if (medicine != null) {
                return ResponseEntity.ok(medicine);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("药品不存在");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("获取药品失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加药品
     */
    @PostMapping
    public ResponseEntity<?> addMedicine(@RequestBody Medicine medicine) {
        try {
            boolean save = medicineService.save(medicine);
            if (save) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "添加药品成功");
                response.put("data", medicine);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("添加药品失败");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("添加药品失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新药品
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedicine(@PathVariable Long id, @RequestBody Medicine medicine) {
        try {
            medicine.setId(id);
            boolean update = medicineService.updateById(medicine);
            if (update) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "更新药品成功");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("药品不存在");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("更新药品失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除药品
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedicine(@PathVariable Long id) {
        try {
            boolean delete = medicineService.removeById(id);
            if (delete) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "删除药品成功");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("药品不存在");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("删除药品失败: " + e.getMessage());
        }
    }
    
    /**
     * 药品补货
     */
    @PutMapping("/{id}/stock-in")
    public ResponseEntity<?> stockIn(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        try {
            Medicine medicine = medicineService.getById(id);
            if (medicine == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("药品不存在");
            }
            
            Integer quantity = request.get("quantity");
            if (quantity == null || quantity <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("补货数量必须大于0");
            }
            
            medicine.setStockQuantity(medicine.getStockQuantity() + quantity);
            boolean update = medicineService.updateById(medicine);
            
            if (update) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "补货成功");
                response.put("data", medicine);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("补货失败");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("补货失败: " + e.getMessage());
        }
    }
}
