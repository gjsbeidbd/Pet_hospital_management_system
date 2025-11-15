package com.pet.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.hospital.entity.Appointment;
import com.pet.hospital.entity.DoctorSchedule;
import com.pet.hospital.mapper.AppointmentMapper;
import com.pet.hospital.service.AppointmentService;
import com.pet.hospital.service.DoctorScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {
    
    @Autowired
    private DoctorScheduleService doctorScheduleService;
    
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
    
    /**
     * 根据医生ID获取预约列表
     * @param doctorId 医生ID
     * @return 预约列表
     */
    @Override
    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        QueryWrapper<Appointment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("doctor_id", doctorId);
        queryWrapper.orderByDesc("appointment_time");
        return this.list(queryWrapper);
    }
    
    /**
     * 检查预约时间冲突
     * 同一医生同一时间段（前后30分钟）不能有多个预约
     */
    @Override
    public boolean checkTimeConflict(Long doctorId, LocalDateTime appointmentTime, Long excludeAppointmentId) {
        // 计算时间范围：前后30分钟
        LocalDateTime startTime = appointmentTime.minusMinutes(30);
        LocalDateTime endTime = appointmentTime.plusMinutes(30);
        
        QueryWrapper<Appointment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("doctor_id", doctorId);
        queryWrapper.between("appointment_time", startTime, endTime);
        queryWrapper.in("status", "PENDING", "CONFIRMED"); // 只检查未取消的预约
        
        if (excludeAppointmentId != null) {
            queryWrapper.ne("id", excludeAppointmentId);
        }
        
        return this.count(queryWrapper) > 0;
    }
    
    /**
     * 验证预约时间是否在医生排班时间内
     */
    @Override
    public boolean validateAppointmentTime(Long doctorId, LocalDateTime appointmentTime) {
        LocalDate date = appointmentTime.toLocalDate();
        LocalTime time = appointmentTime.toLocalTime();
        
        // 获取医生当天的排班
        QueryWrapper<DoctorSchedule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("doctor_id", doctorId);
        queryWrapper.eq("schedule_date", date);
        
        List<DoctorSchedule> schedules = doctorScheduleService.list(queryWrapper);
        
        if (schedules.isEmpty()) {
            return false; // 医生当天没有排班
        }
        
        // 检查时间是否在排班范围内
        for (DoctorSchedule schedule : schedules) {
            if (!time.isBefore(schedule.getStartTime()) && !time.isAfter(schedule.getEndTime())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 获取待确认的预约列表
     */
    @Override
    public List<Appointment> getPendingAppointments() {
        QueryWrapper<Appointment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "PENDING");
        queryWrapper.orderByAsc("appointment_time");
        return this.list(queryWrapper);
    }
    
    /**
     * 获取今日预约列表
     */
    @Override
    public List<Appointment> getTodayAppointments() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        
        QueryWrapper<Appointment> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("appointment_time", startOfDay, endOfDay);
        queryWrapper.orderByAsc("appointment_time");
        return this.list(queryWrapper);
    }
}