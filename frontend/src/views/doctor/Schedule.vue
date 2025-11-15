<template>
  <div class="schedule-container">
    <el-card class="schedule-card">
      <template #header>
        <div class="card-header">
          <span>我的排班</span>
          <div class="header-actions">
            <el-radio-group v-model="viewMode" size="small">
              <el-radio-button label="calendar">课程表视图</el-radio-button>
              <el-radio-button label="list">列表视图</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>
      
      <div v-if="loading" class="loading">
        <el-skeleton :rows="5" animated />
      </div>
      
      <!-- 课程表视图 -->
      <div v-else-if="viewMode === 'calendar'" class="calendar-view">
        <!-- 周选择器 -->
        <div class="week-selector">
          <el-button @click="previousWeek" :icon="'ArrowLeft'" circle></el-button>
          <span class="week-range">{{ weekRange }}</span>
          <el-button @click="nextWeek" :icon="'ArrowRight'" circle></el-button>
          <el-button @click="goToCurrentWeek" size="small" style="margin-left: 20px;">本周</el-button>
        </div>
        
        <!-- 课程表 -->
        <div class="schedule-table">
          <table>
            <thead>
              <tr>
                <th class="time-header">时间段</th>
                <th v-for="day in 7" :key="day" :class="isToday(day - 1) ? 'today-header' : ''">
                  <div class="day-header">
                    <div class="weekday">{{ getWeekdayText(day - 1) }}</div>
                    <div class="date">{{ getDateText(day - 1) }}</div>
                  </div>
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="period in timePeriods" :key="period.key">
                <td class="time-label">
                  <div class="period-name">{{ period.name }}</div>
                  <div class="period-time">{{ period.time }}</div>
                </td>
                <td 
                  v-for="day in 7" 
                  :key="day" 
                  :class="[
                    'schedule-cell',
                    isToday(day - 1) ? 'today-cell' : '',
                    getScheduleForCell(day - 1, period.key) ? 'has-schedule' : 'no-schedule'
                  ]">
                  <div v-if="getScheduleForCell(day - 1, period.key)" class="schedule-content">
                    <div class="schedule-badge" :style="{ backgroundColor: period.color }">
                      {{ period.badge }}
                    </div>
                    <div class="schedule-time">
                      {{ getScheduleForCell(day - 1, period.key).startTime }} - 
                      {{ getScheduleForCell(day - 1, period.key).endTime }}
                    </div>
                    <div class="schedule-dept" v-if="getScheduleForCell(day - 1, period.key).department">
                      {{ getScheduleForCell(day - 1, period.key).department }}
                    </div>
                  </div>
                  <div v-else class="no-schedule-content">
                    休息
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      
      <!-- 列表视图 -->
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
      doctorId: null,
      
      // 视图模式
      viewMode: 'calendar', // 'calendar' 或 'list'
      
      // 当前周的起始日期
      currentWeekStart: null,
      
      // 时间段定义
      timePeriods: [
        { key: 'MORNING', name: '上午', time: '08:00-12:00', badge: '早', color: '#409EFF' },
        { key: 'AFTERNOON', name: '下午', time: '14:00-18:00', badge: '午', color: '#67C23A' },
        { key: 'EVENING', name: '晚上', time: '18:00-22:00', badge: '晚', color: '#E6A23C' },
        { key: 'NIGHT', name: '夜班', time: '22:00-08:00', badge: '夜', color: '#F56C6C' }
      ]
    };
  },
  computed: {
    // 当前周的日期范围文本
    weekRange() {
      if (!this.currentWeekStart) return '';
      const start = new Date(this.currentWeekStart);
      const end = new Date(start);
      end.setDate(end.getDate() + 6);
      
      return `${this.formatDateShort(start)} - ${this.formatDateShort(end)}`;
    }
  },
  mounted() {
    // 从localStorage获取当前医生ID
    this.doctorId = localStorage.getItem('userId') || 1;
    // 初始化为本周
    this.initCurrentWeek();
    this.fetchScheduleData();
  },
  methods: {
    // 初始化当前周
    initCurrentWeek() {
      const today = new Date();
      const dayOfWeek = today.getDay(); // 0 (周日) 到 6 (周六)
      const monday = new Date(today);
      monday.setDate(today.getDate() - dayOfWeek); // 设置为本周周日
      monday.setHours(0, 0, 0, 0);
      this.currentWeekStart = monday;
    },
    
    // 上一周
    previousWeek() {
      const newStart = new Date(this.currentWeekStart);
      newStart.setDate(newStart.getDate() - 7);
      this.currentWeekStart = newStart;
    },
    
    // 下一周
    nextWeek() {
      const newStart = new Date(this.currentWeekStart);
      newStart.setDate(newStart.getDate() + 7);
      this.currentWeekStart = newStart;
    },
    
    // 回到本周
    goToCurrentWeek() {
      this.initCurrentWeek();
    },
    
    // 判断是否是今天
    isToday(dayOffset) {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const checkDate = new Date(this.currentWeekStart);
      checkDate.setDate(checkDate.getDate() + dayOffset);
      return checkDate.getTime() === today.getTime();
    },
    
    // 获取星期文本
    getWeekdayText(dayOffset) {
      const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
      const date = new Date(this.currentWeekStart);
      date.setDate(date.getDate() + dayOffset);
      return weekdays[date.getDay()];
    },
    
    // 获取日期文本
    getDateText(dayOffset) {
      const date = new Date(this.currentWeekStart);
      date.setDate(date.getDate() + dayOffset);
      return `${date.getMonth() + 1}/${date.getDate()}`;
    },
    
    // 获取指定单元格的排班数据
    getScheduleForCell(dayOffset, periodKey) {
      const date = new Date(this.currentWeekStart);
      date.setDate(date.getDate() + dayOffset);
      const dateStr = this.formatDateForCompare(date);
      
      return this.scheduleData.find(schedule => {
        const scheduleDate = this.formatDateForCompare(new Date(schedule.scheduleDate));
        return scheduleDate === dateStr && schedule.shiftType === periodKey;
      });
    },
    
    // 格式化日期用于比较
    formatDateForCompare(date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    
    // 格式化日期（短格式）
    formatDateShort(date) {
      const month = date.getMonth() + 1;
      const day = date.getDate();
      return `${month}月${day}日`;
    },
    async fetchScheduleData() {
      this.loading = true;
      try {
        const response = await api.get(`/api/schedules/doctor/${this.doctorId}`);
        
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  font-size: 16px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

/* 课程表视图样式 */
.calendar-view {
  padding: 20px 0;
}

.week-selector {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  gap: 10px;
}

.week-range {
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
  min-width: 200px;
  text-align: center;
}

.schedule-table {
  overflow-x: auto;
}

.schedule-table table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.schedule-table th,
.schedule-table td {
  border: 1px solid #EBEEF5;
  padding: 12px;
  text-align: center;
}

.schedule-table thead th {
  background: linear-gradient(to bottom, #f5f7fa, #e4e7ed);
  font-weight: bold;
  color: #303133;
}

.time-header {
  width: 120px;
  background: #409EFF !important;
  color: white !important;
}

.today-header {
  background: #ecf5ff !important;
  color: #409EFF !important;
}

.day-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.weekday {
  font-size: 16px;
  font-weight: bold;
}

.date {
  font-size: 12px;
  color: #909399;
}

.today-header .date {
  color: #409EFF;
}

.time-label {
  background: #f5f7fa;
  font-weight: bold;
  width: 120px;
}

.period-name {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
}

.period-time {
  font-size: 12px;
  color: #909399;
}

.schedule-cell {
  min-height: 100px;
  vertical-align: middle;
  transition: all 0.3s;
}

.schedule-cell:hover {
  background-color: #f5f7fa;
}

.today-cell {
  background-color: #ecf5ff;
}

.has-schedule {
  background-color: #f0f9ff;
}

.no-schedule {
  background-color: #fafafa;
}

.schedule-content {
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: center;
}

.schedule-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 4px;
  color: white;
  font-weight: bold;
  font-size: 14px;
}

.schedule-time {
  font-size: 13px;
  color: #606266;
  font-weight: 500;
}

.schedule-dept {
  font-size: 12px;
  color: #909399;
  padding: 2px 8px;
  background: #f4f4f5;
  border-radius: 3px;
}

.no-schedule-content {
  color: #C0C4CC;
  font-size: 14px;
  padding: 20px;
}

/* 列表视图样式 */
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