package com.pet.hospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.hospital.entity.Pet;
import com.pet.hospital.mapper.PetMapper;
import com.pet.hospital.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet> implements PetService {
    // 继承ServiceImpl并实现PetService接口
    // MyBatis Plus已经提供了常用的CRUD实现，可以直接使用
    
    @Autowired
    private PetMapper petMapper;
    
    @Override
    public List<Map<String, Object>> getUserPetsWithOwners() {
        return petMapper.getUserPetsWithOwners();
    }
}