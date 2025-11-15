<template>
  <div class="doctor-layout">
    <el-container>
      <el-header class="header">
        <div class="logo">宠物医院管理系统 - 医生端</div>
        <div class="user-info">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              医生<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
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
            <el-menu-item index="/doctor/dashboard">
              <i class="el-icon-house"></i>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="/doctor/schedule">
              <i class="el-icon-timer"></i>
              <span>我的排班</span>
            </el-menu-item>
            <el-menu-item index="/doctor/appointments">
              <i class="el-icon-user"></i>
              <span>就诊管理</span>
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
export default {
  name: 'DoctorLayout',
  methods: {
    handleCommand(command) {
      if (command === 'logout') {
        // 清除本地存储的用户信息
        localStorage.removeItem('token');
        localStorage.removeItem('userRole');
        // 跳转到登录页
        this.$router.push('/login');
      }
    }
  }
};
</script>

<style scoped>
.doctor-layout {
  height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #E6A23C;
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