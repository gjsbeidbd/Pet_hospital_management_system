<template>
  <div class="profile-page">
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
        <!-- 头像上传 -->
        <el-form-item label="头像">
          <div class="avatar-upload-section">
            <div class="avatar-display" v-if="profileForm.avatar">
              <img :src="profileForm.avatar" class="avatar-image">
            </div>
            <div class="avatar-placeholder" v-else>
              <i class="el-icon-user"></i>
            </div>
            <div class="avatar-upload-btn" v-if="isEditing">
              <el-button size="small" @click="triggerFileInput">上传头像</el-button>
              <input 
                ref="avatarInput" 
                type="file" 
                accept="image/*" 
                style="display: none;"
                @change="handleAvatarSelect">
              <div class="upload-tip">支持jpg、png格式，不超过2MB</div>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="用户名" prop="username">
          <el-input v-model="profileForm.username" :disabled="true"></el-input>
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
        address: '',
        avatar: ''
      },
      avatarFile: null, // 存储选中的头像文件
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
    // 触发文件选择
    triggerFileInput() {
      this.$refs.avatarInput.click();
    },
    
    // 处理头像选择
    handleAvatarSelect(event) {
      const file = event.target.files[0];
      if (file) {
        // 检查文件大小（2MB）
        if (file.size > 2 * 1024 * 1024) {
          this.$message.error('图片大小不能超过2MB');
          return;
        }
        
        // 检查文件类型
        if (!file.type.startsWith('image/')) {
          this.$message.error('只能上传图片文件');
          return;
        }
        
        // 保存文件
        this.avatarFile = file;
        
        // 生成预览
        const reader = new FileReader();
        reader.onload = (e) => {
          this.profileForm.avatar = e.target.result; // 用于预览显示
        };
        reader.readAsDataURL(file);
      }
    },
    
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
            address: data.address || '',
            avatar: data.avatar ? `http://localhost:8080${data.avatar}` : ''
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
            console.log('正在保存的用户资料:', this.profileForm);
            if (userId) {
              // 先上传头像（如果有新选择的头像）
              if (this.avatarFile) {
                const formData = new FormData();
                formData.append('file', this.avatarFile);
                
                const uploadResponse = await api.post(`/api/users/upload-avatar/${userId}`, formData, {
                  headers: {
                    'Content-Type': 'multipart/form-data'
                  }
                });
                
                if (uploadResponse.data.success) {
                  this.profileForm.avatar = `http://localhost:8080${uploadResponse.data.data}`;
                  this.avatarFile = null; // 清空文件
                  this.$message.success('头像上传成功');
                }
              }
              
              // 保存其他用户资料（不包含avatar，因为头像已经在上传时更新）
              const saveData = {
                username: this.profileForm.username,
                realName: this.profileForm.realName,
                gender: this.profileForm.gender,
                birthday: this.profileForm.birthday,
                phone: this.profileForm.phone,
                email: this.profileForm.email,
                address: this.profileForm.address
              };
              
              await api.put(`/api/users/profile/${userId}`, saveData);
              this.$message.success('资料保存成功');
              this.isEditing = false;
              this.originalProfile = { ...this.profileForm };
              
              // 更新localStorage中的用户名和头像
              localStorage.setItem('username', this.profileForm.username);
              if (this.profileForm.avatar) {
                localStorage.setItem('userAvatar', this.profileForm.avatar);
              }
              
              // 触发事件通知Layout更新
              window.dispatchEvent(new Event('userProfileUpdated'));
            }
          } catch (error) {
            console.error('保存用户资料失败:', error);
            this.$message.error('保存用户资料失败: ' + (error.response?.data?.message || error.message));
          }
        }
      });
    }
  }
};
</script>

<style scoped>
.profile-page {
  width: 100%;
  height: 100%;
  padding: 20px;
}

.profile-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 头像上传区域 */
.avatar-upload-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-display,
.avatar-placeholder {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #e2e8f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f7fafc;
  color: #a0aec0;
}

.avatar-placeholder i {
  font-size: 48px;
}

.avatar-upload-btn {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.upload-tip {
  font-size: 12px;
  color: #a0aec0;
}
</style>