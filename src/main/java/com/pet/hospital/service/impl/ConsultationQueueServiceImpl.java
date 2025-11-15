package com.pet.hospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.hospital.entity.ConsultationQueue;
import com.pet.hospital.mapper.ConsultationQueueMapper;
import com.pet.hospital.service.ConsultationQueueService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultationQueueServiceImpl extends ServiceImpl<ConsultationQueueMapper, ConsultationQueue> implements ConsultationQueueService {
    
    @Override
    public List<ConsultationQueue> getQueueByDoctorIdAndDate(Long doctorId, LocalDate queueDate) {
        return baseMapper.findByDoctorIdAndDate(doctorId, queueDate);
    }
    
    @Override
    public String generateQueueNumber(Long doctorId, LocalDate queueDate) {
        Integer maxNumber = baseMapper.getMaxQueueNumber(doctorId, queueDate);
        int nextNumber = (maxNumber == null) ? 1 : maxNumber + 1;
        return "A" + String.format("%03d", nextNumber);
    }
    
    @Override
    public boolean callQueue(Long queueId) {
        ConsultationQueue queue = this.getById(queueId);
        if (queue != null && "WAITING".equals(queue.getStatus())) {
            queue.setStatus("CONSULTING");
            queue.setCallTime(LocalDateTime.now());
            return this.updateById(queue);
        }
        return false;
    }
    
    @Override
    public boolean completeQueue(Long queueId) {
        ConsultationQueue queue = this.getById(queueId);
        if (queue != null && "CONSULTING".equals(queue.getStatus())) {
            queue.setStatus("COMPLETED");
            queue.setCompleteTime(LocalDateTime.now());
            return this.updateById(queue);
        }
        return false;
    }
}
