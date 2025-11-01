package com.pet.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.hospital.entity.User;
import com.pet.hospital.entity.Pet;
import com.pet.hospital.entity.Customer;
import com.pet.hospital.service.UserService;
import com.pet.hospital.service.PetService;
import com.pet.hospital.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/test/mybatis")
public class TestMyBatisController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private PetService petService;
    
    @Autowired
    private CustomerService customerService;

    /**
     * 测试保存用户
     */
    @PostMapping("/user")
    public String saveUser(@RequestBody User user) {
        boolean save = userService.save(user);
        if (save) {
            return "用户保存成功";
        } else {
            return "用户保存失败";
        }
    }

    /**
     * 测试根据ID获取用户
     */
    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getById(id);
    }

    /**
     * 测试更新用户
     */
    @PutMapping("/user")
    public String updateUser(@RequestBody User user) {
        boolean update = userService.updateById(user);
        if (update) {
            return "用户更新成功";
        } else {
            return "用户更新失败";
        }
    }
    
    /**
     * 获取所有客户信息
     */
    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return customerService.list();
    }
    
    /**
     * 根据用户ID获取客户信息
     */
    @GetMapping("/customer/user/{userId}")
    public Customer getCustomerByUserId(@PathVariable Long userId) {
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return customerService.getOne(queryWrapper);
    }
}