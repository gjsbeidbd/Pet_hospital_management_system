<template>
  <div class="appointment-management">
    <el-card class="appointment-card">
      <template #header>
        <div class="card-header">
          <span>预约管理</span>
          <div class="header-actions">
            <el-date-picker
              v-model="selectedDate"
              type="date"
              placeholder="选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 150px; margin-right: 10px;"
              @change="loadAppointments"
            />
            <el-button type="primary" @click="showAddAppointmentDialog">添加预约</el-button>
          </div>
        </div>
      </template>

      <!-- 预约列表 -->
      <el-table :data="appointments" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="appointmentTime" label="预约时间" width="180"></el-table-column>
        <el-table-column prop="petName" label="宠物名称" width="120"></el-table-column>
        <el-table-column prop="ownerName" label="主人姓名" width="120"></el-table-column>
        <el-table-column prop="doctorName" label="医生" width="120"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="notes" label="备注"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="showEditAppointmentDialog(scope.row)">编辑</el-button>
            <el-button 
              size="small" 
              type="success" 
              @click="confirmAppointment(scope.row)" 
              v-if="scope.row.status === 'PENDING'"
            >
              确认
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="cancelAppointment(scope.row)"
              v-if="scope.row.status !== 'CANCELLED' && scope.row.status !== 'COMPLETED'"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑预约对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="appointmentForm" :rules="appointmentRules" ref="appointmentFormRef" label-width="100px">
        <el-form-item label="预约时间" prop="appointmentTime">
          <el-date-picker
            v-model="appointmentForm.appointmentTime"
            type="datetime"
            placeholder="选择预约时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="宠物名称" prop="petName">
          <el-input v-model="appointmentForm.petName"></el-input>
        </el-form-item>
        <el-form-item label="主人姓名" prop="ownerName">
          <el-input v-model="appointmentForm.ownerName"></el-input>
        </el-form-item>
        <el-form-item label="医生" prop="doctorName">
          <el-select v-model="appointmentForm.doctorName" placeholder="请选择医生" style="width: 100%">
            <el-option label="李医生" value="李医生"></el-option>
            <el-option label="王医生" value="王医生"></el-option>
            <el-option label="张医生" value="张医生"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="notes">
          <el-input v-model="appointmentForm.notes" type="textarea"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveAppointment">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus';

export default {
  name: 'AppointmentManagement',
  data() {
    return {
      loading: false,
      appointments: [],
      selectedDate: '',
      dialogVisible: false,
      isEdit: false,
      appointmentForm: {
        id: null,
        appointmentTime: '',
        petName: '',
        ownerName: '',
        doctorName: '',
        status: 'PENDING',
        notes: ''
      },
      appointmentRules: {
        appointmentTime: [
          { required: true, message: '请选择预约时间', trigger: 'change' }
        ],
        petName: [
          { required: true, message: '请输入宠物名称', trigger: 'blur' }
        ],
        ownerName: [
          { required: true, message: '请输入主人姓名', trigger: 'blur' }
        ],
        doctorName: [
          { required: true, message: '请选择医生', trigger: 'change' }
        ]
      }
    };
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑预约' : '添加预约';
    }
  },
  mounted() {
    // 默认选择今天
    const today = new Date();
    this.selectedDate = today.toISOString().split('T')[0];
    this.loadAppointments();
  },
  methods: {
    loadAppointments() {
      this.loading = true;
      try {
        // 暂时使用模拟数据
        this.appointments = [
          {
            id: 1,
            appointmentTime: '2023-06-15 09:00',
            petName: '小白',
            ownerName: '张三',
            doctorName: '李医生',
            status: 'CONFIRMED',
            notes: '定期体检'
          },
          {
            id: 2,
            appointmentTime: '2023-06-15 10:30',
            petName: '小黑',
            ownerName: '李四',
            doctorName: '王医生',
            status: 'PENDING',
            notes: '呕吐腹泻'
          },
          {
            id: 3,
            appointmentTime: '2023-06-15 14:00',
            petName: '花花',
            ownerName: '王五',
            doctorName: '张医生',
            status: 'COMPLETED',
            notes: '疫苗接种'
          }
        ];
      } catch (error) {
        ElMessage.error('获取预约列表失败: ' + error.message);
      } finally {
        this.loading = false;
      }
    },

    showAddAppointmentDialog() {
      this.isEdit = false;
      this.appointmentForm = {
        id: null,
        appointmentTime: '',
        petName: '',
        ownerName: '',
        doctorName: '',
        status: 'PENDING',
        notes: ''
      };
      this.dialogVisible = true;
    },

    showEditAppointmentDialog(appointment) {
      this.isEdit = true;
      this.appointmentForm = { ...appointment };
      this.dialogVisible = true;
    },

    saveAppointment() {
      this.$refs.appointmentFormRef.validate((valid) => {
        if (valid) {
          if (this.isEdit) {
            // 编辑预约
            const index = this.appointments.findIndex(a => a.id === this.appointmentForm.id);
            if (index !== -1) {
              this.appointments.splice(index, 1, { ...this.appointmentForm });
              ElMessage.success('预约信息更新成功');
            }
          } else {
            // 添加预约
            const newAppointment = {
              ...this.appointmentForm,
              id: this.appointments.length + 1
            };
            this.appointments.push(newAppointment);
            ElMessage.success('预约添加成功');
          }
          this.dialogVisible = false;
        }
      });
    },

    confirmAppointment(appointment) {
      const index = this.appointments.findIndex(a => a.id === appointment.id);
      if (index !== -1) {
        this.appointments[index].status = 'CONFIRMED';
        ElMessage.success('预约已确认');
      }
    },

    cancelAppointment(appointment) {
      ElMessageBox.confirm('确定要取消这个预约吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const index = this.appointments.findIndex(a => a.id === appointment.id);
        if (index !== -1) {
          this.appointments[index].status = 'CANCELLED';
          ElMessage.success('预约已取消');
        }
      }).catch(() => {
        // 用户取消操作
      });
    },

    getStatusType(status) {
      const statusMap = {
        'PENDING': 'info',
        'CONFIRMED': 'warning',
        'COMPLETED': 'success',
        'CANCELLED': 'danger'
      };
      return statusMap[status] || 'info';
    },

    getStatusText(status) {
      const textMap = {
        'PENDING': '待确认',
        'CONFIRMED': '已确认',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
      };
      return textMap[status] || status;
    }
  }
};
</script>

<style scoped>
.appointment-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}
</style>