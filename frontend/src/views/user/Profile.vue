<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人资料</span>
          <el-button type="primary" @click="editProfile" v-if="!isEditing">编辑资料</el-button>
          <div v-else>
            <el-button type="success" @click="saveProfile">保存</el-button>
            <el-button @click="cancelEdit">取消</el-button>
          </div>
        </div>
      </template>
      
      <el-form :model="profileForm" :rules="profileRules" ref="profileFormRef" label-width="100px" :disabled="!isEditing">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="profileForm.username" :disabled="!isEditing"></el-input>
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="profileForm.realName"></el-input>
        </el-form-item>
        
        <el-form-item label="性别">
          <el-select v-model="profileForm.gender" placeholder="请选择性别">
            <el-option label="男" value="MALE"></el-option>
            <el-option label="女" value="FEMALE"></el-option>
            <el-option label="其他" value="OTHER"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="出生日期">
          <el-date-picker
            v-model="profileForm.birthday"
            type="date"
            placeholder="选择出生日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD">
          </el-date-picker>
        </el-form-item>
        
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="profileForm.phone"></el-input>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="profileForm.email"></el-input>
        </el-form-item>
        
        <el-form-item label="地址">
          <el-input v-model="profileForm.address" type="textarea"></el-input>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import api from '@/services/api';

export default {
  name: 'UserProfile',
  data() {
    return {
      isEditing: false,
      originalProfile: {},
      profileForm: {
        username: '',
        realName: '',
        gender: '',
        birthday: '',
        phone: '',
        email: '',
        address: ''
      },
      profileRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        email: [
          { pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      }
    };
  },
  mounted() {
    this.loadUserProfile();
  },
  methods: {
    async loadUserProfile() {
      try {
        const userId = localStorage.getItem('userId');
        console.log('从localStorage获取的userId:', userId);
        
        if (userId) {
          const response = await api.get(`/api/users/profile/${userId}`);
          console.log('从API获取的用户资料:', response.data);
          
          const data = response.data;
          this.profileForm = {
            username: data.username,
            realName: data.realName || '',
            gender: data.gender || '',
            birthday: data.birthday || '',
            phone: data.phone || '',
            email: data.email || '',
            address: data.address || ''
          };
        }
        this.originalProfile = { ...this.profileForm };
      } catch (error) {
        console.error('加载用户资料失败:', error);
        console.error('错误详情:', {
          message: error.message,
          response: error.response?.data,
          status: error.response?.status
        });
        this.$message.error('加载用户资料失败: ' + (error.response?.data || error.message));
      }
    },
    
    editProfile() {
      this.isEditing = true;
    },
    
    cancelEdit() {
      this.isEditing = false;
      this.profileForm = { ...this.originalProfile };
    },
    
    async saveProfile() {
      this.$refs.profileFormRef.validate(async (valid) => {
        if (valid) {
          try {
            const userId = localStorage.getItem('userId');
            console.log('正在保存的用户资料:', this.profileForm); // 添加调试日志
            if (userId) {
              await api.put(`/api/users/profile/${userId}`, this.profileForm);
              this.$message.success('资料保存成功');
              this.isEditing = false;
              this.originalProfile = { ...this.profileForm };
            }
          } catch (error) {
            console.error('保存用户资料失败:', error);
            this.$message.error('保存用户资料失败: ' + (error.response?.data || error.message));
          }
        }
      });
    }
  }
};
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

</style>