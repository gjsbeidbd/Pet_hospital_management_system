package com.pet.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet.hospital.entity.DoctorSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DoctorScheduleMapper extends BaseMapper<DoctorSchedule> {
    
    /**
     * 根据医生ID查询排班信息
     * @param doctorId 医生ID
     * @return 排班列表
     */
    @Select("SELECT * FROM doctor_schedules WHERE doctor_id = #{doctorId} ORDER BY schedule_date ASC")
    List<DoctorSchedule> findByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * 根据医生ID和日期范围查询排班信息
     * @param doctorId 医生ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 排班列表
     */
    @Select("SELECT * FROM doctor_schedules WHERE doctor_id = #{doctorId} AND schedule_date BETWEEN #{startDate} AND #{endDate} ORDER BY schedule_date ASC")
    List<DoctorSchedule> findByDoctorIdAndDateRange(@Param("doctorId") Long doctorId, 
                                                   @Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);
    
    /**
     * 根据日期范围查询排班信息
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 排班列表
     */
    @Select("SELECT * FROM doctor_schedules WHERE schedule_date BETWEEN #{startDate} AND #{endDate} ORDER BY schedule_date ASC")
    List<DoctorSchedule> findByDateRange(@Param("startDate") LocalDate startDate, 
                                        @Param("endDate") LocalDate endDate);
}