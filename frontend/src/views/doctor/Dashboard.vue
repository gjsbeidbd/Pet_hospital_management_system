<template>
  <div class="dashboard">
    <el-card class="welcome-card">
      <div class="welcome-content">
        <h2>欢迎使用宠物医院信息管理系统</h2>
        <p>您已成功登录为医生角色</p>
      </div>
    </el-card>
    
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <i class="el-icon-date stat-icon"></i>
            <div class="stat-info">
              <div class="stat-number">{{ todayAppointments }}</div>
              <div class="stat-label">今日预约</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <i class="el-icon-document-checked stat-icon"></i>
            <div class="stat-info">
              <div class="stat-number">{{ todayCompleted }}</div>
              <div class="stat-label">今日接诊</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <i class="el-icon-document stat-icon"></i>
            <div class="stat-info">
              <div class="stat-number">{{ totalRecords }}</div>
              <div class="stat-label">累计病例</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <i class="el-icon-star-off stat-icon"></i>
            <div class="stat-info">
              <div class="stat-number">{{ satisfaction }}</div>
              <div class="stat-label">患者满意度</div>
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
              <span>今日预约列表</span>
            </div>
          </template>
          <el-table :data="todayAppointmentsList" style="width: 100%">
            <el-table-column prop="time" label="预约时间" width="180"></el-table-column>
            <el-table-column prop="customer" label="客户" width="120"></el-table-column>
            <el-table-column prop="pet" label="宠物" width="120"></el-table-column>
            <el-table-column prop="symptoms" label="症状"></el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="schedule-card">
          <template #header>
            <div class="card-header">
              <span>今日排班</span>
            </div>
          </template>
          <div class="schedule-info">
            <p><i class="el-icon-time"></i> 上午班次: 08:00-12:00</p>
            <p><i class="el-icon-time"></i> 下午班次: 14:00-18:00</p>
            <p><i class="el-icon-location"></i> 诊室: 二楼201室</p>
          </div>
        </el-card>
        
        <el-card class="notice-card">
          <template #header>
            <div class="card-header">
              <span>重要提醒</span>
            </div>
          </template>
          <ul class="notice-list">
            <li v-for="notice in notices" :key="notice.id" class="notice-item">
              <div class="notice-title">{{ notice.title }}</div>
              <div class="notice-content">{{ notice.content }}</div>
            </li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'DoctorDashboard',
  data() {
    return {
      todayAppointments: 8,
      todayCompleted: 5,
      totalRecords: 126,
      satisfaction: '96%',
      todayAppointmentsList: [
        {
          time: '2023-06-15 09:00',
          customer: '张三',
          pet: '小白',
          symptoms: '食欲不振，精神萎靡',
          status: '已完成'
        },
        {
          time: '2023-06-15 10:30',
          customer: '李四',
          pet: '小黑',
          symptoms: '呕吐，腹泻',
          status: '已接诊'
        },
        {
          time: '2023-06-15 14:00',
          customer: '王五',
          pet: '花花',
          symptoms: '定期体检',
          status: '待接诊'
        },
        {
          time: '2023-06-15 15:30',
          customer: '赵六',
          pet: '毛毛',
          symptoms: '皮肤瘙痒，脱毛',
          status: '待接诊'
        }
      ],
      notices: [
        {
          id: 1,
          title: '药品库存提醒',
          content: '皮肤病用药库存不足，请及时补充'
        },
        {
          id: 2,
          title: '会议通知',
          content: '本周五下午3点召开医生例会'
        }
      ]
    };
  },
  methods: {
    getStatusType(status) {
      const statusMap = {
        '已完成': 'success',
        '已接诊': 'warning',
        '待接诊': 'info'
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
  color: #E6A23C;
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
.schedule-card,
.notice-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.schedule-info p {
  margin: 10px 0;
  color: #666;
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
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.notice-content {
  font-size: 14px;
  color: #666;
}
</style>