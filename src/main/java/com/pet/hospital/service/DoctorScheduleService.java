package com.pet.hospital.service;

import com.pet.hospital.entity.DoctorSchedule;

import java.time.LocalDate;
import java.util.List;

public interface DoctorScheduleService {
    
    /**
     * 根据医生ID查询排班信息
     * @param doctorId 医生ID
     * @return 排班列表
     */
    List<DoctorSchedule> getScheduleByDoctorId(Long doctorId);
    
    /**
     * 根据医生ID和日期范围查询排班信息
     * @param doctorId 医生ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 排班列表
     */
    List<DoctorSchedule> getScheduleByDoctorIdAndDateRange(Long doctorId, LocalDate startDate, LocalDate endDate);
}