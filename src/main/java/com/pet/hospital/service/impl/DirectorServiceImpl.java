package com.pet.hospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.hospital.entity.Director;
import com.pet.hospital.mapper.DirectorMapper;
import com.pet.hospital.service.DirectorService;
import org.springframework.stereotype.Service;

@Service
public class DirectorServiceImpl extends ServiceImpl<DirectorMapper, Director> implements DirectorService {
    // 继承ServiceImpl并实现DirectorService接口
    // MyBatis Plus已经提供了常用的CRUD实现，可以直接使用
}