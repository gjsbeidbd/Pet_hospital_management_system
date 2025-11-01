package com.pet.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.hospital.entity.MedicalRecord;
import com.pet.hospital.mapper.MedicalRecordMapper;
import com.pet.hospital.service.MedicalRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalRecordServiceImpl extends ServiceImpl<MedicalRecordMapper, MedicalRecord> implements MedicalRecordService {
    // 继承ServiceImpl并实现MedicalRecordService接口
    // MyBatis Plus已经提供了常用的CRUD实现，可以直接使用

    /**
     * 根据宠物ID获取病例记录
     * @param petId 宠物ID
     * @return 病例记录列表
     */
    @Override
    public List<MedicalRecord> getPetMedicalRecords(Long petId) {
        QueryWrapper<MedicalRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId);
        queryWrapper.orderByDesc("created_at");
        return this.list(queryWrapper);
    }
}