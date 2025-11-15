<template>
  <div class="receptionist-layout">
    <el-container>
      <el-header class="header">
        <div class="logo">宠物医院管理系统 - 前台端</div>
        <div class="user-info">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              前台人员<i class="el-icon-arrow-down el-icon--right"></i>
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
            <el-menu-item index="/receptionist/dashboard">
              <i class="el-icon-house"></i>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="/receptionist/queue">
              <i class="el-icon-tickets"></i>
              <span>取号排队</span>
            </el-menu-item>
            <el-menu-item index="/receptionist/customers">
              <i class="el-icon-user"></i>
              <span>客户管理</span>
            </el-menu-item>
            <el-menu-item index="/receptionist/pets">
              <i class="el-icon-guide"></i>
              <span>宠物管理</span>
            </el-menu-item>
            <el-menu-item index="/receptionist/appointments">
              <i class="el-icon-date"></i>
              <span>预约管理</span>
            </el-menu-item>
            <el-menu-item index="/receptionist/billing">
              <i class="el-icon-money"></i>
              <span>收费管理</span>
            </el-menu-item>
            <el-menu-item index="/receptionist/inventory">
              <i class="el-icon-box"></i>
              <span>药品库存</span>
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
  name: 'ReceptionistLayout',
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
.receptionist-layout {
  height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #67C23A;
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