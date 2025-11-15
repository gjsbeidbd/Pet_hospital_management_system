package com.pet.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.hospital.entity.Appointment;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService extends IService<Appointment> {
    /**
     * 根据客户ID获取预约列表
     * @param customerId 客户ID
     * @return 预约列表
     */
    List<Appointment> getAppointmentsByCustomerId(Long customerId);
    
    /**
     * 根据医生ID获取预约列表
     * @param doctorId 医生ID
     * @return 预约列表
     */
    List<Appointment> getAppointmentsByDoctorId(Long doctorId);
    
    /**
     * 检查预约时间冲突
     * @param doctorId 医生ID
     * @param appointmentTime 预约时间
     * @param excludeAppointmentId 排除的预约ID（修改预约时使用）
     * @return 是否有冲突
     */
    boolean checkTimeConflict(Long doctorId, LocalDateTime appointmentTime, Long excludeAppointmentId);
    
    /**
     * 验证预约是否有效（医生排班检查）
     * @param doctorId 医生ID
     * @param appointmentTime 预约时间
     * @return 是否有效
     */
    boolean validateAppointmentTime(Long doctorId, LocalDateTime appointmentTime);
    
    /**
     * 获取待确认的预约列表（前台管理用）
     * @return 预约列表
     */
    List<Appointment> getPendingAppointments();
    
    /**
     * 获取今日预约列表
     * @return 预约列表
     */
    List<Appointment> getTodayAppointments();
}