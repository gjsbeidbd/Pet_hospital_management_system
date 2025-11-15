<template>
  <div class="consultation-container">
    <el-row :gutter="20">
      <!-- 左侧：候诊队列 -->
      <el-col :span="6">
        <el-card class="queue-card">
          <template #header>
            <div class="card-header">
              <span>候诊队列</span>
              <el-button size="small" @click="loadQueue">刷新</el-button>
            </div>
          </template>

          <div class="queue-list">
            <div 
              v-for="item in queueList" 
              :key="item.id"
              :class="['queue-item', item.status === 'CONSULTING' ? 'consulting' : '', selectedQueueId === item.id ? 'selected' : '']"
              @click="selectPatient(item)">
              <div class="queue-number">{{ item.queueNumber }}</div>
              <div class="queue-info">
                <div class="patient-name">{{ item.customerName }}</div>
                <div class="pet-name">{{ item.petName }}</div>
              </div>
              <div class="queue-status">
                <el-tag :type="getQueueStatusType(item.status)" size="small">
                  {{ getQueueStatusText(item.status) }}
                </el-tag>
              </div>
            </div>
            <el-empty v-if="queueList.length === 0" description="暂无候诊患者" :image-size="100"></el-empty>
          </div>
        </el-card>
      </el-col>

      <!-- 中间：宠物信息和问诊 -->
      <el-col :span="12">
        <el-card class="patient-card" v-if="currentPatient">
          <template #header>
            <div class="card-header">
              <span>当前就诊</span>
              <div>
                <el-button 
                  v-if="currentQueue && currentQueue.status === 'WAITING'"
                  type="primary" 
                  @click="startConsultation">
                  开始就诊
                </el-button>
                <el-button 
                  v-if="currentQueue && currentQueue.status === 'CONSULTING'"
                  type="success" 
                  @click="showPrescriptionDialog">
                  开具处方
                </el-button>
              </div>
            </div>
          </template>

          <!-- 宠物基本信息 -->
          <el-descriptions title="宠物信息" :column="2" border>
            <el-descriptions-item label="号码">{{ currentQueue?.queueNumber }}</el-descriptions-item>
            <el-descriptions-item label="宠物名称">{{ currentPatient.name }}</el-descriptions-item>
            <el-descriptions-item label="种类">{{ currentPatient.species }}</el-descriptions-item>
            <el-descriptions-item label="品种">{{ currentPatient.breed || '未知' }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ currentPatient.age }}岁</el-descriptions-item>
            <el-descriptions-item label="性别">
              {{ currentPatient.gender === 'MALE' ? '雄性' : '雌性' }}
            </el-descriptions-item>
            <el-descriptions-item label="主人">{{ currentCustomer?.realName }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ currentCustomer?.phone }}</el-descriptions-item>
          </el-descriptions>

          <!-- 疫苗情况 -->
          <div class="section-title">疫苗接种情况</div>
          <el-input 
            v-model="currentPatient.vaccinationStatus" 
            type="textarea" 
            :rows="2" 
            readonly
            placeholder="暂无记录">
          </el-input>

          <!-- 既往病史 -->
          <div class="section-title">既往病史</div>
          <el-input 
            v-model="currentPatient.medicalHistory" 
            type="textarea" 
            :rows="3" 
            readonly
            placeholder="暂无记录">
          </el-input>

          <!-- 问诊记录 -->
          <div class="section-title" v-if="currentQueue && currentQueue.status === 'CONSULTING'">问诊记录</div>
          <el-form 
            v-if="currentQueue && currentQueue.status === 'CONSULTING'"
            :model="medicalRecordForm" 
            label-width="100px">
            <el-form-item label="症状">
              <el-input v-model="medicalRecordForm.symptoms" type="textarea" :rows="3" placeholder="请输入宠物症状"></el-input>
            </el-form-item>
            
            <!-- 步骤1：初步评估 -->
            <el-form-item label="是否需要检查">
              <el-radio-group v-model="needsExamination">
                <el-radio :label="false">不需要，直接诊断</el-radio>
                <el-radio :label="true">需要检查（CT/血常规/X光等）</el-radio>
              </el-radio-group>
            </el-form-item>

            <!-- 如果需要检查 -->
            <div v-if="needsExamination" class="examination-section">
              <el-alert
                title="检查流程提示"
                type="info"
                :closable="false"
                style="margin-bottom: 15px;">
                <template #default>
                  <div>开具检查单 → 用户缴费 → 进行检查 → 上传检查结果 → 确定诊断和治疗方案</div>
                </template>
              </el-alert>
              
              <el-form-item label="检查项目">
                <el-checkbox-group v-model="examinationItems">
                  <el-checkbox label="CT检查" value="CT"></el-checkbox>
                  <el-checkbox label="X光检查" value="XRAY"></el-checkbox>
                  <el-checkbox label="血常规" value="BLOOD"></el-checkbox>
                  <el-checkbox label="尿检" value="URINE"></el-checkbox>
                  <el-checkbox label="B超" value="ULTRASOUND"></el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              
              <el-form-item label="检查说明">
                <el-input v-model="examinationNotes" type="textarea" :rows="2" placeholder="请输入检查目的和注意事项"></el-input>
              </el-form-item>
              
              <el-form-item>
                <el-button type="warning" @click="issueExaminationOrder" :disabled="examinationItems.length === 0">
                  开具检查单
                </el-button>
              </el-form-item>
              
              <!-- 检查结果上传区域 -->
              <div v-if="examinationIssued">
                <el-divider></el-divider>
                <el-form-item label="检查结果">
                  <el-input 
                    v-model="examinationResult" 
                    type="textarea" 
                    :rows="4" 
                    placeholder="等待用户缴费后进行检查，然后在此输入检查结果"></el-input>
                </el-form-item>
              </div>
            </div>
            
            <!-- 步骤2：诊断和治疗方案（在不需要检查或检查结果录入后显示） -->
            <div v-if="!needsExamination || examinationResult">
              <el-divider v-if="needsExamination"></el-divider>
              <el-form-item label="诊断">
                <el-input v-model="medicalRecordForm.diagnosis" type="textarea" :rows="2" placeholder="请输入诊断结果"></el-input>
              </el-form-item>
              <el-form-item label="治疗方案">
                <el-input v-model="medicalRecordForm.treatment" type="textarea" :rows="2" placeholder="请输入治疗方案"></el-input>
              </el-form-item>
              <el-form-item label="备注">
                <el-input v-model="medicalRecordForm.notes" type="textarea" :rows="2" placeholder="其他备注信息"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveMedicalRecord">保存病历</el-button>
                <el-button type="success" @click="showPrescriptionDialog" v-if="currentMedicalRecordId">开具处方</el-button>
                <el-button type="success" @click="completeConsultation" v-if="currentMedicalRecordId">完成就诊</el-button>
              </el-form-item>
            </div>
          </el-form>
        </el-card>

        <el-empty v-else description="请选择候诊患者" :image-size="200"></el-empty>
      </el-col>

      <!-- 右侧：历史病例 -->
      <el-col :span="6">
        <el-card class="history-card">
          <template #header>
            <span>历史病例</span>
          </template>
          <div class="history-list" v-if="currentPatient">
            <div 
              v-for="record in historyRecords" 
              :key="record.id"
              class="history-item"
              @click="viewHistoryRecord(record)">
              <div class="history-date">{{ formatDate(record.createdAt) }}</div>
              <div class="history-diagnosis">{{ record.diagnosis }}</div>
            </div>
            <el-empty v-if="historyRecords.length === 0" description="暂无历史病例" :image-size="80"></el-empty>
          </div>
          <el-empty v-else description="请选择患者查看" :image-size="80"></el-empty>
        </el-card>
      </el-col>
    </el-row>

    <!-- 开具处方对话框 -->
    <el-dialog title="开具处方" v-model="prescriptionDialogVisible" width="700px">
      <el-form :model="prescriptionForm" label-width="100px">
        <el-form-item label="选择药品">
          <el-select v-model="selectedMedicine" placeholder="请选择药品" style="width: 70%;" filterable>
            <el-option 
              v-for="medicine in medicines" 
              :key="medicine.id" 
              :label="`${medicine.name} - ¥${medicine.price}`" 
              :value="medicine.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="prescriptionForm.quantity" :min="1" :max="100"></el-input-number>
        </el-form-item>
        <el-form-item label="用法用量">
          <el-input v-model="prescriptionForm.dosage" type="textarea" :rows="2" placeholder="请输入用法用量"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="addPrescriptionItem">添加药品</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 已添加的药品 -->
      <div class="prescription-list" v-if="prescriptionItems.length > 0">
        <h4>已添加的药品</h4>
        <el-table :data="prescriptionItems" style="width: 100%">
          <el-table-column prop="medicineName" label="药品名称"></el-table-column>
          <el-table-column prop="quantity" label="数量" width="80"></el-table-column>
          <el-table-column prop="dosage" label="用法用量"></el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="scope">
              <el-button size="small" type="danger" @click="removePrescriptionItem(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="prescriptionDialogVisible = false">取消</el-button>
          <el-button type="success" @click="submitPrescription" :disabled="prescriptionItems.length === 0">提交处方</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看历史病例对话框 -->
    <el-dialog title="病例详情" v-model="historyDialogVisible" width="600px">
      <el-descriptions :column="1" border v-if="selectedHistory">
        <el-descriptions-item label="就诊日期">{{ formatDateTime(selectedHistory.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="症状">{{ selectedHistory.symptoms }}</el-descriptions-item>
        <el-descriptions-item label="诊断">{{ selectedHistory.diagnosis }}</el-descriptions-item>
        <el-descriptions-item label="治疗方案">{{ selectedHistory.treatment }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ selectedHistory.notes || '无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="historyDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus';
import api from '@/services/api';

export default {
  name: 'DoctorConsultation',
  data() {
    return {
      doctorId: null,
      selectedQueueId: null,
      queueList: [],
      currentQueue: null,
      currentPatient: null,
      currentCustomer: null,
      historyRecords: [],
      medicines: [],
      
      // 问诊相关
      needsExamination: false, // 是否需要检查
      examinationItems: [], // 检查项目
      examinationNotes: '', // 检查说明
      examinationIssued: false, // 是否已开检查单
      examinationResult: '', // 检查结果
      
      medicalRecordForm: {
        symptoms: '',
        diagnosis: '',
        treatment: '',
        notes: ''
      },
      prescriptionDialogVisible: false,
      prescriptionForm: {
        quantity: 1,
        dosage: ''
      },
      selectedMedicine: null,
      prescriptionItems: [],
      currentMedicalRecordId: null,
      historyDialogVisible: false,
      selectedHistory: null
    };
  },
  mounted() {
    this.doctorId = localStorage.getItem('userId');
    this.loadQueue();
    this.loadMedicines();
  },
  methods: {
    async loadQueue() {
      try {
        const response = await api.get(`/api/consultation-queue/doctor/${this.doctorId}`);
        this.queueList = response.data;
        
        // 获取客户和宠物信息
        for (let item of this.queueList) {
          try {
            const customerResp = await api.get(`/api/customers/${item.customerId}`);
            if (customerResp.data.success) {
              item.customerName = customerResp.data.data.realName;
            }
            
            const petResp = await api.get(`/api/users/pet/${item.petId}`);
            if (petResp.data) {
              item.petName = petResp.data.name;
            }
          } catch (error) {
            console.error('获取详情失败:', error);
          }
        }
      } catch (error) {
        ElMessage.error('获取候诊队列失败: ' + error.message);
      }
    },

    async loadMedicines() {
      try {
        const response = await api.get('/api/medicines');
        this.medicines = response.data;
      } catch (error) {
        console.error('获取药品列表失败:', error);
      }
    },

    async selectPatient(queueItem) {
      this.selectedQueueId = queueItem.id;
      this.currentQueue = queueItem;
      
      try {
        // 获取宠物详细信息
        const petResp = await api.get(`/api/users/pet/${queueItem.petId}`);
        this.currentPatient = petResp.data;
        
        // 获取客户信息
        const customerResp = await api.get(`/api/customers/${queueItem.customerId}`);
        if (customerResp.data.success) {
          this.currentCustomer = customerResp.data.data;
        }
        
        // 获取历史病例
        await this.loadHistoryRecords(queueItem.petId);
      } catch (error) {
        ElMessage.error('获取患者信息失败: ' + error.message);
      }
    },

    async loadHistoryRecords(petId) {
      try {
        const response = await api.get(`/api/medical-records/pet/${petId}`);
        this.historyRecords = response.data;
      } catch (error) {
        console.error('获取历史病例失败:', error);
        this.historyRecords = [];
      }
    },

    async startConsultation() {
      try {
        await api.put(`/api/consultation-queue/${this.currentQueue.id}/call`);
        
        // 如果是预约就诊，更新预约状态为IN_PROGRESS（就诊中）
        if (this.currentQueue.appointmentId) {
          await api.put(`/api/appointments/${this.currentQueue.appointmentId}/start`);
        }
        
        ElMessage.success('开始就诊');
        this.currentQueue.status = 'CONSULTING';
        this.loadQueue();
        
        // 重置问诊状态
        this.needsExamination = false;
        this.examinationItems = [];
        this.examinationNotes = '';
        this.examinationIssued = false;
        this.examinationResult = '';
      } catch (error) {
        ElMessage.error('操作失败: ' + error.message);
      }
    },
    
    // 开具检查单
    async issueExaminationOrder() {
      if (this.examinationItems.length === 0) {
        ElMessage.warning('请至少选择一项检查');
        return;
      }
      
      try {
        // TODO: 调用后端接口创建检查单和费用
        // 目前先模拟功能
        this.examinationIssued = true;
        ElMessage.success(`检查单已开具：${this.examinationItems.join('、')}`);
        ElMessage.info('请通知用户去前台缴费后进行检查');
      } catch (error) {
        ElMessage.error('开具检查单失败: ' + error.message);
      }
    },

    async saveMedicalRecord() {
      if (!this.medicalRecordForm.symptoms || !this.medicalRecordForm.diagnosis) {
        ElMessage.warning('请填写症状和诊断');
        return;
      }

      try {
        // 构建病历数据，包含检查信息
        let notesContent = this.medicalRecordForm.notes || '';
        
        // 如果有检查，将检查信息添加到备注中
        if (this.needsExamination && this.examinationIssued) {
          notesContent += `\n\n【检查信息】\n`;
          notesContent += `检查项目：${this.examinationItems.join('、')}\n`;
          if (this.examinationNotes) {
            notesContent += `检查说明：${this.examinationNotes}\n`;
          }
          if (this.examinationResult) {
            notesContent += `检查结果：${this.examinationResult}`;
          }
        }
        
        const recordData = {
          petId: this.currentPatient.id,
          customerId: this.currentQueue.customerId,
          doctorId: this.doctorId,
          symptoms: this.medicalRecordForm.symptoms,
          diagnosis: this.medicalRecordForm.diagnosis,
          treatment: this.medicalRecordForm.treatment,
          notes: notesContent
        };

        const response = await api.post('/api/medical-records', recordData);
        if (response.data.success) {
          this.currentMedicalRecordId = response.data.data.id;
          ElMessage.success('病历保存成功');
        }
      } catch (error) {
        ElMessage.error('保存病历失败: ' + error.message);
      }
    },

    showPrescriptionDialog() {
      if (!this.currentMedicalRecordId) {
        ElMessage.warning('请先保存病历');
        return;
      }
      this.prescriptionDialogVisible = true;
    },

    addPrescriptionItem() {
      if (!this.selectedMedicine) {
        ElMessage.warning('请选择药品');
        return;
      }

      const medicine = this.medicines.find(m => m.id === this.selectedMedicine);
      if (medicine) {
        this.prescriptionItems.push({
          medicineId: medicine.id,
          medicineName: medicine.name,
          quantity: this.prescriptionForm.quantity,
          dosage: this.prescriptionForm.dosage
        });

        // 重置表单
        this.selectedMedicine = null;
        this.prescriptionForm.quantity = 1;
        this.prescriptionForm.dosage = '';
      }
    },

    removePrescriptionItem(index) {
      this.prescriptionItems.splice(index, 1);
    },

    async submitPrescription() {
      try {
        for (let item of this.prescriptionItems) {
          await api.post('/api/prescriptions', {
            medicalRecordId: this.currentMedicalRecordId,
            medicineId: item.medicineId,
            quantity: item.quantity,
            dosage: item.dosage
          });
        }

        ElMessage.success('处方开具成功');
        this.prescriptionDialogVisible = false;
        this.prescriptionItems = [];
      } catch (error) {
        ElMessage.error('开具处方失败: ' + error.message);
      }
    },

    async completeConsultation() {
      if (!this.currentMedicalRecordId) {
        ElMessage.warning('请先保存病历');
        return;
      }

      try {
        await api.put(`/api/consultation-queue/${this.currentQueue.id}/complete`);
        
        // 如果是预约就诊，更新预约状态为COMPLETED（已就诊）
        if (this.currentQueue.appointmentId) {
          await api.put(`/api/appointments/${this.currentQueue.appointmentId}/complete`);
        }
        
        ElMessage.success('就诊完成');
        
        // 重置所有状态
        this.currentQueue = null;
        this.currentPatient = null;
        this.currentCustomer = null;
        this.selectedQueueId = null;
        this.medicalRecordForm = {
          symptoms: '',
          diagnosis: '',
          treatment: '',
          notes: ''
        };
        this.currentMedicalRecordId = null;
        
        // 重置检查相关
        this.needsExamination = false;
        this.examinationItems = [];
        this.examinationNotes = '';
        this.examinationIssued = false;
        this.examinationResult = '';
        
        this.loadQueue();
      } catch (error) {
        ElMessage.error('操作失败: ' + error.message);
      }
    },

    viewHistoryRecord(record) {
      this.selectedHistory = record;
      this.historyDialogVisible = true;
    },

    formatDate(date) {
      if (!date) return '';
      return new Date(date).toLocaleDateString('zh-CN');
    },

    formatDateTime(date) {
      if (!date) return '';
      return new Date(date).toLocaleString('zh-CN');
    },

    getQueueStatusType(status) {
      const map = {
        'WAITING': 'info',
        'CONSULTING': 'warning',
        'COMPLETED': 'success',
        'CANCELLED': 'danger'
      };
      return map[status] || 'info';
    },

    getQueueStatusText(status) {
      const map = {
        'WAITING': '候诊',
        'CONSULTING': '就诊中',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
      };
      return map[status] || status;
    }
  }
};
</script>

<style scoped>
.consultation-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.queue-list {
  max-height: calc(100vh - 200px);
  overflow-y: auto;
}

.queue-item {
  display: flex;
  align-items: center;
  padding: 12px;
  margin-bottom: 8px;
  border: 1px solid #eee;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.queue-item:hover {
  background-color: #f5f7fa;
  border-color: #409EFF;
}

.queue-item.selected {
  background-color: #ecf5ff;
  border-color: #409EFF;
}

.queue-item.consulting {
  background-color: #fdf6ec;
  border-color: #E6A23C;
}

.queue-number {
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
  margin-right: 12px;
  min-width: 50px;
}

.queue-info {
  flex: 1;
}

.patient-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.pet-name {
  font-size: 12px;
  color: #999;
}

.section-title {
  margin-top: 20px;
  margin-bottom: 10px;
  font-weight: bold;
  color: #333;
}

.patient-card {
  min-height: calc(100vh - 160px);
}

.history-list {
  max-height: calc(100vh - 200px);
  overflow-y: auto;
}

.history-item {
  padding: 10px;
  margin-bottom: 8px;
  border: 1px solid #eee;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.history-item:hover {
  background-color: #f5f7fa;
  border-color: #409EFF;
}

.history-date {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.history-diagnosis {
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.prescription-list {
  margin-top: 20px;
}

.prescription-list h4 {
  margin-bottom: 10px;
}

.examination-section {
  padding: 15px;
  background-color: #fef0f0;
  border-radius: 4px;
  margin: 15px 0;
}
</style>
