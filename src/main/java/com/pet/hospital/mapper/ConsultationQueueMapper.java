package com.pet.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet.hospital.entity.ConsultationQueue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ConsultationQueueMapper extends BaseMapper<ConsultationQueue> {
    
    /**
     * 根据医生ID和日期获取候诊队列
     */
    @Select("SELECT * FROM consultation_queue WHERE doctor_id = #{doctorId} AND queue_date = #{queueDate} AND status IN ('WAITING', 'CONSULTING') ORDER BY queue_number ASC")
    List<ConsultationQueue> findByDoctorIdAndDate(@Param("doctorId") Long doctorId, @Param("queueDate") LocalDate queueDate);
    
    /**
     * 获取当天最大队列号
     */
    @Select("SELECT MAX(CAST(SUBSTRING(queue_number, 2) AS UNSIGNED)) FROM consultation_queue WHERE doctor_id = #{doctorId} AND queue_date = #{queueDate}")
    Integer getMaxQueueNumber(@Param("doctorId") Long doctorId, @Param("queueDate") LocalDate queueDate);
}
