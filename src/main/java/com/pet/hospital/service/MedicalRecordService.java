package com.pet.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.hospital.entity.MedicalRecord;

import java.util.List;

public interface MedicalRecordService extends IService<MedicalRecord> {
    /**
     * 根据宠物ID获取病例记录
     * @param petId 宠物ID
     * @return 病例记录列表
     */
    List<MedicalRecord> getPetMedicalRecords(Long petId);
}