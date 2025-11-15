package com.pet.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.hospital.entity.ConsultationQueue;

import java.time.LocalDate;
import java.util.List;

public interface ConsultationQueueService extends IService<ConsultationQueue> {
    
    /**
     * 根据医生ID和日期获取候诊队列
     */
    List<ConsultationQueue> getQueueByDoctorIdAndDate(Long doctorId, LocalDate queueDate);
    
    /**
     * 生成队列号
     */
    String generateQueueNumber(Long doctorId, LocalDate queueDate);
    
    /**
     * 叫号（开始就诊）
     */
    boolean callQueue(Long queueId);
    
    /**
     * 完成就诊
     */
    boolean completeQueue(Long queueId);
}
