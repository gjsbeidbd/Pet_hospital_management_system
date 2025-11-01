package com.pet.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.hospital.entity.Customer;

public interface CustomerService extends IService<Customer> {
    // 继承MyBatis Plus的IService接口，拥有常用的CRUD方法
    // 可以在这里定义自定义的业务方法
}