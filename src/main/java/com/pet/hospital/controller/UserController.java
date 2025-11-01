package com.pet.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pet.hospital.entity.User;
import com.pet.hospital.entity.Pet;
import com.pet.hospital.entity.Customer;
import com.pet.hospital.service.UserService;
import com.pet.hospital.service.PetService;
import com.pet.hospital.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:5180"})
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PetService petService;
    
    @Autowired
    private CustomerService customerService;
    
    // 从application.properties中读取数据库连接配置
    @Value("${spring.datasource.url}")
    private String DB_URL;
    
    @Value("${spring.datasource.username}")
    private String DB_USER;
    
    @Value("${spring.datasource.password}")
    private String DB_PASSWORD;
    
    /**
     * 删除账户请求数据结构
     */
    public static class DeleteAccountRequest {
        private Long userId;
        private String password;
        
        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    /**
     * 修改密码请求数据结构
     */
    public static class ChangePasswordRequest {
        private Long userId;
        private String oldPassword;
        private String newPassword;
        
        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
    
    /**
     * 获取用户资料
     * @param userId 用户ID
     * @return 用户资料
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        try {
            User user = userService.getById(userId);
            if (user != null) {
                Map<String, Object> profile = new HashMap<>();
                profile.put("id", user.getId());
                profile.put("username", user.getUsername());
                profile.put("realName", user.getRealName());
                profile.put("phone", user.getPhone());
                profile.put("email", user.getEmail());
                profile.put("gender", user.getGender());
                profile.put("birthday", user.getBirthday());
                profile.put("address", user.getAddress());
                profile.put("avatar", user.getAvatar());
                
                return ResponseEntity.ok(profile);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户不存在");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取用户资料失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新用户资料
     * @param userId 用户ID
     * @param updatedUser 更新的用户信息
     * @return 更新结果
     */
    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> updateUserProfile(@PathVariable Long userId, @RequestBody User updatedUser) {
        try {
            // 确保ID匹配
            updatedUser.setId(userId);
            boolean update = userService.updateById(updatedUser);
            if (update) {
                return ResponseEntity.ok("用户资料更新成功");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户不存在");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新用户资料失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除账户
     * @param request 删除账户请求
     * @return 删除结果
     */
    @PostMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(@RequestBody DeleteAccountRequest request) {
        try {
            Long userId = request.getUserId();
            String password = request.getPassword();
            
            // 验证用户密码
            User user = userService.getById(userId);
            if (user != null && user.getPassword().equals(password)) {
                // 删除用户
                boolean remove = userService.removeById(userId);
                if (remove) {
                    return ResponseEntity.ok("账户删除成功");
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("账户删除失败");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("密码错误");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除账户失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改密码
     * @param request 修改密码请求
     * @return 修改结果
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            Long userId = request.getUserId();
            String oldPassword = request.getOldPassword();
            String newPassword = request.getNewPassword();
            
            // 验证旧密码
            User user = userService.getById(userId);
            if (user != null && user.getPassword().equals(oldPassword)) {
                // 更新密码
                user.setPassword(newPassword);
                boolean update = userService.updateById(user);
                if (update) {
                    return ResponseEntity.ok("密码修改成功");
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("密码修改失败");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("原密码错误");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("修改密码失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户的宠物列表
     * @param userId 用户ID
     * @return 宠物列表
     */
    @GetMapping("/{userId}/pets")
    public ResponseEntity<?> getUserPets(@PathVariable Long userId) {
        try {
            // 首先根据用户ID查找对应的客户
            QueryWrapper<Customer> customerQueryWrapper = new QueryWrapper<>();
            customerQueryWrapper.eq("user_id", userId);
            Customer customer = customerService.getOne(customerQueryWrapper);
            
            if (customer == null) {
                // 如果找不到对应的客户，创建一个新的客户记录
                customer = new Customer();
                customer.setUserId(userId);
                // 获取用户信息来填充客户信息
                User user = userService.getById(userId);
                if (user != null) {
                    customer.setRealName(user.getRealName() != null ? user.getRealName() : "未知用户");
                    customer.setPhone(user.getPhone()); // 直接使用用户电话，可能为null
                    customer.setEmail(user.getEmail());
                } else {
                    // 如果用户不存在，设置默认值
                    customer.setRealName("未知用户");
                }
                customerService.save(customer);
            }
            
            // 根据客户ID查找宠物
            QueryWrapper<Pet> petQueryWrapper = new QueryWrapper<>();
            petQueryWrapper.eq("customer_id", customer.getId());
            List<Pet> pets = petService.list(petQueryWrapper);
            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取宠物列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据用户ID获取客户信息
     * @param userId 用户ID
     * @return 客户信息
     */
    @GetMapping("/{userId}/customer")
    public ResponseEntity<?> getUserCustomer(@PathVariable Long userId) {
        try {
            // 首先根据用户ID查找对应的客户
            QueryWrapper<Customer> customerQueryWrapper = new QueryWrapper<>();
            customerQueryWrapper.eq("user_id", userId);
            Customer customer = customerService.getOne(customerQueryWrapper);
            
            if (customer == null) {
                // 如果找不到对应的客户，创建一个新的客户记录
                customer = new Customer();
                customer.setUserId(userId);
                // 获取用户信息来填充客户信息
                User user = userService.getById(userId);
                if (user != null) {
                    customer.setRealName(user.getRealName() != null ? user.getRealName() : "未知用户");
                    customer.setPhone(user.getPhone()); // 直接使用用户电话，可能为null
                    customer.setEmail(user.getEmail());
                } else {
                    // 如果用户不存在，设置默认值
                    customer.setRealName("未知用户");
                }
                customerService.save(customer);
            }
            
            return ResponseEntity.ok(customer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取客户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加宠物
     * @param pet 宠物信息（包含customerId）
     * @return 添加结果
     */
    @PostMapping("/pets")
    public ResponseEntity<?> addPet(@RequestBody Pet pet) {
        try {
            // 检查提供的customerId是否有效
            Customer customer = customerService.getById(pet.getCustomerId());
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("无效的客户ID");
            }
            
            // 添加宠物
            boolean save = petService.save(pet);
            if (save) {
                return ResponseEntity.ok("宠物信息添加成功");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("宠物信息添加失败");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("添加宠物失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新宠物信息
     * @param petId 宠物ID
     * @param pet 更新的宠物信息
     * @return 更新结果
     */
    @PutMapping("/pets/{petId}")
    public ResponseEntity<?> updatePet(@PathVariable Long petId, @RequestBody Pet pet) {
        try {
            pet.setId(petId);
            boolean update = petService.updateById(pet);
            if (update) {
                return ResponseEntity.ok("宠物信息更新成功");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("宠物不存在");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新宠物信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除宠物
     * @param petId 宠物ID
     * @return 删除结果
     */
    @DeleteMapping("/pets/{petId}")
    public ResponseEntity<?> deletePet(@PathVariable Long petId) {
        try {
            boolean remove = petService.removeById(petId);
            if (remove) {
                return ResponseEntity.ok("宠物信息删除成功");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("宠物不存在");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除宠物信息失败: " + e.getMessage());
        }
    }
}