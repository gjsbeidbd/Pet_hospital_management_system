package com.pet.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet.hospital.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {
    // MyBatis Plus已经提供了常用的CRUD方法，无需额外编写
    // 可以在这里添加自定义的SQL方法
}