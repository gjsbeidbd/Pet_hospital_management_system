<template>
  <div class="dashboard">
    <el-card class="welcome-card">
      <div class="welcome-content">
        <h2>欢迎使用宠物医院信息管理系统</h2>
        <p>您已成功登录为前台人员角色</p>
      </div>
    </el-card>
    
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <i class="el-icon-user stat-icon"></i>
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
            <i class="el-icon-guide stat-icon"></i>
            <div class="stat-info">
              <div class="stat-number">{{ petCount }}</div>
              <div class="stat-label">宠物总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
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
            <i class="el-icon-money stat-icon"></i>
            <div class="stat-info">
              <div class="stat-number">{{ todayIncome }}</div>
              <div class="stat-label">今日收入(元)</div>
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
              <span>待办事项</span>
            </div>
          </template>
          <ul class="todo-list">
            <li v-for="todo in todos" :key="todo.id" class="todo-item">
              <el-checkbox v-model="todo.completed">{{ todo.content }}</el-checkbox>
            </li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'ReceptionistDashboard',
  data() {
    return {
      customerCount: 128,
      petCount: 156,
      todayAppointments: 12,
      todayIncome: 2450.00,
      todayAppointmentsList: [
        {
          time: '2023-06-15 09:00',
          customer: '张三',
          pet: '小白',
          doctor: '李医生',
          status: '已完成'
        },
        {
          time: '2023-06-15 10:30',
          customer: '李四',
          pet: '小黑',
          doctor: '王医生',
          status: '已确认'
        },
        {
          time: '2023-06-15 14:00',
          customer: '王五',
          pet: '花花',
          doctor: '张医生',
          status: '待确认'
        },
        {
          time: '2023-06-15 15:30',
          customer: '赵六',
          pet: '毛毛',
          doctor: '李医生',
          status: '已取消'
        }
      ],
      todos: [
        {
          id: 1,
          content: '联系客户确认预约',
          completed: false
        },
        {
          id: 2,
          content: '整理药品库存清单',
          completed: false
        },
        {
          id: 3,
          content: '处理收费退款申请',
          completed: true
        },
        {
          id: 4,
          content: '更新客户档案信息',
          completed: false
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
  color: #67C23A;
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

.todo-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.todo-item {
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.todo-item:last-child {
  border-bottom: none;
}
</style>