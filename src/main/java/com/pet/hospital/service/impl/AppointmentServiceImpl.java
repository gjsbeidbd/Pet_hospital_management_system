package com.pet.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.hospital.entity.Appointment;
import com.pet.hospital.mapper.AppointmentMapper;
import com.pet.hospital.service.AppointmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {
    // 继承ServiceImpl并实现AppointmentService接口
    // MyBatis Plus已经提供了常用的CRUD实现，可以直接使用

    /**
     * 根据客户ID获取预约列表
     * @param customerId 客户ID
     * @return 预约列表
     */
    @Override
    public List<Appointment> getAppointmentsByCustomerId(Long customerId) {
        QueryWrapper<Appointment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("customer_id", customerId);
        queryWrapper.orderByDesc("appointment_time");
        return this.list(queryWrapper);
    }
}