package com.pet.hospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.hospital.entity.UserPet;
import com.pet.hospital.mapper.UserPetMapper;
import com.pet.hospital.service.UserPetService;
import org.springframework.stereotype.Service;

@Service
public class UserPetServiceImpl extends ServiceImpl<UserPetMapper, UserPet> implements UserPetService {
}