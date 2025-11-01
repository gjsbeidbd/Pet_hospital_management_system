<template>
  <div class="dashboard">
    <el-card class="welcome-card">
      <div class="welcome-content">
        <h2>欢迎使用宠物医院信息管理系统</h2>
        <p>您已成功登录为院长角色</p>
      </div>
    </el-card>
    
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <i class="el-icon-user stat-icon"></i>
            <div class="stat-info">
              <div class="stat-number">{{ userCount }}</div>
              <div class="stat-label">员工总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <i class="el-icon-guide stat-icon"></i>
            <div class="stat-info">
              <div class="stat-number">{{ customerCount }}</div>
              <div class="stat-label">客户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <i class="el-icon-money stat-icon"></i>
            <div class="stat-info">
              <div class="stat-number">{{ todayIncome }}</div>
              <div class="stat-label">今日收入(元)</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <i class="el-icon-data-analysis stat-icon"></i>
            <div class="stat-info">
              <div class="stat-number">{{ monthlyGrowth }}</div>
              <div class="stat-label">月增长率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>收入统计</span>
            </div>
          </template>
          <div class="chart-container">
            <div class="chart-placeholder">
              <p>收入统计图表区域</p>
              <p>（实际项目中将展示收入趋势图表）</p>
            </div>
          </div>
        </el-card>
        
        <el-card class="recent-card">
          <template #header>
            <div class="card-header">
              <span>近期预约</span>
            </div>
          </template>
          <el-table :data="recentAppointments" style="width: 100%">
            <el-table-column prop="date" label="预约时间" width="180"></el-table-column>
            <el-table-column prop="customer" label="客户" width="120"></el-table-column>
            <el-table-column prop="pet" label="宠物" width="120"></el-table-column>
            <el-table-column prop="doctor" label="医生" width="120"></el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="summary-card">
          <template #header>
            <div class="card-header">
              <span>运营概况</span>
            </div>
          </template>
          <div class="summary-item">
            <span>今日预约数:</span>
            <span class="summary-value">{{ todayAppointments }}</span>
          </div>
          <div class="summary-item">
            <span>今日接诊数:</span>
            <span class="summary-value">{{ todayTreatments }}</span>
          </div>
          <div class="summary-item">
            <span>药品库存警告:</span>
            <span class="summary-value warning">{{ lowStockItems }}</span>
          </div>
          <div class="summary-item">
            <span>待审核病例:</span>
            <span class="summary-value">{{ pendingRecords }}</span>
          </div>
        </el-card>
        
        <el-card class="notice-card">
          <template #header>
            <div class="card-header">
              <span>院长通知</span>
            </div>
          </template>
          <ul class="notice-list">
            <li v-for="notice in notices" :key="notice.id" class="notice-item">
              <div class="notice-title">{{ notice.title }}</div>
              <div class="notice-date">{{ notice.date }}</div>
            </li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'DirectorDashboard',
  data() {
    return {
      userCount: 12,
      customerCount: 256,
      todayIncome: 3250.00,
      monthlyGrowth: '12.5%',
      todayAppointments: 18,
      todayTreatments: 15,
      lowStockItems: 3,
      pendingRecords: 2,
      recentAppointments: [
        {
          date: '2023-06-15 09:00',
          customer: '张三',
          pet: '小白',
          doctor: '李医生',
          status: '已完成'
        },
        {
          date: '2023-06-15 10:30',
          customer: '李四',
          pet: '小黑',
          doctor: '王医生',
          status: '已接诊'
        },
        {
          date: '2023-06-15 14:00',
          customer: '王五',
          pet: '花花',
          doctor: '张医生',
          status: '待接诊'
        },
        {
          date: '2023-06-15 15:30',
          customer: '赵六',
          pet: '毛毛',
          doctor: '李医生',
          status: '已取消'
        }
      ],
      notices: [
        {
          id: 1,
          title: '月度经营分析会议',
          date: '2023-06-20'
        },
        {
          id: 2,
          title: '新员工入职培训',
          date: '2023-06-18'
        },
        {
          id: 3,
          title: '设备维护通知',
          date: '2023-06-17'
        }
      ]
    };
  },
  methods: {
    getStatusType(status) {
      const statusMap = {
        '已完成': 'success',
        '已接诊': 'warning',
        '待接诊': 'info',
        '已取消': 'danger'
      };
      return statusMap[status] || 'info';
    }
  }
};
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 20px;
}

.welcome-content {
  text-align: center;
  padding: 20px 0;
}

.welcome-content h2 {
  margin-bottom: 10px;
  color: #333;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
}

.stat-item {
  display: flex;
  align-items: center;
  height: 100%;
}

.stat-icon {
  font-size: 40px;
  color: #F56C6C;
  margin: 0 20px;
}

.stat-info {
  text-align: center;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.chart-card,
.recent-card,
.summary-card,
.notice-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-placeholder {
  text-align: center;
  color: #999;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.summary-item:last-child {
  border-bottom: none;
}

.summary-value {
  font-weight: bold;
  color: #333;
}

.summary-value.warning {
  color: #F56C6C;
}

.notice-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.notice-item {
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}

.notice-date {
  font-size: 12px;
  color: #999;
}
</style>