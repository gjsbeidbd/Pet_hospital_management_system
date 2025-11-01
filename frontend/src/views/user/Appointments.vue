<template>
  <div class="appointments-container">
    <el-card class="appointments-card">
      <template #header>
        <div class="card-header">
          <span>我的预约</span>
        </div>
      </template>
      
      <!-- 预约列表 -->
      <el-table :data="appointments" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="预约ID" width="80"></el-table-column>
        <el-table-column prop="petName" label="宠物名称" width="120"></el-table-column>
        <el-table-column prop="doctorName" label="医生" width="120"></el-table-column>
        <el-table-column prop="appointmentTime" label="预约时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.appointmentTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" @click="viewDetail(scope.row)">查看详情</el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="cancelAppointment(scope.row)" 
              :disabled="scope.row.status !== 'PENDING'"
              v-if="scope.row.status === 'PENDING' || scope.row.status === 'CONFIRMED'">
              取消预约
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 无预约提示 -->
      <el-empty description="暂无预约记录" v-if="appointments.length === 0 && !loading"></el-empty>
    </el-card>
    
    <!-- 预约详情对话框 -->
    <el-dialog title="预约详情" v-model="detailDialogVisible" width="600px">
      <el-form label-width="120px" :model="currentAppointment" disabled>
        <el-row>
          <el-col :span="12">
            <el-form-item label="预约ID:">
              <span>{{ currentAppointment.id }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预约时间:">
              <span>{{ formatDate(currentAppointment.appointmentTime) }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="12">
            <el-form-item label="宠物名称:">
              <span>{{ currentAppointment.petName }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="医生:">
              <span>{{ currentAppointment.doctorName }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="状态:">
          <el-tag :type="getStatusType(currentAppointment.status)">
            {{ getStatusText(currentAppointment.status) }}
          </el-tag>
        </el-form-item>
        
        <el-form-item label="备注:">
          <span>{{ currentAppointment.notes }}</span>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import api from '@/services/api';

export default {
  name: 'UserAppointments',
  data() {
    return {
      loading: true,
      appointments: [],
      detailDialogVisible: false,
      currentAppointment: {}
    };
  },
  mounted() {
    this.loadAppointments();
  },
  methods: {
    // 加载预约列表
    async loadAppointments() {
      this.loading = true;
      try {
        const userId = localStorage.getItem('userId');
        if (userId) {
          // 先获取用户的客户信息
          const customerResponse = await api.get(`/api/users/${userId}/customer`);
          const customerId = customerResponse.data.id;
          
          // 获取预约列表
          const response = await api.get(`/api/appointments/customer/${customerId}`);
          const appointments = response.data;
          
          // 获取宠物和医生信息
          const petMap = {};
          const doctorMap = {};
          
          // 获取用户的所有宠物
          const petsResponse = await api.get(`/api/users/${userId}/pets`);
          const pets = petsResponse.data;
          pets.forEach(pet => {
            petMap[pet.id] = pet.name;
          });
          
          // 获取所有医生（这里简化处理，实际项目中可能需要分页）
          // 由于没有获取所有医生的接口，我们暂时只显示ID
          
          // 为预约添加宠物和医生名称
          this.appointments = appointments.map(appointment => ({
            ...appointment,
            petName: petMap[appointment.petId] || '未知宠物',
            doctorName: `医生${appointment.doctorId}` // 简化处理
          }));
        }
      } catch (error) {
        console.error('加载预约列表失败:', error);
        this.$message.error('加载预约列表失败: ' + (error.response?.data || error.message));
      } finally {
        this.loading = false;
      }
    },
    
    // 查看详情
    viewDetail(appointment) {
      this.currentAppointment = { ...appointment };
      this.detailDialogVisible = true;
    },
    
    // 取消预约
    cancelAppointment(appointment) {
      this.$confirm('确定要取消这个预约吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          // 这里应该调用取消预约的接口
          // 由于目前没有提供取消预约的接口，我们暂时只显示提示
          this.$message.info('预约取消功能将在后续版本中实现');
          
          // 重新加载预约列表
          await this.loadAppointments();
        } catch (error) {
          console.error('取消预约失败:', error);
          this.$message.error('取消预约失败: ' + (error.response?.data || error.message));
        }
      }).catch(() => {
        // 用户取消操作
      });
    },
    
    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    
    // 获取状态类型
    getStatusType(status) {
      const statusMap = {
        'PENDING': 'info',
        'CONFIRMED': 'warning',
        'COMPLETED': 'success',
        'CANCELLED': 'danger'
      };
      return statusMap[status] || 'info';
    },
    
    // 获取状态文本
    getStatusText(status) {
      const statusTextMap = {
        'PENDING': '待确认',
        'CONFIRMED': '已确认',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
      };
      return statusTextMap[status] || status;
    }
  }
};
</script>

<style scoped>
.appointments-container {
  padding: 20px;
}

.appointments-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  text-align: right;
}
</style>