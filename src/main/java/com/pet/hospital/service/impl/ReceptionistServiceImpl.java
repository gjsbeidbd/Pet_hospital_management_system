package com.pet.hospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.hospital.entity.Receptionist;
import com.pet.hospital.mapper.ReceptionistMapper;
import com.pet.hospital.service.ReceptionistService;
import org.springframework.stereotype.Service;

@Service
public class ReceptionistServiceImpl extends ServiceImpl<ReceptionistMapper, Receptionist> implements ReceptionistService {
    // 继承ServiceImpl并实现ReceptionistService接口
    // MyBatis Plus已经提供了常用的CRUD实现，可以直接使用
}