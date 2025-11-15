<template>
  <div class="dashboard">
    <!-- 欢迎卡片 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <h1>欢迎回来，{{ username }}</h1>
        <p>祝您和您的宠物健康快乐！</p>
      </div>
      <div class="welcome-decoration">
        <span class="decoration-icon">🐾</span>
      </div>
    </div>
    
    <!-- 统计卡片 -->
    <el-row :gutter="24" class="stats-row">
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <div class="stat-card stat-card-1">
          <div class="stat-icon">
            <i class="el-icon-guide"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ petCount }}</div>
            <div class="stat-label">我的宠物</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <div class="stat-card stat-card-2">
          <div class="stat-icon">
            <i class="el-icon-date"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ appointmentCount }}</div>
            <div class="stat-label">我的预约</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <div class="stat-card stat-card-3">
          <div class="stat-icon">
            <i class="el-icon-document"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ recordCount }}</div>
            <div class="stat-label">病例记录</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <div class="stat-card stat-card-4">
          <div class="stat-icon">
            <i class="el-icon-chat-line-round"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ messageCount }}</div>
            <div class="stat-label">未读消息</div>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 内容区域 -->
    <el-row :gutter="24" class="content-row">
      <el-col :xs="24" :sm="24" :md="16" :lg="16">
        <el-card class="recent-card">
          <template #header>
            <div class="card-header">
              <span><i class="el-icon-time"></i> 最近预约</span>
              <el-button type="text" size="small">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentAppointments" style="width: 100%">
            <el-table-column prop="date" label="预约时间" min-width="160"></el-table-column>
            <el-table-column prop="pet" label="宠物" width="100"></el-table-column>
            <el-table-column prop="doctor" label="医生" width="100"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)" size="small">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="8" :lg="8">
        <el-card class="notice-card">
          <template #header>
            <div class="card-header">
              <span><i class="el-icon-bell"></i> 医院公告</span>
            </div>
          </template>
          <div class="notice-list">
            <div v-for="notice in notices" :key="notice.id" class="notice-item">
              <div class="notice-dot"></div>
              <div class="notice-info">
                <div class="notice-title">{{ notice.title }}</div>
                <div class="notice-date">{{ notice.date }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'UserDashboard',
  data() {
    return {
      username: localStorage.getItem('username') || '用户',
      petCount: 2,
      appointmentCount: 3,
      recordCount: 5,
      messageCount: 1,
      recentAppointments: [
        {
          date: '2023-06-15 14:30',
          pet: '小白',
          doctor: '张医生',
          status: '已完成'
        },
        {
          date: '2023-06-20 10:00',
          pet: '小黑',
          doctor: '李医生',
          status: '已确认'
        },
        {
          date: '2023-06-25 15:00',
          pet: '小白',
          doctor: '王医生',
          status: '待确认'
        }
      ],
      notices: [
        {
          id: 1,
          title: '端午节放假通知',
          date: '2023-06-10'
        },
        {
          id: 2,
          title: '疫苗接种优惠活动',
          date: '2023-06-05'
        },
        {
          id: 3,
          title: '新增宠物寄养服务',
          date: '2023-06-01'
        }
      ]
    };
  },
  methods: {
    getStatusType(status) {
      const statusMap = {
        '已完成': 'success',
        '已确认': 'warning',
        '待确认': 'info',
        '已取消': 'danger'
      };
      return statusMap[status] || 'info';
    }
  }
};
</script>

<style scoped>
.dashboard {
  width: 100%;
  height: 100%;
  padding: 20px;
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #3182ce 0%, #2c5282 100%);
  border-radius: 12px;
  padding: 24px 28px;
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(49, 130, 206, 0.15);
  position: relative;
  overflow: hidden;
}

.welcome-banner::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -10%;
  width: 350px;
  height: 350px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 50%;
}

.welcome-content h1 {
  font-size: 22px;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 6px;
  position: relative;
  z-index: 1;
}

.welcome-content p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  position: relative;
  z-index: 1;
}

.welcome-decoration {
  position: relative;
  z-index: 1;
}

.decoration-icon {
  font-size: 56px;
  opacity: 0.25;
  filter: brightness(1.3);
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 16px;
}

.stat-card {
  background: #ffffff;
  border-radius: 10px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  height: 100px;
  position: relative;
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: currentColor;
  transform: scaleX(0);
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.stat-card:hover::before {
  transform: scaleX(1);
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: currentColor;
}

.stat-card-1 { color: #3182ce; }
.stat-card-2 { color: #38a169; }
.stat-card-3 { color: #d69e2e; }
.stat-card-4 { color: #e53e3e; }

.stat-card-1 .stat-icon { background: linear-gradient(135deg, #3182ce 0%, #2c5282 100%); }
.stat-card-2 .stat-icon { background: linear-gradient(135deg, #38a169 0%, #2f855a 100%); }
.stat-card-3 .stat-icon { background: linear-gradient(135deg, #d69e2e 0%, #b7791f 100%); }
.stat-card-4 .stat-icon { background: linear-gradient(135deg, #e53e3e 0%, #c53030 100%); }

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.stat-icon i {
  font-size: 26px;
  color: #ffffff;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #1a202c;
  line-height: 1;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 13px;
  color: #718096;
  font-weight: 600;
  letter-spacing: 0.3px;
}

/* 内容区域 */
.content-row {
  height: calc(100vh - 340px);
  min-height: 400px;
}

.content-row .el-col {
  height: 100%;
}

.recent-card,
.notice-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
}

.recent-card :deep(.el-card__header),
.notice-card :deep(.el-card__header) {
  background: #f7fafc;
  border-bottom: 1px solid #e2e8f0;
  padding: 16px 20px;
}

.recent-card :deep(.el-card__body),
.notice-card :deep(.el-card__body) {
  flex: 1;
  overflow-y: auto;
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-size: 15px;
  font-weight: 700;
  color: #2d3748;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-header span i {
  font-size: 16px;
  color: #3182ce;
}

.card-header .el-button {
  color: #3182ce;
  font-weight: 600;
  font-size: 13px;
}

/* 表格样式 */
.recent-card :deep(.el-table) {
  font-size: 14px;
}

.recent-card :deep(.el-table th) {
  background: #f7fafc;
  color: #4a5568;
  font-weight: 600;
  border-bottom: 1px solid #e2e8f0;
  font-size: 13px;
}

.recent-card :deep(.el-table td) {
  color: #4a5568;
  border-bottom: 1px solid #edf2f7;
  padding: 11px 0;
  font-size: 14px;
}

.recent-card :deep(.el-table tr:hover > td) {
  background: #f7fafc;
}

.recent-card :deep(.el-tag) {
  border-radius: 5px;
  font-weight: 600;
  padding: 3px 10px;
  border: none;
  font-size: 12px;
}

/* 公告列表 */
.notice-list {
  padding: 12px 16px;
}

.notice-item {
  display: flex;
  gap: 10px;
  padding: 12px;
  margin-bottom: 8px;
  border-radius: 8px;
  background: #f7fafc;
  transition: all 0.2s ease;
  cursor: pointer;
  border-left: 2px solid transparent;
}

.notice-item:hover {
  background: #edf2f7;
  border-left-color: #3182ce;
  transform: translateX(3px);
}

.notice-item:last-child {
  margin-bottom: 0;
}

.notice-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #3182ce;
  margin-top: 6px;
  flex-shrink: 0;
}

.notice-info {
  flex: 1;
}

.notice-title {
  font-size: 14px;
  color: #2d3748;
  margin-bottom: 4px;
  font-weight: 600;
  line-height: 1.4;
}

.notice-date {
  font-size: 12px;
  color: #a0aec0;
  font-weight: 500;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .welcome-banner {
    padding: 20px;
  }
  
  .welcome-content h1 {
    font-size: 18px;
  }
  
  .decoration-icon {
    font-size: 42px;
  }
  
  .stat-card {
    height: auto;
    padding: 16px;
  }
  
  .stat-number {
    font-size: 22px;
  }
  
  .content-row {
    height: auto;
  }
  
  .content-row .el-col {
    height: auto;
    margin-bottom: 16px;
  }
}
</style>