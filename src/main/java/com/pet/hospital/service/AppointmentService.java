package com.pet.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.hospital.entity.Appointment;

import java.util.List;

public interface AppointmentService extends IService<Appointment> {
    /**
     * 根据客户ID获取预约列表
     * @param customerId 客户ID
     * @return 预约列表
     */
    List<Appointment> getAppointmentsByCustomerId(Long customerId);
}