<template>
  <div class="make-appointment">
    <el-card class="appointment-card">
      <template #header>
        <div class="card-header">
          <span>预约挂号</span>
        </div>
      </template>

      <el-steps :active="activeStep" finish-status="success" align-center>
        <el-step title="选择医生和时间"></el-step>
        <el-step title="选择宠物"></el-step>
        <el-step title="填写信息"></el-step>
        <el-step title="确认提交"></el-step>
      </el-steps>

      <!-- 步骤1: 选择医生和时间 -->
      <div v-show="activeStep === 0" class="step-content">
        <h3>请选择医生和排班时间</h3>
        
        <el-form label-width="100px">
          <el-form-item label="选择医生">
            <el-select v-model="selectedDoctorId" placeholder="请选择医生" @change="loadDoctorSchedules" style="width: 100%;">
              <el-option 
                v-for="doctor in doctors" 
                :key="doctor.id" 
                :label="`${doctor.realName} - ${doctor.department || '综合科'}`" 
                :value="doctor.id">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>

        <div v-if="doctorSchedules.length > 0" class="schedules-list">
          <h4>可预约排班</h4>
          <el-table :data="filteredSchedules" style="width: 100%">
            <el-table-column prop="scheduleDate" label="日期" width="120">
              <template #default="scope">
                {{ formatDate(scope.row.scheduleDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="weekday" label="星期" width="80">
              <template #default="scope">
                {{ getWeekdayText(scope.row.weekday) }}
              </template>
            </el-table-column>
            <el-table-column prop="shiftType" label="班次" width="100">
              <template #default="scope">
                {{ getShiftTypeText(scope.row.shiftType) }}
              </template>
            </el-table-column>
            <el-table-column label="时间段" width="150">
              <template #default="scope">
                {{ scope.row.startTime }} - {{ scope.row.endTime }}
              </template>
            </el-table-column>
            <el-table-column prop="department" label="科室"></el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="small" type="primary" @click="selectSchedule(scope.row)">选择</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <el-empty v-else-if="selectedDoctorId" description="该医生暂无可预约排班"></el-empty>
      </div>

      <!-- 步骤2: 选择宠物 -->
      <div v-show="activeStep === 1" class="step-content">
        <h3>请选择就诊宠物</h3>
        <el-form label-width="100px">
          <el-form-item label="选择宠物">
            <el-select v-model="appointmentForm.petId" placeholder="请选择宠物" style="width: 100%;">
              <el-option 
                v-for="pet in pets" 
                :key="pet.id" 
                :label="`${pet.name} - ${pet.species}`" 
                :value="pet.id">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <el-empty v-if="pets.length === 0" description="您还没有宠物，请先添加宠物信息"></el-empty>
      </div>

      <!-- 步骤3: 填写信息 -->
      <div v-show="activeStep === 2" class="step-content">
        <h3>填写预约信息</h3>
        <el-form :model="appointmentForm" label-width="120px">
          <el-form-item label="预约时间">
            <el-date-picker
              v-model="appointmentForm.appointmentTime"
              type="datetime"
              placeholder="选择预约时间"
              :disabled-date="disabledDate"
              :disabled-hours="disabledHours"
              style="width: 100%;">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="备注说明">
            <el-input v-model="appointmentForm.notes" type="textarea" rows="4" placeholder="请输入宠物症状或备注信息"></el-input>
          </el-form-item>
        </el-form>
      </div>

      <!-- 步骤4: 确认信息 -->
      <div v-show="activeStep === 3" class="step-content">
        <h3>确认预约信息</h3>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="医生">{{ selectedDoctorName }}</el-descriptions-item>
          <el-descriptions-item label="科室">{{ selectedSchedule?.department || '综合科' }}</el-descriptions-item>
          <el-descriptions-item label="排班时间">
            {{ formatDate(selectedSchedule?.scheduleDate) }} 
            {{ getShiftTypeText(selectedSchedule?.shiftType) }}
            ({{ selectedSchedule?.startTime }} - {{ selectedSchedule?.endTime }})
          </el-descriptions-item>
          <el-descriptions-item label="宠物">{{ selectedPetName }}</el-descriptions-item>
          <el-descriptions-item label="预约时间">{{ formatDateTime(appointmentForm.appointmentTime) }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ appointmentForm.notes || '无' }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 步骤控制按钮 -->
      <div class="step-buttons">
        <el-button v-if="activeStep > 0" @click="prevStep">上一步</el-button>
        <el-button v-if="activeStep < 3" type="primary" @click="nextStep" :disabled="!canNext">下一步</el-button>
        <el-button v-if="activeStep === 3" type="success" @click="submitAppointment" :loading="submitting">提交预约</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus';
import api from '@/services/api';

export default {
  name: 'MakeAppointment',
  data() {
    return {
      activeStep: 0,
      doctors: [],
      doctorSchedules: [],
      pets: [],
      selectedDoctorId: null,
      selectedSchedule: null,
      customerId: null,
      submitting: false,
      appointmentForm: {
        petId: null,
        customerId: null,
        doctorId: null,
        appointmentTime: null,
        status: 'PENDING',
        notes: ''
      }
    };
  },
  computed: {
    filteredSchedules() {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      return this.doctorSchedules.filter(schedule => {
        const scheduleDate = new Date(schedule.scheduleDate);
        return scheduleDate >= today;
      });
    },
    selectedDoctorName() {
      const doctor = this.doctors.find(d => d.id === this.selectedDoctorId);
      return doctor ? doctor.realName : '';
    },
    selectedPetName() {
      const pet = this.pets.find(p => p.id === this.appointmentForm.petId);
      return pet ? pet.name : '';
    },
    canNext() {
      if (this.activeStep === 0) {
        return this.selectedSchedule !== null;
      } else if (this.activeStep === 1) {
        return this.appointmentForm.petId !== null;
      } else if (this.activeStep === 2) {
        return this.appointmentForm.appointmentTime !== null;
      }
      return true;
    }
  },
  mounted() {
    this.loadDoctors();
    this.loadPets();
    this.loadCustomerId();
  },
  methods: {
    async loadDoctors() {
      try {
        // 只加载在职医生
        const response = await api.get('/api/doctors/active');
        this.doctors = response.data;
      } catch (error) {
        ElMessage.error('获取医生列表失败: ' + error.message);
      }
    },

    async loadDoctorSchedules() {
      if (!this.selectedDoctorId) return;
      
      try {
        const today = new Date();
        const endDate = new Date();
        endDate.setDate(today.getDate() + 30); // 查询未来30天的排班

        const response = await api.get('/api/schedules', {
          params: {
            doctorId: this.selectedDoctorId,
            startDate: this.formatDateForApi(today),
            endDate: this.formatDateForApi(endDate)
          }
        });
        this.doctorSchedules = response.data;
      } catch (error) {
        ElMessage.error('获取医生排班失败: ' + error.message);
      }
    },

    async loadPets() {
      try {
        const userId = localStorage.getItem('userId');
        if (userId) {
          const response = await api.get(`/api/users/${userId}/pets`);
          if (response.data.success) {
            this.pets = response.data.data;
          }
        }
      } catch (error) {
        ElMessage.error('获取宠物列表失败: ' + error.message);
      }
    },

    async loadCustomerId() {
      try {
        const userId = localStorage.getItem('userId');
        if (userId) {
          const response = await api.get(`/api/users/${userId}/customer`);
          if (response.data.success) {
            this.customerId = response.data.data.id;
            this.appointmentForm.customerId = this.customerId;
          }
        }
      } catch (error) {
        ElMessage.error('获取客户信息失败: ' + error.message);
      }
    },

    selectSchedule(schedule) {
      this.selectedSchedule = schedule;
      this.appointmentForm.doctorId = schedule.doctorId;
      
      // 自动设置预约时间为排班开始时间
      const scheduleDate = new Date(schedule.scheduleDate);
      const [hours, minutes] = schedule.startTime.split(':');
      scheduleDate.setHours(parseInt(hours), parseInt(minutes), 0);
      this.appointmentForm.appointmentTime = scheduleDate;
      
      this.nextStep();
    },

    prevStep() {
      if (this.activeStep > 0) {
        this.activeStep--;
      }
    },

    nextStep() {
      if (this.activeStep < 3 && this.canNext) {
        this.activeStep++;
      }
    },

    async submitAppointment() {
      this.submitting = true;
      try {
        // 调试日志：检查发送的数据
        console.log('Submitting appointment:', this.appointmentForm);
        console.log('Selected schedule:', this.selectedSchedule);
        console.log('Appointment time:', this.appointmentForm.appointmentTime);
        
        // 格式化时间为ISO格式，确保后端能正确解析
        const appointmentData = {
          ...this.appointmentForm,
          appointmentTime: new Date(this.appointmentForm.appointmentTime).toISOString()
        };
        
        console.log('Final appointment data:', appointmentData);
        
        const response = await api.post('/api/appointments', appointmentData);
        if (response.data.success) {
          ElMessage.success('预约成功！');
          this.$router.push('/user/appointments');
        } else {
          ElMessage.error('预约失败: ' + response.data.message);
        }
      } catch (error) {
        console.error('提交预约错误:', error);
        console.error('错误响应:', error.response);
        // 获取后端返回的错误信息
        const errorMsg = error.response?.data?.message || error.message;
        ElMessage.error('提交预约失败: ' + errorMsg);
      } finally {
        this.submitting = false;
      }
    },

    disabledDate(date) {
      if (!this.selectedSchedule) return true;
      const scheduleDate = new Date(this.selectedSchedule.scheduleDate);
      scheduleDate.setHours(0, 0, 0, 0);
      date.setHours(0, 0, 0, 0);
      return date.getTime() !== scheduleDate.getTime();
    },

    disabledHours() {
      if (!this.selectedSchedule) return [];
      const [startHour] = this.selectedSchedule.startTime.split(':');
      const [endHour] = this.selectedSchedule.endTime.split(':');
      const disabled = [];
      for (let i = 0; i < 24; i++) {
        if (i < parseInt(startHour) || i >= parseInt(endHour)) {
          disabled.push(i);
        }
      }
      return disabled;
    },

    formatDate(date) {
      if (!date) return '';
      return new Date(date).toLocaleDateString('zh-CN');
    },

    formatDateTime(date) {
      if (!date) return '';
      return new Date(date).toLocaleString('zh-CN');
    },

    formatDateForApi(date) {
      const d = new Date(date);
      const year = d.getFullYear();
      const month = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    },

    getWeekdayText(weekday) {
      const weekdayMap = {
        1: '周一', 2: '周二', 3: '周三', 4: '周四',
        5: '周五', 6: '周六', 7: '周日'
      };
      return weekdayMap[weekday] || '';
    },

    getShiftTypeText(shiftType) {
      const shiftTypeMap = {
        'MORNING': '上午班',
        'AFTERNOON': '下午班',
        'EVENING': '晚班',
        'NIGHT': '夜班',
        'FULL_DAY': '全天班'
      };
      return shiftTypeMap[shiftType] || shiftType;
    }
  }
};
</script>

<style scoped>
.make-appointment {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.step-content {
  margin: 30px 0;
  min-height: 300px;
}

.step-content h3 {
  margin-bottom: 20px;
  color: #333;
}

.schedules-list {
  margin-top: 20px;
}

.schedules-list h4 {
  margin-bottom: 10px;
  color: #666;
}

.step-buttons {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
</style>
