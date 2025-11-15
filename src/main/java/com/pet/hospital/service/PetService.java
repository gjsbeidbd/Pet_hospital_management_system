package com.pet.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.hospital.entity.Pet;

import java.util.List;
import java.util.Map;

public interface PetService extends IService<Pet> {
    // 继承MyBatis Plus的IService接口，拥有常用的CRUD方法
    // 可以在这里定义自定义的业务方法
    
    /**
     * 获取用户和宠物的关联数据
     * @return 宠物和主人的关联列表
     */
    List<Map<String, Object>> getUserPetsWithOwners();
}