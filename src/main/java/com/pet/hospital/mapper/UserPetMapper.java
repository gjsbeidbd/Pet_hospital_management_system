package com.pet.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet.hospital.entity.UserPet;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserPetMapper extends BaseMapper<UserPet> {
}