<template>
  <div class="doctor-schedule">
    <el-card class="schedule-card">
      <template #header>
        <div class="card-header">
          <span>医生排班管理</span>
          <el-button type="primary" @click="showAddScheduleDialog">添加排班</el-button>
        </div>
      </template>

      <!-- 医生选择和日期筛选 -->
      <div class="filter-bar">
        <el-select v-model="selectedDoctorId" placeholder="选择医生" @change="loadSchedules" style="width: 200px; margin-right: 10px;">
          <el-option label="全部医生" :value="null"></el-option>
          <el-option 
            v-for="doctor in doctors" 
            :key="doctor.id" 
            :label="doctor.realName" 
            :value="doctor.id">
          </el-option>
        </el-select>
        
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="loadSchedules"
          style="margin-right: 10px;">
        </el-date-picker>
        
        <el-button type="primary" @click="loadSchedules">查询</el-button>
      </div>

      <!-- 排班列表 -->
      <el-table :data="schedules" style="width: 100%; margin-top: 20px;" v-loading="loading">
        <el-table-column prop="id" label="排班ID" width="80"></el-table-column>
        <el-table-column prop="doctorName" label="医生姓名" width="120"></el-table-column>
        <el-table-column prop="scheduleDate" label="排班日期" width="120">
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
            <el-tag :type="getShiftTypeColor(scope.row.shiftType)">
              {{ getShiftTypeText(scope.row.shiftType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="时间段" width="150">
          <template #default="scope">
            {{ scope.row.startTime }} - {{ scope.row.endTime }}
          </template>
        </el-table-column>
        <el-table-column prop="department" label="科室" width="100"></el-table-column>
        <el-table-column prop="notes" label="备注"></el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="showEditScheduleDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteSchedule(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑排班对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
      <el-form :model="scheduleForm" :rules="scheduleRules" ref="scheduleFormRef" label-width="100px">
        <el-form-item label="选择医生" prop="doctorId">
          <el-select v-model="scheduleForm.doctorId" placeholder="请选择医生" style="width: 100%;">
            <el-option 
              v-for="doctor in doctors" 
              :key="doctor.id" 
              :label="doctor.realName" 
              :value="doctor.id">
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="排班日期" prop="scheduleDate">
          <el-date-picker
            v-model="scheduleForm.scheduleDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%;"
            @change="updateWeekday">
          </el-date-picker>
        </el-form-item>
        
        <el-form-item label="班次类型" prop="shiftType">
          <el-select v-model="scheduleForm.shiftType" placeholder="请选择班次" style="width: 100%;" @change="updateShiftTime">
            <el-option label="上午班" value="MORNING"></el-option>
            <el-option label="下午班" value="AFTERNOON"></el-option>
            <el-option label="晚班" value="EVENING"></el-option>
            <el-option label="夜班" value="NIGHT"></el-option>
            <el-option label="全天班" value="FULL_DAY"></el-option>
          </el-select>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-time-picker
                v-model="scheduleForm.startTime"
                format="HH:mm"
                value-format="HH:mm:ss"
                placeholder="选择时间"
                style="width: 100%;">
              </el-time-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-time-picker
                v-model="scheduleForm.endTime"
                format="HH:mm"
                value-format="HH:mm:ss"
                placeholder="选择时间"
                style="width: 100%;">
              </el-time-picker>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="科室" prop="department">
          <el-input v-model="scheduleForm.department" placeholder="请输入科室"></el-input>
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input v-model="scheduleForm.notes" type="textarea" placeholder="请输入备注"></el-input>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveSchedule">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus';
import api from '@/services/api';

export default {
  name: 'DoctorSchedule',
  data() {
    return {
      loading: false,
      doctors: [],
      schedules: [],
      selectedDoctorId: null,
      dateRange: null,
      dialogVisible: false,
      isEdit: false,
      scheduleForm: {
        id: null,
        doctorId: null,
        scheduleDate: '',
        weekday: null,
        shiftType: '',
        startTime: '',
        endTime: '',
        department: '',
        notes: ''
      },
      scheduleRules: {
        doctorId: [
          { required: true, message: '请选择医生', trigger: 'change' }
        ],
        scheduleDate: [
          { required: true, message: '请选择排班日期', trigger: 'change' }
        ],
        shiftType: [
          { required: true, message: '请选择班次类型', trigger: 'change' }
        ],
        startTime: [
          { required: true, message: '请选择开始时间', trigger: 'change' }
        ],
        endTime: [
          { required: true, message: '请选择结束时间', trigger: 'change' }
        ]
      }
    };
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑排班' : '添加排班';
    }
  },
  mounted() {
    this.loadDoctors();
    this.loadSchedules();
  },
  methods: {
    async loadDoctors() {
      try {
        const response = await api.get('/api/doctors');
        this.doctors = response.data;
      } catch (error) {
        ElMessage.error('获取医生列表失败: ' + error.message);
      }
    },

    async loadSchedules() {
      this.loading = true;
      try {
        let url = '/api/schedules';
        const params = {};
        
        if (this.selectedDoctorId) {
          params.doctorId = this.selectedDoctorId;
        }
        
        if (this.dateRange && this.dateRange.length === 2) {
          params.startDate = this.formatDateForApi(this.dateRange[0]);
          params.endDate = this.formatDateForApi(this.dateRange[1]);
        }
        
        const response = await api.get(url, { params });
        this.schedules = response.data;
        
        // 补充医生姓名
        this.schedules = this.schedules.map(schedule => {
          const doctor = this.doctors.find(d => d.id === schedule.doctorId);
          return {
            ...schedule,
            doctorName: doctor ? doctor.realName : '未知医生'
          };
        });
      } catch (error) {
        ElMessage.error('获取排班列表失败: ' + error.message);
      } finally {
        this.loading = false;
      }
    },

    showAddScheduleDialog() {
      this.isEdit = false;
      this.scheduleForm = {
        id: null,
        doctorId: null,
        scheduleDate: '',
        weekday: null,
        shiftType: '',
        startTime: '',
        endTime: '',
        department: '',
        notes: ''
      };
      this.dialogVisible = true;
    },

    showEditScheduleDialog(schedule) {
      this.isEdit = true;
      this.scheduleForm = { ...schedule };
      this.dialogVisible = true;
    },

    async saveSchedule() {
      this.$refs.scheduleFormRef.validate(async (valid) => {
        if (valid) {
          try {
            if (this.isEdit) {
              await api.put(`/api/schedules/${this.scheduleForm.id}`, this.scheduleForm);
              ElMessage.success('排班更新成功');
            } else {
              await api.post('/api/schedules', this.scheduleForm);
              ElMessage.success('排班添加成功');
            }
            
            this.dialogVisible = false;
            this.loadSchedules();
          } catch (error) {
            ElMessage.error('保存排班失败: ' + error.message);
          }
        }
      });
    },

    deleteSchedule(id) {
      ElMessageBox.confirm('确定要删除这个排班吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await api.delete(`/api/schedules/${id}`);
          ElMessage.success('排班删除成功');
          this.loadSchedules();
        } catch (error) {
          ElMessage.error('删除排班失败: ' + error.message);
        }
      }).catch(() => {});
    },

    updateWeekday(date) {
      if (date) {
        const d = new Date(date);
        this.scheduleForm.weekday = d.getDay() === 0 ? 7 : d.getDay();
      }
    },

    updateShiftTime(shiftType) {
      const shiftTimes = {
        'MORNING': { start: '08:00:00', end: '12:00:00' },
        'AFTERNOON': { start: '14:00:00', end: '18:00:00' },
        'EVENING': { start: '18:00:00', end: '22:00:00' },
        'NIGHT': { start: '22:00:00', end: '08:00:00' },
        'FULL_DAY': { start: '08:00:00', end: '18:00:00' }
      };
      
      if (shiftTimes[shiftType]) {
        this.scheduleForm.startTime = shiftTimes[shiftType].start;
        this.scheduleForm.endTime = shiftTimes[shiftType].end;
      }
    },

    formatDate(date) {
      if (!date) return '';
      return new Date(date).toLocaleDateString('zh-CN');
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
    },

    getShiftTypeColor(shiftType) {
      const colorMap = {
        'MORNING': '',
        'AFTERNOON': 'success',
        'EVENING': 'warning',
        'NIGHT': 'info',
        'FULL_DAY': 'danger'
      };
      return colorMap[shiftType] || '';
    }
  }
};
</script>

<style scoped>
.doctor-schedule {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-bar {
  display: flex;
  align-items: center;
}
</style>
