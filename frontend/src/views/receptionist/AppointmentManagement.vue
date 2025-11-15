<template>
  <div class="appointment-management">
    <el-card class="appointment-card">
      <template #header>
        <div class="card-header">
          <span>预约管理</span>
          <div class="header-actions">
            <el-radio-group v-model="viewMode" @change="loadAppointments" style="margin-right: 10px;">
              <el-radio-button label="all">所有预约</el-radio-button>
              <el-radio-button label="pending">待确认</el-radio-button>
              <el-radio-button label="today">今日预约</el-radio-button>
            </el-radio-group>
            <el-button type="primary" @click="loadAppointments" icon="el-icon-refresh">刷新</el-button>
          </div>
        </div>
      </template>

      <!-- 预约列表 -->
      <el-table :data="appointments" style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60"></el-table-column>
        <el-table-column prop="appointmentTime" label="预约时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.appointmentTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="petName" label="宠物名称" width="120"></el-table-column>
        <el-table-column prop="customerName" label="客户姓名" width="120"></el-table-column>
        <el-table-column prop="doctorName" label="医生" width="120"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="notes" label="备注" min-width="150"></el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
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
              type="primary"
              @click="viewDetail(scope.row)"
            >
              查看
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

      <el-empty v-if="appointments.length === 0 && !loading" description="暂无预约记录" :image-size="150"></el-empty>
    </el-card>

    <!-- 预约详情对话框 -->
    <el-dialog title="预约详情" v-model="detailDialogVisible" width="600px">
      <el-descriptions :column="2" border v-if="currentAppointment">
        <el-descriptions-item label="预约ID">{{ currentAppointment.id }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentAppointment.status)">
            {{ getStatusText(currentAppointment.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="预约时间" :span="2">
          {{ formatDateTime(currentAppointment.appointmentTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="客户姓名">{{ currentAppointment.customerName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentAppointment.customerPhone || '未提供' }}</el-descriptions-item>
        <el-descriptions-item label="宠物名称">{{ currentAppointment.petName }}</el-descriptions-item>
        <el-descriptions-item label="宠物种类">{{ currentAppointment.petSpecies }}</el-descriptions-item>
        <el-descriptions-item label="医生">{{ currentAppointment.doctorName }}</el-descriptions-item>
        <el-descriptions-item label="科室">{{ currentAppointment.doctorDepartment || '未知' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ currentAppointment.notes || '无' }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button 
          v-if="currentAppointment && currentAppointment.status === 'PENDING'"
          type="success"
          @click="confirmAppointment(currentAppointment)">
          确认预约
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus';
import api from '@/services/api';

export default {
  name: 'AppointmentManagement',
  data() {
    return {
      loading: false,
      viewMode: 'all', // all, pending, today
      appointments: [],
      detailDialogVisible: false,
      currentAppointment: null
    };
  },
  mounted() {
    this.loadAppointments();
  },
  methods: {
    async loadAppointments() {
      this.loading = true;
      try {
        let url = '/api/appointments';
        
        // 根据视图模式选择不同API
        if (this.viewMode === 'pending') {
          url = '/api/appointments/pending';
        } else if (this.viewMode === 'today') {
          url = '/api/appointments/today';
        }
        
        const response = await api.get(url);
        this.appointments = response.data.success ? response.data.data : response.data;
        
        // 获取详细信息
        await this.enrichAppointmentData();
      } catch (error) {
        ElMessage.error('获取预约列表失败: ' + error.message);
      } finally {
        this.loading = false;
      }
    },

    async enrichAppointmentData() {
      for (let appointment of this.appointments) {
        try {
          // 获取客户信息
          const customerResp = await api.get(`/api/customers/${appointment.customerId}`);
          if (customerResp.data.success) {
            appointment.customerName = customerResp.data.data.realName;
            appointment.customerPhone = customerResp.data.data.phone;
          }
          
          // 获取宠物信息
          const petResp = await api.get(`/api/users/pet/${appointment.petId}`);
          if (petResp.data) {
            appointment.petName = petResp.data.name;
            appointment.petSpecies = petResp.data.species;
          }
          
          // 获取医生信息
          const doctorResp = await api.get(`/api/doctors/${appointment.doctorId}`);
          if (doctorResp.data) {
            appointment.doctorName = doctorResp.data.realName;
            appointment.doctorDepartment = doctorResp.data.department;
          }
        } catch (error) {
          console.error('获取预约详情失败:', error);
        }
      }
    },

    viewDetail(appointment) {
      this.currentAppointment = { ...appointment };
      this.detailDialogVisible = true;
    },

    async confirmAppointment(appointment) {
      try {
        await ElMessageBox.confirm(
          `确认预约：${appointment.petName} - ${appointment.customerName}?`,
          '确认操作',
          {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'success'
          }
        );

        await api.put(`/api/appointments/${appointment.id}/confirm`);
        ElMessage.success('预约已确认');
        this.detailDialogVisible = false;
        this.loadAppointments();
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('确认失败: ' + error.message);
        }
      }
    },

    async cancelAppointment(appointment) {
      try {
        await ElMessageBox.confirm(
          `确定要取消预约：${appointment.petName} - ${appointment.customerName}?`,
          '警告',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        );

        await api.put(`/api/appointments/${appointment.id}/cancel`);
        ElMessage.success('预约已取消');
        this.loadAppointments();
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('取消失败: ' + error.message);
        }
      }
    },

    formatDateTime(dateTime) {
      if (!dateTime) return '';
      return new Date(dateTime).toLocaleString('zh-CN');
    },

    getStatusType(status) {
      const typeMap = {
        'PENDING': 'warning',
        'CONFIRMED': 'primary',
        'IN_PROGRESS': 'success',
        'COMPLETED': 'info',
        'CANCELLED': 'danger'
      };
      return typeMap[status] || 'info';
    },

    getStatusText(status) {
      const textMap = {
        'PENDING': '待取号',
        'CONFIRMED': '待就诊',
        'IN_PROGRESS': '就诊中',
        'COMPLETED': '已就诊',
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