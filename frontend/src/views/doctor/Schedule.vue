<template>
  <div class="schedule-container">
    <el-card class="schedule-card">
      <template #header>
        <div class="card-header">
          <span>我的排班</span>
        </div>
      </template>
      
      <div v-if="loading" class="loading">
        <el-skeleton :rows="5" animated />
      </div>
      
      <div v-else>
        <el-table :data="scheduleData" style="width: 100%" stripe>
          <el-table-column prop="scheduleDate" label="日期" width="120">
            <template #default="scope">
              {{ formatDate(scope.row.scheduleDate) }}
            </template>
          </el-table-column>
          <el-table-column prop="weekday" label="星期" width="80">
            <template #default="scope">
              {{ getWeekdayName(scope.row.weekday) }}
            </template>
          </el-table-column>
          <el-table-column prop="shiftType" label="班次" width="150">
            <template #default="scope">
              <el-tag :type="getShiftType(scope.row.shiftType)">
                {{ getShiftTypeName(scope.row.shiftType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="startTime" label="开始时间" width="100">
            <template #default="scope">
              {{ scope.row.startTime }}
            </template>
          </el-table-column>
          <el-table-column prop="endTime" label="结束时间" width="100">
            <template #default="scope">
              {{ scope.row.endTime }}
            </template>
          </el-table-column>
          <el-table-column prop="department" label="科室" width="120" />
          <el-table-column prop="notes" label="备注">
            <template #default="scope">
              <span v-if="scope.row.notes">{{ scope.row.notes }}</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="total, prev, pager, next, jumper"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>
    
    <el-card class="info-card">
      <template #header>
        <div class="card-header">
          <span>排班说明</span>
        </div>
      </template>
      <div class="info-content">
        <p><i class="el-icon-info"></i> 排班由医院人事部门统一安排，如有疑问请联系相关负责人。</p>
        <p><i class="el-icon-info"></i> 如需调班，请提前3天向科室主任申请。</p>
        <p><i class="el-icon-info"></i> 班次类型：</p>
        <ul>
          <li><el-tag type="primary" size="small">早班</el-tag> 08:00-12:00</li>
          <li><el-tag type="success" size="small">下午班</el-tag> 14:00-18:00</li>
          <li><el-tag type="warning" size="small">晚班</el-tag> 18:00-22:00</li>
          <li><el-tag type="danger" size="small">夜班</el-tag> 22:00-08:00（次日）</li>
          <li><el-tag type="info" size="small">全天</el-tag> 08:00-18:00</li>
        </ul>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus';
import api from '@/services/api';

export default {
  name: 'DoctorSchedule',
  data() {
    return {
      loading: true,
      scheduleData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      doctorId: null
    };
  },
  mounted() {
    // 从localStorage获取当前医生ID
    this.doctorId = localStorage.getItem('userId') || 1;
    this.fetchScheduleData();
  },
  methods: {
    async fetchScheduleData() {
      this.loading = true;
      try {
        const response = await api.get(`/api/doctor/schedule/${this.doctorId}`);
        
        if (response.data.success) {
          this.scheduleData = response.data.data;
          this.total = this.scheduleData.length;
        } else {
          ElMessage.error(response.data.message);
        }
      } catch (error) {
        ElMessage.error('获取排班信息失败: ' + error.message);
      } finally {
        this.loading = false;
      }
    },
    
    handleCurrentChange(page) {
      this.currentPage = page;
      // 这里可以添加分页逻辑
    },
    
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      });
    },
    
    getWeekdayName(weekday) {
      const weekdays = ['日', '一', '二', '三', '四', '五', '六'];
      return '周' + (weekdays[weekday] || '');
    },
    
    getShiftType(shiftType) {
      const typeMap = {
        'MORNING': 'primary',
        'AFTERNOON': 'success',
        'EVENING': 'warning',
        'NIGHT': 'danger',
        'FULL_DAY': 'info'
      };
      return typeMap[shiftType] || 'info';
    },
    
    getShiftTypeName(shiftType) {
      const nameMap = {
        'MORNING': '早班',
        'AFTERNOON': '下午班',
        'EVENING': '晚班',
        'NIGHT': '夜班',
        'FULL_DAY': '全天'
      };
      return nameMap[shiftType] || shiftType;
    }
  }
};
</script>

<style scoped>
.schedule-container {
  padding: 20px;
}

.schedule-card {
  margin-bottom: 20px;
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.info-card {
  margin-bottom: 20px;
}

.info-content p {
  margin: 10px 0;
  line-height: 1.6;
}

.info-content ul {
  margin: 10px 0 10px 20px;
}

.info-content li {
  margin: 5px 0;
  line-height: 1.6;
}

.loading {
  padding: 20px 0;
}
</style>