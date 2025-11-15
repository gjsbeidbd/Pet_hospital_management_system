package com.pet.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pet.hospital.entity.User;
import com.pet.hospital.entity.Pet;
import com.pet.hospital.entity.Customer;
import com.pet.hospital.entity.UserPet;
import com.pet.hospital.service.UserService;
import com.pet.hospital.service.PetService;
import com.pet.hospital.service.CustomerService;
import com.pet.hospital.service.UserPetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    
    @Autowired
    private UserPetService userPetService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // 从application.properties中读取数据库连接配置
    @Value("${spring.datasource.url}")
    private String DB_URL;
    
    @Value("${spring.datasource.username}")
    private String DB_USER;
    
    @Value("${spring.datasource.password}")
    private String DB_PASSWORD;
    
    @Value("${file.upload.avatar-dir}")
    private String avatarUploadDir;
    
    /**
     * 上传用户头像
     * @param userId 用户ID
     * @param file 头像文件
     * @return 上传结果
     */
    @PostMapping("/upload-avatar/{userId}")
    public ResponseEntity<?> uploadAvatar(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        try {
            // 验证文件是否为空
            if (file.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "请选择要上传的文件");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "请上传图片文件");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // 验证文件大小（2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "图片大小不能超过2MB");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // 生成新文件名：user_{userId}_{timestamp}{extension}
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String newFilename = "user_" + userId + "_" + timestamp + fileExtension;
            
            // 创建上传目录
            File uploadDir = new File(avatarUploadDir);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 保存文件
            Path filePath = Paths.get(avatarUploadDir + newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // 生成访问 URL
            String avatarUrl = "/" + avatarUploadDir + newFilename;
            
            // 更新数据库中的用户头像
            User user = userService.getById(userId);
            if (user != null) {
                user.setAvatar(avatarUrl);
                userService.updateById(user);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "头像上传成功");
                response.put("data", avatarUrl);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "上传头像失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
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
     * 重置密码（前台专用，不需要验证旧密码）
     * @param userId 用户ID
     * @param request 包含新密码的请求体
     * @return 重置结果
     */
    @PostMapping("/reset-password/{userId}")
    public ResponseEntity<?> resetPassword(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        try {
            String newPassword = request.get("newPassword");
            
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("新密码不能为空");
            }
            
            // 获取用户
            User user = userService.getById(userId);
            if (user != null) {
                // 直接更新密码
                user.setPassword(newPassword);
                boolean update = userService.updateById(user);
                if (update) {
                    return ResponseEntity.ok("密码重置成功");
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("密码重置失败");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户不存在");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("重置密码失败: " + e.getMessage());
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
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", pets);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取宠物列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", customer);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取客户信息失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
                // 同时在user_pets表中创建用户和宠物的关联记录
                // 首先根据customerId查找对应的userId
                if (customer.getUserId() != null) {
                    UserPet userPet = new UserPet(customer.getUserId(), pet.getId());
                    boolean userPetSaved = userPetService.save(userPet);
                    if (!userPetSaved) {
                        // 如果关联记录保存失败，记录日志但不中断主流程
                        System.err.println("警告：无法创建用户和宠物的关联记录");
                    }
                }
                return ResponseEntity.ok("宠物信息添加成功");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("宠物信息添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈以便调试
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("添加宠物失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有用户列表
     * @return 用户列表
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.list();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", users);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取用户列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 创建新用户
     * @param user 用户信息
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            // 检查用户名是否已存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", user.getUsername());
            User existingUser = userService.getOne(queryWrapper);
            
            if (existingUser != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户名已存在");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // 保存用户
            boolean save = userService.save(user);
            if (save) {
                // 同时创建对应的customer记录
                Customer customer = new Customer();
                customer.setUserId(user.getId());
                customer.setRealName(user.getRealName());
                customer.setPhone(user.getPhone());
                customer.setEmail(user.getEmail());
                customer.setAddress(user.getAddress());
                customerService.save(customer);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "用户创建成功");
                response.put("data", user);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户创建失败");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建用户失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 根据ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.getById(userId);
            if (user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", user);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取用户信息失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 获取用户关联的宠物列表（通过user_pets关联表）
     * @return 宠物列表
     */
    @GetMapping("/pets")
    public ResponseEntity<?> getUserPetsWithAssociations() {
        try {
            // 使用MyBatis Plus联表查询获取用户和宠物的关联数据
            List<Map<String, Object>> petsWithOwners = petService.getUserPetsWithOwners();
            return ResponseEntity.ok(petsWithOwners);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取宠物列表失败: " + e.getMessage());
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
    
    /**
     * 根据ID获取宠物信息
     * @param petId 宠物ID
     * @return 宠物信息
     */
    @GetMapping("/pet/{petId}")
    public ResponseEntity<?> getPetById(@PathVariable Long petId) {
        try {
            Pet pet = petService.getById(petId);
            if (pet != null) {
                return ResponseEntity.ok(pet);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("宠物不存在");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取宠物信息失败: " + e.getMessage());
        }
    }
}