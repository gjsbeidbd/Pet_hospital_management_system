<template>
  <div class="user-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="200px" class="aside">
        <div class="logo-section">
          <div class="logo">
            <span class="logo-text">宠物医院</span>
          </div>
        </div>
        <el-menu
          :default-active="$route.path"
          class="menu"
          router
        >
          <el-menu-item index="/user/dashboard">
            <i class="el-icon-house"></i>
            <span>首页概览</span>
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
      <!-- 主内容区 -->
      <el-container class="main-container">
        <!-- 顶部用户信息栏 -->
        <el-header class="header" height="70px">
          <div class="header-left"></div>
          <div class="user-info-section">
            <el-dropdown @command="handleCommand" trigger="click">
              <div class="user-avatar-wrapper">
                <div class="user-avatar" v-if="userAvatar">
                  <img :src="userAvatar" class="user-avatar-img">
                </div>
                <div class="user-avatar" v-else>{{ username.charAt(0) }}</div>
                <span class="user-name">{{ username }}</span>
                <i class="el-icon-arrow-down"></i>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                  <el-dropdown-item command="account">账户管理</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <!-- 主内容 -->
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
      username: '用户', // 默认显示"用户"
      userAvatar: '' // 用户头像
    };
  },
  async mounted() {
    // 页面加载时获取用户名
    await this.loadUsername();
    // 监听用户资料更新事件
    window.addEventListener('userProfileUpdated', this.updateUserInfo);
  },
  beforeUnmount() {
    // 移除事件监听
    window.removeEventListener('userProfileUpdated', this.updateUserInfo);
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
          
          console.log('从 API获取的用户资料:', response.data);
          // 按照要求，应该使用username字段显示用户名称
          this.username = response.data.username || '用户';
          // 如果avatar是相对路径，需要转换为完整URL
          const avatar = response.data.avatar;
          if (avatar && avatar.startsWith('/')) {
            this.userAvatar = `http://localhost:8080${avatar}`;
          } else {
            this.userAvatar = avatar || '';
          }
                    
          // 保存到localStorage
          if (this.username) {
            localStorage.setItem('username', this.username);
          }
          if (this.userAvatar) {
            localStorage.setItem('userAvatar', this.userAvatar);
          }
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
    // 更新用户信息
    updateUserInfo() {
      this.username = localStorage.getItem('username') || '用户';
      this.userAvatar = localStorage.getItem('userAvatar') || '';
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
  overflow: hidden;
  background: #f5f7fa;
}

.aside {
  background: linear-gradient(180deg, #2d3748 0%, #1a202c 100%);
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

.logo-section {
  padding: 32px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-text {
  font-size: 24px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 3px;
}

.menu {
  flex: 1;
  border-right: none;
  background: transparent;
  padding: 24px 16px;
  overflow-y: auto;
}

.menu::-webkit-scrollbar {
  width: 4px;
}

.menu::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 2px;
}

.menu .el-menu-item {
  margin: 6px 0;
  border-radius: 10px;
  height: 48px;
  line-height: 48px;
  color: rgba(255, 255, 255, 0.75);
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: transparent;
}

.menu .el-menu-item i {
  font-size: 18px;
  margin-right: 12px;
  color: rgba(255, 255, 255, 0.6);
  transition: all 0.3s ease;
}

.menu .el-menu-item:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #ffffff;
}

.menu .el-menu-item:hover i {
  color: #ffffff;
  transform: scale(1.1);
}

.menu .el-menu-item.is-active {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.3);
}

.menu .el-menu-item.is-active i {
  color: #ffffff;
}

.main-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

.header {
  background: linear-gradient(90deg, #8b5cf6 0%, #7c3aed 100%);
  box-shadow: 0 2px 12px rgba(124, 58, 237, 0.3);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 32px;
  z-index: 10;
}

.header-left {
  flex: 1;
}

.user-info-section {
  display: flex;
  align-items: center;
}

.user-avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 14px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.15);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.user-avatar-wrapper:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.3);
}

.user-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8b5cf6;
  font-weight: 600;
  font-size: 15px;
  flex-shrink: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.user-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-name {
  color: #ffffff;
  font-size: 14px;
  font-weight: 600;
}

.user-avatar-wrapper i {
  color: rgba(255, 255, 255, 0.9);
  font-size: 12px;
  transition: transform 0.3s ease;
}

.user-avatar-wrapper:hover i {
  transform: rotate(180deg);
}

.main {
  flex: 1;
  padding: 0;
  overflow-y: auto;
  overflow-x: hidden;
  background: #ffffff;
}

.main::-webkit-scrollbar {
  width: 6px;
}

.main::-webkit-scrollbar-track {
  background: transparent;
}

.main::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 3px;
}

.main::-webkit-scrollbar-thumb:hover {
  background: #a0aec0;
}

/* Element Plus 下拉菜单样式覆盖 */
:deep(.el-dropdown-menu) {
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
  padding: 6px;
  background: #ffffff;
  margin-top: 8px;
}

:deep(.el-dropdown-menu__item) {
  border-radius: 6px;
  margin: 2px 0;
  color: #4a5568;
  transition: all 0.2s ease;
  font-weight: 500;
  padding: 8px 14px;
  font-size: 14px;
}

:deep(.el-dropdown-menu__item:hover) {
  background: #f7fafc;
  color: #3182ce;
}

:deep(.el-dropdown-menu__item.is-divided) {
  border-top: 1px solid #e2e8f0;
  margin-top: 6px;
  padding-top: 8px;
}

.el-container {
  height: 100%;
}
</style>