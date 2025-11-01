<template>
  <div class="login-container">
    <div class="login-left">
      <div class="welcome-text">
        <h1>宠物医院信息管理系统</h1>
        <p>为您的宠物提供专业、便捷的医疗服务</p>
        <div class="features">
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="feature-item">
                <i class="el-icon-guide feature-icon"></i>
                <span>宠物档案管理</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="feature-item">
                <i class="el-icon-date feature-icon"></i>
                <span>在线预约挂号</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="feature-item">
                <i class="el-icon-document feature-icon"></i>
                <span>电子病历记录</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="feature-item">
                <i class="el-icon-money feature-icon"></i>
                <span>费用明细查询</span>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
    <div class="login-right">
      <div class="login-form">
        <h2>{{ activeTab === 'login' ? '用户登录' : '用户注册' }}</h2>
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="登录" name="login">
            <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" class="form">
              <el-form-item label="账号" prop="username">
                <el-input v-model="loginForm.username" placeholder="请输入手机号或邮箱" size="large" />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password size="large" />
              </el-form-item>
              <el-form-item label="角色" prop="role">
                <el-select v-model="loginForm.role" placeholder="请选择角色" style="width: 100%" size="large">
                  <el-option label="宠物主人" value="USER" />
                  <el-option label="前台人员" value="RECEPTIONIST" />
                  <el-option label="医生" value="DOCTOR" />
                  <el-option label="院长" value="ADMIN" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleLogin" :loading="loginLoading" style="width: 100%" size="large">
                  登录
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="注册" name="register">
            <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" class="form">
              <el-form-item label="账号" prop="username">
                <el-input v-model="registerForm.username" placeholder="请输入手机号或邮箱" size="large" />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password size="large" />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码" show-password size="large" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleRegister" :loading="registerLoading" style="width: 100%" size="large">
                  注册
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/services/api';

export default {
  name: 'Login',
  data() {
    // 确认密码验证规则
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };
    
    // 手机号/邮箱验证规则
    const validateUsername = (rule, value, callback) => {
      const phoneRegex = /^1[3-9]\d{9}$/;
      const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
      
      if (phoneRegex.test(value) || emailRegex.test(value)) {
        callback();
      } else {
        callback(new Error('请输入有效的手机号或邮箱'));
      }
    };

    return {
      activeTab: 'login',
      loginLoading: false,
      registerLoading: false,
      loginForm: {
        username: '',
        password: '',
        role: 'USER'
      },
      registerForm: {
        username: '',
        password: '',
        confirmPassword: ''
        // 移除 role 字段，注册时默认为 USER 角色
      },
      loginRules: {
        username: [
          { required: true, message: '请输入手机号或邮箱', trigger: 'blur' },
          { validator: validateUsername, trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ],
        role: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ]
      },
      registerRules: {
        username: [
          { required: true, message: '请输入手机号或邮箱', trigger: 'blur' },
          { validator: validateUsername, trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
        // 移除 role 规则
      }
    };
  },
  methods: {
    handleTabChange(tab) {
      this.activeTab = tab;
    },
    async handleLogin() {
      this.$refs.loginFormRef.validate(async (valid) => {
        if (valid) {
          this.loginLoading = true;
          try {
            const response = await api.post('/api/auth/login', this.loginForm);
            const { token, role, userId } = response.data;
            
            // 存储token和用户信息
            localStorage.setItem('token', token);
            localStorage.setItem('userRole', role);
            localStorage.setItem('userId', userId);
            
            // 根据角色跳转到不同页面
            this.redirectByRole(role);
            
            this.$message.success('登录成功');
          } catch (error) {
            this.$message.error('登录失败: ' + (error.response?.data || '未知错误'));
          } finally {
            this.loginLoading = false;
          }
        }
      });
    },
    async handleRegister() {
      this.$refs.registerFormRef.validate(async (valid) => {
        if (valid) {
          this.registerLoading = true;
          try {
            const response = await api.post('/api/auth/register', {
              username: this.registerForm.username,
              password: this.registerForm.password,
              role: 'USER' // 默认为宠物主人角色
            });
            
            this.$message.success('注册成功，请登录');
            this.activeTab = 'login';
          } catch (error) {
            this.$message.error('注册失败: ' + (error.response?.data || '未知错误'));
          } finally {
            this.registerLoading = false;
          }
        }
      });
    },
    redirectByRole(role) {
      switch (role) {
        case 'USER':
          this.$router.push('/user/dashboard');
          break;
        case 'RECEPTIONIST':
          this.$router.push('/receptionist/dashboard');
          break;
        case 'DOCTOR':
          this.$router.push('/doctor/dashboard');
          break;
        case 'ADMIN':
          this.$router.push('/director/dashboard');
          break;
        default:
          this.$router.push('/');
      }
    }
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  height: 100vh;
  background: linear-gradient(120deg, #e0c3fc, #8ec5fc);
}

.login-left {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: white;
  background: rgba(0, 0, 0, 0.3);
}

.welcome-text h1 {
  font-size: 36px;
  margin-bottom: 20px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.welcome-text p {
  font-size: 18px;
  margin-bottom: 40px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.features {
  width: 100%;
}

.feature-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  font-size: 16px;
}

.feature-icon {
  font-size: 24px;
  margin-right: 10px;
  color: #ffd700;
}

.login-right {
  width: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  padding: 40px;
  box-shadow: -2px 0 10px rgba(0, 0, 0, 0.1);
}

.login-form {
  width: 100%;
}

.login-form h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 28px;
}

.form {
  padding: 20px 0;
}

.form :deep(.el-form-item__label) {
  font-weight: bold;
  color: #555;
}

@media (max-width: 992px) {
  .login-left {
    display: none;
  }
  
  .login-right {
    width: 100%;
  }
}
</style>