package com.pet.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.hospital.entity.User;

public interface UserService extends IService<User> {
    // 继承MyBatis Plus的IService接口，拥有常用的CRUD方法
    // 可以在这里定义自定义的业务方法
}