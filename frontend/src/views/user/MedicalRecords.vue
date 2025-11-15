<template>
  <div class="medical-records-page">
    <el-card class="medical-records-card">
      <template #header>
        <div class="card-header">
          <span>病例记录</span>
        </div>
      </template>
      
      <!-- 病例列表 -->
      <el-table :data="medicalRecords" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="病例ID" align="center"></el-table-column>
        <el-table-column prop="petName" label="宠物名称" align="center"></el-table-column>
        <el-table-column prop="doctorName" label="医生" align="center"></el-table-column>
        <el-table-column prop="visitDate" label="就诊日期" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.visitDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="symptoms" label="症状" show-overflow-tooltip align="center"></el-table-column>
        <el-table-column prop="diagnosis" label="诊断结果" show-overflow-tooltip align="center"></el-table-column>
        <el-table-column label="操作" align="center">
          <template #default="scope">
            <div class="operation-buttons">
              <el-button size="small" @click="viewDetail(scope.row)">查看详情</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 无病例提示 -->
      <el-empty description="暂无病例记录" v-if="medicalRecords.length === 0 && !loading"></el-empty>
    </el-card>
    </div>
    
    <!-- 病例详情对话框 -->
    <el-dialog title="病例详情" v-model="detailDialogVisible" width="700px">
      <el-form label-width="120px" :model="currentRecord" disabled>
        <el-row>
          <el-col :span="12">
            <el-form-item label="病例ID:">
              <span>{{ currentRecord.id }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="就诊日期:">
              <span>{{ formatDate(currentRecord.visitDate) }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="12">
            <el-form-item label="宠物名称:">
              <span>{{ currentRecord.petName }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="医生:">
              <span>{{ currentRecord.doctorName }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="症状:">
          <span>{{ currentRecord.symptoms }}</span>
        </el-form-item>
        
        <el-form-item label="诊断结果:">
          <span>{{ currentRecord.diagnosis }}</span>
        </el-form-item>
        
        <el-form-item label="治疗方案:">
          <span>{{ currentRecord.treatment }}</span>
        </el-form-item>
        
        <el-form-item label="备注:">
          <span>{{ currentRecord.notes }}</span>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
</template>

<script>
import api from '@/services/api';

export default {
  name: 'MedicalRecords',
  data() {
    return {
      loading: true,
      medicalRecords: [],
      detailDialogVisible: false,
      currentRecord: {}
    };
  },
  mounted() {
    this.loadMedicalRecords();
  },
  methods: {
    // 加载病例记录
    async loadMedicalRecords() {
      this.loading = true;
      try {
        const userId = localStorage.getItem('userId');
        if (userId) {
          // 先获取用户的所有宠物
          const petsResponse = await api.get(`/api/users/${userId}/pets`);
          const pets = petsResponse.data.success ? petsResponse.data.data : petsResponse.data;
          
          // 获取每个宠物的病例记录
          const allRecords = [];
          for (const pet of pets) {
            try {
              const recordsResponse = await api.get(`/api/medical-records/pet/${pet.id}`);
              const records = recordsResponse.data.map(record => ({
                ...record,
                petName: pet.name,
                visitDate: record.createdAt // 使用createdAt作为就诊日期
              }));
              allRecords.push(...records);
            } catch (error) {
              console.error(`获取宠物 ${pet.name} 的病例记录失败:`, error);
            }
          }
          
          // 按就诊日期倒序排列
          this.medicalRecords = allRecords.sort((a, b) => 
            new Date(b.visitDate || b.createdAt) - new Date(a.visitDate || a.createdAt)
          );
        }
      } catch (error) {
        console.error('加载病例记录失败:', error);
        this.$message.error('加载病例记录失败: ' + (error.response?.data || error.message));
      } finally {
        this.loading = false;
      }
    },
    
    // 查看详情
    viewDetail(record) {
      this.currentRecord = { ...record };
      this.detailDialogVisible = true;
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
    }
  }
};
</script>

<style scoped>
.medical-records-page {
  width: 100%;
  height: 100%;
}

.medical-records-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.medical-records-card :deep(.el-card__body) {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.medical-records-card :deep(.el-table) {
  width: 100%;
}

.medical-records-card :deep(.el-empty) {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  text-align: right;
}

.operation-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: nowrap;
}
</style>