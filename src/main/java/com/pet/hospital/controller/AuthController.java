package com.pet.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pet.hospital.entity.User;
import com.pet.hospital.entity.Director;
import com.pet.hospital.entity.Doctor;
import com.pet.hospital.entity.Receptionist;
import com.pet.hospital.service.UserService;
import com.pet.hospital.service.DirectorService;
import com.pet.hospital.service.DoctorService;
import com.pet.hospital.service.ReceptionistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5180"})
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DirectorService directorService;
    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private ReceptionistService receptionistService;
    
    // 手机号正则表达式
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    // 邮箱正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    
    // 测试数据库连接的端点
    @GetMapping("/test-db")
    public ResponseEntity<?> testDBConnection() {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("message", "数据库连接成功");
            
            // 查询所有用户表中的数据
            Map<String, Object> usersData = new HashMap<>();
            
            // 查询普通用户
            List<User> users = userService.list();
            usersData.put("users", users);
            
            // 查询前台用户
            List<Receptionist> receptionists = receptionistService.list();
            usersData.put("receptionists", receptionists);
            
            // 查询医生
            List<Doctor> doctors = doctorService.list();
            usersData.put("doctors", doctors);
            
            // 查询管理员
            List<Director> directors = directorService.list();
            usersData.put("directors", directors);
            
            result.put("data", usersData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "数据库连接失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        System.out.println("Login request received: " + loginRequest);
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        String role = loginRequest.get("role");
        
        System.out.println("Login attempt - Username: " + username + ", Password: " + password + ", Role: " + role);

        // 验证手机号或邮箱格式
        if (!isValidPhoneOrEmail(username)) {
            System.out.println("Invalid phone or email format: " + username);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("请输入有效的手机号或邮箱");
        }
        
        // 验证输入参数
        if (username == null || username.isEmpty() || password == null || password.isEmpty() || role == null || role.isEmpty()) {
            System.out.println("Missing required fields");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("请输入完整的登录信息");
        }
        
        try {
            // 验证用户
            Map<String, Object> user = validateUser(username, password, role);
            if (user != null) {
                // 检查是否是账户被停用的错误
                if (user.containsKey("error") && "ACCOUNT_DISABLED".equals(user.get("error"))) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(user.get("message"));
                }
                // 登录成功
                System.out.println("Login successful, generating response");
                Map<String, Object> response = new HashMap<>();
                response.put("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6I" + username + "InJvbGUiOiI" + role + "In0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
                response.put("role", role);
                response.put("userId", user.get("id"));
                response.put("realName", user.get("real_name"));
                
                return ResponseEntity.ok(response);
            } else {
                // 检查用户是否存在，但角色不匹配
                if (isUserExistsInAnyTable(username)) {
                    System.out.println("User exists but wrong role selected");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("类别错误");
                } else {
                    System.out.println("Login failed - user not found or password mismatch");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("账号不存在或密码错误");
                }
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("登录失败：系统错误");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registerRequest) {
        String username = registerRequest.get("username");
        String password = registerRequest.get("password");
        String role = registerRequest.get("role");

        // 验证手机号或邮箱格式
        if (!isValidPhoneOrEmail(username)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("请输入有效的手机号或邮箱");
        }
        
        // 验证输入参数
        if (username == null || username.isEmpty() || password == null || password.isEmpty() || role == null || role.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("请输入完整的注册信息");
        }
        
        // 注册时只允许普通用户注册
        if (!"USER".equals(role)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("只允许注册为宠物主人角色");
        }
        
        try {
            // 检查用户是否已存在
            if (isUserExists(username, role)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("该手机号或邮箱已被注册");
            }
            
            // 创建用户
            boolean success = createUser(username, password, role);
            if (success) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "注册成功");
                response.put("username", username);
                response.put("role", role);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("注册失败：系统错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("注册失败：系统错误");
        }
    }
    
    /**
     * 验证手机号或邮箱格式
     * @param input 输入的手机号或邮箱
     * @return 是否有效
     */
    private boolean isValidPhoneOrEmail(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(input).matches() || EMAIL_PATTERN.matcher(input).matches();
    }
    
    /**
     * 验证用户登录信息
     * @param username 用户名（手机号或邮箱）
     * @param password 密码
     * @param role 角色
     * @return 用户信息或null
     */
    private Map<String, Object> validateUser(String username, String password, String role) {
        try {
            System.out.println("Login attempt - Role: " + role);
            
            switch (role) {
                case "USER":
                    QueryWrapper<User> userWrapper = new QueryWrapper<>();
                    userWrapper.and(wrapper -> wrapper.eq("username", username)
                            .or().eq("phone", username)
                            .or().eq("email", username));
                    User user = userService.getOne(userWrapper);
                    if (user != null) {
                        System.out.println("User found: " + user.getUsername() + ", Stored password: " + user.getPassword() + ", Provided password: " + password);
                        if (user.getPassword().equals(password)) {
                            Map<String, Object> result = new HashMap<>();
                            result.put("id", user.getId());
                            result.put("username", user.getUsername());
                            result.put("role", role);
                            result.put("real_name", user.getRealName());
                            System.out.println("Login successful for user: " + username);
                            return result;
                        } else {
                            System.out.println("Password mismatch for user: " + username);
                        }
                    } else {
                        System.out.println("User not found: " + username);
                    }
                    break;
                case "RECEPTIONIST":
                    QueryWrapper<Receptionist> receptionistWrapper = new QueryWrapper<>();
                    receptionistWrapper.and(wrapper -> wrapper.eq("username", username)
                            .or().eq("phone", username)
                            .or().eq("email", username));
                    Receptionist receptionist = receptionistService.getOne(receptionistWrapper);
                    if (receptionist != null) {
                        System.out.println("Receptionist found: " + receptionist.getUsername() + ", Stored password: " + receptionist.getPassword() + ", Provided password: " + password);
                        // 检查账户状态
                        if (receptionist.getStatus() != null && receptionist.getStatus() == 0) {
                            System.out.println("Receptionist account is disabled: " + username);
                            Map<String, Object> result = new HashMap<>();
                            result.put("error", "ACCOUNT_DISABLED");
                            result.put("message", "该账户已被停用，请联系院长进行启用");
                            return result;
                        }
                        if (receptionist.getPassword().equals(password)) {
                            Map<String, Object> result = new HashMap<>();
                            result.put("id", receptionist.getId());
                            result.put("username", receptionist.getUsername());
                            result.put("role", role);
                            result.put("real_name", receptionist.getRealName());
                            System.out.println("Login successful for receptionist: " + username);
                            return result;
                        } else {
                            System.out.println("Password mismatch for receptionist: " + username);
                        }
                    } else {
                        System.out.println("Receptionist not found: " + username);
                    }
                    break;
                case "DOCTOR":
                    QueryWrapper<Doctor> doctorWrapper = new QueryWrapper<>();
                    doctorWrapper.and(wrapper -> wrapper.eq("username", username)
                            .or().eq("phone", username)
                            .or().eq("email", username));
                    Doctor doctor = doctorService.getOne(doctorWrapper);
                    if (doctor != null) {
                        System.out.println("Doctor found: " + doctor.getUsername() + ", Stored password: " + doctor.getPassword() + ", Provided password: " + password);
                        // 检查账户状态
                        if (doctor.getStatus() != null && doctor.getStatus() == 0) {
                            System.out.println("Doctor account is disabled: " + username);
                            Map<String, Object> result = new HashMap<>();
                            result.put("error", "ACCOUNT_DISABLED");
                            result.put("message", "该账户已被停用，请联系院长进行启用");
                            return result;
                        }
                        if (doctor.getPassword().equals(password)) {
                            Map<String, Object> result = new HashMap<>();
                            result.put("id", doctor.getId());
                            result.put("username", doctor.getUsername());
                            result.put("role", role);
                            result.put("real_name", doctor.getRealName());
                            System.out.println("Login successful for doctor: " + username);
                            return result;
                        } else {
                            System.out.println("Password mismatch for doctor: " + username);
                        }
                    } else {
                        System.out.println("Doctor not found: " + username);
                    }
                    break;
                case "ADMIN":
                    QueryWrapper<Director> directorWrapper = new QueryWrapper<>();
                    directorWrapper.and(wrapper -> wrapper.eq("username", username)
                            .or().eq("phone", username)
                            .or().eq("email", username));
                    Director director = directorService.getOne(directorWrapper);
                    if (director != null) {
                        System.out.println("Director found: " + director.getUsername() + ", Stored password: " + director.getPassword() + ", Provided password: " + password);
                        if (director.getPassword().equals(password)) {
                            Map<String, Object> result = new HashMap<>();
                            result.put("id", director.getId());
                            result.put("username", director.getUsername());
                            result.put("role", role);
                            result.put("real_name", director.getRealName());
                            System.out.println("Login successful for director: " + username);
                            return result;
                        } else {
                            System.out.println("Password mismatch for director: " + username);
                        }
                    } else {
                        System.out.println("Director not found: " + username);
                    }
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error validating user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * 检查用户是否已存在
     * @param username 用户名
     * @param role 角色
     * @return 是否存在
     */
    private boolean isUserExists(String username, String role) {
        try {
            switch (role) {
                case "USER":
                    QueryWrapper<User> userWrapper = new QueryWrapper<>();
                    userWrapper.and(wrapper -> wrapper.eq("username", username)
                            .or().eq("phone", username)
                            .or().eq("email", username));
                    return userService.count(userWrapper) > 0;
                case "RECEPTIONIST":
                    QueryWrapper<Receptionist> receptionistWrapper = new QueryWrapper<>();
                    receptionistWrapper.and(wrapper -> wrapper.eq("username", username)
                            .or().eq("phone", username)
                            .or().eq("email", username));
                    return receptionistService.count(receptionistWrapper) > 0;
                case "DOCTOR":
                    QueryWrapper<Doctor> doctorWrapper = new QueryWrapper<>();
                    doctorWrapper.and(wrapper -> wrapper.eq("username", username)
                            .or().eq("phone", username)
                            .or().eq("email", username));
                    return doctorService.count(doctorWrapper) > 0;
                case "ADMIN":
                    QueryWrapper<Director> directorWrapper = new QueryWrapper<>();
                    directorWrapper.and(wrapper -> wrapper.eq("username", username)
                            .or().eq("phone", username)
                            .or().eq("email", username));
                    return directorService.count(directorWrapper) > 0;
            }
        } catch (Exception e) {
            System.err.println("Error checking user existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * 创建新用户
     * @param username 用户名
     * @param password 密码
     * @param role 角色
     * @return 是否成功
     */
    private boolean createUser(String username, String password, String role) {
        try {
            System.out.println("Attempting to create user with role: " + role);
            
            if (!"USER".equals(role)) {
                System.out.println("Only USER role is allowed for registration: " + role);
                return false;
            }
            
            // 判断输入的是手机号还是邮箱
            String phone = null;
            String email = null;
            if (PHONE_PATTERN.matcher(username).matches()) {
                phone = username;
                System.out.println("Identified as phone number: " + username);
            } else if (EMAIL_PATTERN.matcher(username).matches()) {
                email = username;
                System.out.println("Identified as email: " + username);
            }
            
            User user = new User();
            user.setUsername(username);
            // 实际项目中应该对密码进行加密存储，这里为了演示使用明文
            user.setPassword(password);
            user.setPhone(phone);
            user.setEmail(email);
            
            System.out.println("Creating user with username=" + username + ", phone=" + phone + ", email=" + email);
            
            boolean result = userService.save(user);
            System.out.println("User creation result: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * 检查用户是否存在于任何表中（不考虑角色）
     * @param username 用户名
     * @return 是否存在
     */
    private boolean isUserExistsInAnyTable(String username) {
        try {
            // 检查所有可能的表
            QueryWrapper<User> userWrapper = new QueryWrapper<>();
            userWrapper.and(wrapper -> wrapper.eq("username", username)
                    .or().eq("phone", username)
                    .or().eq("email", username));
            if (userService.count(userWrapper) > 0) {
                return true;
            }
            
            QueryWrapper<Receptionist> receptionistWrapper = new QueryWrapper<>();
            receptionistWrapper.and(wrapper -> wrapper.eq("username", username)
                    .or().eq("phone", username)
                    .or().eq("email", username));
            if (receptionistService.count(receptionistWrapper) > 0) {
                return true;
            }
            
            QueryWrapper<Doctor> doctorWrapper = new QueryWrapper<>();
            doctorWrapper.and(wrapper -> wrapper.eq("username", username)
                    .or().eq("phone", username)
                    .or().eq("email", username));
            if (doctorService.count(doctorWrapper) > 0) {
                return true;
            }
            
            QueryWrapper<Director> directorWrapper = new QueryWrapper<>();
            directorWrapper.and(wrapper -> wrapper.eq("username", username)
                    .or().eq("phone", username)
                    .or().eq("email", username));
            if (directorService.count(directorWrapper) > 0) {
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error checking user existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}


