package com.pet.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet.hospital.entity.Pet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface PetMapper extends BaseMapper<Pet> {
    // MyBatis Plus已经提供了常用的CRUD方法，无需额外编写
    // 可以在这里添加自定义的SQL方法
    
    /**
     * 获取用户和宠物的关联数据
     * @return 宠物和主人的关联列表
     */
    @Select("SELECT p.*, u.username as ownerUsername, u.real_name as ownerName " +
            "FROM pets p " +
            "LEFT JOIN customers c ON p.customer_id = c.id " +
            "LEFT JOIN users u ON c.user_id = u.id")
    List<Map<String, Object>> getUserPetsWithOwners();
}