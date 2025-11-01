package com.pet.hospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.hospital.entity.DoctorSchedule;
import com.pet.hospital.mapper.DoctorScheduleMapper;
import com.pet.hospital.service.DoctorScheduleService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DoctorScheduleServiceImpl extends ServiceImpl<DoctorScheduleMapper, DoctorSchedule> implements DoctorScheduleService {
    
    private final DoctorScheduleMapper doctorScheduleMapper;
    
    public DoctorScheduleServiceImpl(DoctorScheduleMapper doctorScheduleMapper) {
        this.doctorScheduleMapper = doctorScheduleMapper;
    }
    
    @Override
    public List<DoctorSchedule> getScheduleByDoctorId(Long doctorId) {
        return doctorScheduleMapper.findByDoctorId(doctorId);
    }
    
    @Override
    public List<DoctorSchedule> getScheduleByDoctorIdAndDateRange(Long doctorId, LocalDate startDate, LocalDate endDate) {
        return doctorScheduleMapper.findByDoctorIdAndDateRange(doctorId, startDate, endDate);
    }
}