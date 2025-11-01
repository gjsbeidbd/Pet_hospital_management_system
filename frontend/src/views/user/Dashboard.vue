<template>
  <div class="dashboard">
    <el-card class="welcome-card">
      <div class="welcome-content">
        <h2>欢迎使用宠物医院信息管理系统</h2>
        <p>您已成功登录为宠物主人角色</p>
      </div>
    </el-card>
    
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <i class="el-icon-guide stat-icon"></i>
            <div class="stat-info">
              <div class="stat-number">{{ petCount }}</div>
              <div class="stat-label">我的宠物</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <i class="el-icon-date stat-icon"></i>
            <div class="stat-info">
              <div class="stat-number">{{ appointmentCount }}</div>
              <div class="stat-label">我的预约</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <i class="el-icon-document stat-icon"></i>
            <div class="stat-info">
              <div class="stat-number">{{ recordCount }}</div>
              <div class="stat-label">病例记录</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <i class="el-icon-chat-line-round stat-icon"></i>
            <div class="stat-info">
              <div class="stat-number">{{ messageCount }}</div>
              <div class="stat-label">未读消息</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="recent-card">
          <template #header>
            <div class="card-header">
              <span>最近预约</span>
            </div>
          </template>
          <el-table :data="recentAppointments" style="width: 100%">
            <el-table-column prop="date" label="预约时间" width="180"></el-table-column>
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
        <el-card class="notice-card">
          <template #header>
            <div class="card-header">
              <span>医院公告</span>
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
  name: 'UserDashboard',
  data() {
    return {
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
  color: #409EFF;
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

.recent-card,
.notice-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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