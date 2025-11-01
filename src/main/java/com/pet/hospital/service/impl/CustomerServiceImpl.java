package com.pet.hospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.hospital.entity.Customer;
import com.pet.hospital.mapper.CustomerMapper;
import com.pet.hospital.service.CustomerService;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {
    // 继承ServiceImpl并实现CustomerService接口
    // MyBatis Plus已经提供了常用的CRUD实现，可以直接使用
}