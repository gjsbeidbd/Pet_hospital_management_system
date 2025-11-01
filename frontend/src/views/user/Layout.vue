<template>
  <div class="user-layout">
    <el-container>
      <el-header class="header">
        <div class="logo">宠物医院管理系统 - 用户端</div>
        <div class="user-info">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ username }}<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="account">账户管理</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-container>
        <el-aside width="200px" class="aside">
          <el-menu
            :default-active="$route.path"
            class="menu"
            router
          >
            <el-menu-item index="/user/dashboard">
              <i class="el-icon-house"></i>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="/user/appointments">
              <i class="el-icon-date"></i>
              <span>我的预约</span>
            </el-menu-item>
            <el-menu-item index="/user/pets">
              <i class="el-icon-guide"></i>
              <span>我的宠物</span>
            </el-menu-item>
            <el-menu-item index="/user/medical-records">
              <i class="el-icon-document"></i>
              <span>病例记录</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main class="main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import api from '@/services/api';

export default {
  name: 'UserLayout',
  data() {
    return {
      username: '用户' // 默认显示"用户"
    };
  },
  async mounted() {
    // 页面加载时获取用户名
    await this.loadUsername();
  },
  methods: {
    async loadUsername() {
      try {
        const userId = localStorage.getItem('userId');
        const userRole = localStorage.getItem('userRole');
        console.log('从localStorage获取的userId:', userId, 'userRole:', userRole);
        
        if (userId) {
          // 根据用户角色调用不同的API端点
          let response;
          if (userRole === 'USER') {
            response = await api.get(`/api/users/profile/${userId}`);
          } else if (userRole === 'RECEPTIONIST') {
            response = await api.get(`/api/receptionist/profile/${userId}`);
          } else if (userRole === 'DOCTOR') {
            response = await api.get(`/api/doctor/profile/${userId}`);
          } else if (userRole === 'ADMIN') {
            response = await api.get(`/api/director/profile/${userId}`);
          } else {
            // 默认使用用户API
            response = await api.get(`/api/user/profile/${userId}`);
          }
          
          console.log('从API获取的用户资料:', response.data);
          // 按照要求，应该使用username字段显示用户名称
          this.username = response.data.username || '用户';
        } else {
          console.error('未找到用户ID');
          this.username = '用户';
        }
      } catch (error) {
        console.error('获取用户名失败:', error);
        console.error('错误详情:', {
          message: error.message,
          response: error.response?.data,
          status: error.response?.status
        });
        // 即使获取失败也保持默认值
        this.username = '用户';
      }
    },
    handleCommand(command) {
      if (command === 'profile') {
        // 跳转到个人资料页面
        this.$router.push('/user/profile');
      } else if (command === 'account') {
        // 跳转到账户管理页面
        this.$router.push('/user/account');
      } else if (command === 'logout') {
        // 清除本地存储的用户信息
        localStorage.removeItem('token');
        localStorage.removeItem('userRole');
        localStorage.removeItem('userId');
        // 跳转到登录页
        this.$router.push('/login');
      }
    }
  }
};
</script>

<style scoped>
.user-layout {
  height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #409EFF;
  color: white;
  padding: 0 20px;
}

.logo {
  font-size: 18px;
  font-weight: bold;
}

.user-info {
  cursor: pointer;
}

.aside {
  background-color: #f5f5f5;
}

.menu {
  height: 100%;
  border-right: none;
}

.main {
  background-color: #fff;
  padding: 20px;
}
</style>