<template>
  <div class="account-management">
    <el-card class="account-card">
      <template #header>
        <div class="card-header">
          <span>账户管理</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <!-- 修改密码标签页 -->
        <el-tab-pane label="修改密码" name="changePassword">
          <el-form 
            :model="passwordForm" 
            :rules="passwordRules" 
            ref="passwordFormRef" 
            label-width="100px"
            class="password-form"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input 
                v-model="passwordForm.oldPassword" 
                type="password" 
                show-password
                placeholder="请输入原密码"
              />
            </el-form-item>
            
            <el-form-item label="新密码" prop="newPassword">
              <el-input 
                v-model="passwordForm.newPassword" 
                type="password" 
                show-password
                placeholder="请输入新密码"
              />
            </el-form-item>
            
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input 
                v-model="passwordForm.confirmPassword" 
                type="password" 
                show-password
                placeholder="请再次输入新密码"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="changePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <!-- 注销账户标签页 -->
        <el-tab-pane label="注销账户" name="deleteAccount">
          <div class="delete-account-section">
            <h3>注销账户</h3>
            <p class="warning-text">
              警告：注销账户将永久删除您的所有信息，包括个人资料、预约记录、宠物信息等，此操作不可恢复。
            </p>
            
            <el-form 
              :model="deleteAccountForm" 
              :rules="deleteAccountRules" 
              ref="deleteAccountFormRef" 
              label-width="100px"
              class="delete-account-form"
            >
              <el-form-item label="登录密码" prop="password">
                <el-input 
                  v-model="deleteAccountForm.password" 
                  type="password" 
                  show-password
                  placeholder="请输入登录密码以确认注销"
                />
              </el-form-item>
              
              <el-form-item>
                <el-button type="danger" @click="deleteAccount">注销账户</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import api from '@/services/api';

export default {
  name: 'AccountManagement',
  data() {
    // 确认密码验证规则
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入新密码'));
      } else if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };

    return {
      activeTab: 'changePassword',
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      },
      deleteAccountForm: {
        password: ''
      },
      deleteAccountRules: {
        password: [
          { required: true, message: '请输入登录密码', trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    // 修改密码
    changePassword() {
      this.$refs.passwordFormRef.validate(async (valid) => {
        if (valid) {
          try {
            const userId = localStorage.getItem('userId');
            if (userId) {
              const requestData = {
                userId: userId,
                oldPassword: this.passwordForm.oldPassword,
                newPassword: this.passwordForm.newPassword
              };
              
              // 调用后端修改密码接口（需要后端实现）
              await api.post(`/api/users/change-password`, requestData);
              
              this.$message.success('密码修改成功');
              
              // 清空表单
              this.passwordForm = {
                oldPassword: '',
                newPassword: '',
                confirmPassword: ''
              };
            }
          } catch (error) {
            console.error('修改密码失败:', error);
            this.$message.error('修改密码失败: ' + (error.response?.data || error.message));
          }
        }
      });
    },
    
    // 注销账户
    deleteAccount() {
      this.$refs.deleteAccountFormRef.validate(async (valid) => {
        if (valid) {
          this.$confirm('确定要注销您的账户吗？此操作将永久删除您的所有信息且不可恢复！', '警告', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(async () => {
            try {
              const userId = localStorage.getItem('userId');
              if (userId) {
                // 调用后端注销账户接口，传递用户ID和密码
                const requestData = {
                  userId: userId,
                  password: this.deleteAccountForm.password
                };
                
                await api.post(`/api/users/delete-account`, requestData);
                
                this.$message.success('账户注销成功');
                
                // 清除本地存储的用户信息
                localStorage.removeItem('token');
                localStorage.removeItem('userRole');
                localStorage.removeItem('userId');
                
                // 跳转到登录页
                this.$router.push('/login');
              }
            } catch (error) {
              console.error('注销账户失败:', error);
              this.$message.error('注销账户失败: ' + (error.response?.data || error.message));
            }
          }).catch(() => {
            // 用户取消操作
          });
        }
      });
    }
  }
};
</script>

<style scoped>
.account-management {
  padding: 20px;
}

.account-card {
  max-width: 600px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.password-form {
  max-width: 400px;
  margin-top: 20px;
}

.delete-account-form {
  max-width: 400px;
  margin-top: 20px;
}

.delete-account-section h3 {
  margin-bottom: 15px;
}

.warning-text {
  color: #F56C6C;
  font-size: 14px;
  margin-bottom: 20px;
  line-height: 1.5;
}
</style>