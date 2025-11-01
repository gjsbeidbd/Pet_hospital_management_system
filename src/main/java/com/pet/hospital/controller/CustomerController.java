package com.pet.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pet.hospital.entity.Customer;
import com.pet.hospital.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = {"http://localhost:5180"})
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 获取所有客户列表
     * @return 客户列表
     */
    @GetMapping
    public ResponseEntity<?> getAllCustomers() {
        try {
            List<Customer> customers = customerService.list();
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取客户列表失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 根据ID获取客户信息
     * @param id 客户ID
     * @return 客户信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        try {
            Customer customer = customerService.getById(id);
            if (customer != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", customer);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "客户不存在");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取客户信息失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 创建新客户
     * @param customer 客户信息
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer) {
        try {
            // 检查是否已存在相同用户ID的客户
            QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", customer.getUserId());
            Customer existingCustomer = customerService.getOne(queryWrapper);
            
            if (existingCustomer != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "该用户已有关联的客户信息");
                return ResponseEntity.status(400).body(response);
            }
            
            boolean save = customerService.save(customer);
            if (save) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "客户创建成功");
                response.put("data", customer);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "客户创建失败");
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建客户失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 更新客户信息
     * @param id 客户ID
     * @param customer 更新的客户信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        try {
            customer.setId(id);
            boolean update = customerService.updateById(customer);
            if (update) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "客户信息更新成功");
                response.put("data", customer);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "客户不存在");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新客户信息失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 删除客户
     * @param id 客户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        try {
            boolean remove = customerService.removeById(id);
            if (remove) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "客户删除成功");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "客户不存在");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "删除客户失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}