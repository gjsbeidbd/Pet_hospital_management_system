<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <div class="header-actions">
            <el-radio-group v-model="userType" @change="loadUsers" size="small" style="margin-right: 10px;">
              <el-radio-button label="all">全部用户</el-radio-button>
              <el-radio-button label="USER">普通用户</el-radio-button>
              <el-radio-button label="RECEPTIONIST">前台</el-radio-button>
              <el-radio-button label="DOCTOR">医生</el-radio-button>
            </el-radio-group>
            <el-button type="primary" @click="showAddDialog" icon="el-icon-plus">添加用户</el-button>
          </div>
        </div>
      </template>

      <!-- 用户列表 -->
      <el-table :data="users" style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60"></el-table-column>
        <el-table-column prop="username" label="用户名" width="150"></el-table-column>
        <el-table-column prop="realName" label="真实姓名" width="120"></el-table-column>
        <el-table-column prop="userType" label="用户类型" width="100">
          <template #default="scope">
            <el-tag :type="getUserTypeColor(scope.row.userType)">
              {{ getUserTypeText(scope.row.userType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
        <el-table-column prop="employeeId" label="工号" width="100">
          <template #default="scope">
            {{ scope.row.employeeId || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="department" label="科室/部门" width="120">
          <template #default="scope">
            {{ scope.row.department || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewDetail(scope.row)">查看</el-button>
            <el-button size="small" type="primary" @click="editUser(scope.row)">编辑</el-button>
            <el-button 
              v-if="scope.row.userType !== 'USER'"
              size="small" 
              :type="scope.row.status === 1 ? 'warning' : 'success'" 
              @click="toggleStatus(scope.row)">
              {{ scope.row.status === 1 ? '停用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="users.length === 0 && !loading" description="暂无用户数据"></el-empty>
    </el-card>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog 
      :title="dialogMode === 'add' ? '添加用户' : '编辑用户'" 
      v-model="dialogVisible" 
      width="700px"
      @close="resetForm">
      <el-form :model="userForm" :rules="formRules" ref="userFormRef" label-width="120px">
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="userForm.userType" placeholder="请选择用户类型" :disabled="dialogMode === 'edit'" style="width: 100%;">
            <el-option label="普通用户" value="USER"></el-option>
            <el-option label="前台" value="RECEPTIONIST"></el-option>
            <el-option label="医生" value="DOCTOR"></el-option>
          </el-select>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="userForm.username" placeholder="请输入用户名" :disabled="dialogMode === 'edit'"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" :prop="dialogMode === 'add' ? 'password' : ''">
              <el-input 
                v-model="userForm.password" 
                type="password" 
                :placeholder="dialogMode === 'add' ? '请输入密码' : '留空则不修改密码'"
                show-password></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="userForm.realName" placeholder="请输入真实姓名"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="userForm.phone" placeholder="请输入手机号" maxlength="11"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>

        <!-- 前台/医生特有字段 -->
        <div v-if="userForm.userType !== 'USER'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="工号" prop="employeeId">
                <el-input v-model="userForm.employeeId" placeholder="请输入工号"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="状态">
                <el-select v-model="userForm.status" style="width: 100%;">
                  <el-option label="在职" :value="1"></el-option>
                  <el-option label="离职" :value="0"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 医生特有字段 -->
        <div v-if="userForm.userType === 'DOCTOR'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="科室" prop="department">
                <el-select v-model="userForm.department" placeholder="请选择科室" style="width: 100%;">
                  <el-option label="内科" value="内科"></el-option>
                  <el-option label="外科" value="外科"></el-option>
                  <el-option label="眼科" value="眼科"></el-option>
                  <el-option label="皮肤科" value="皮肤科"></el-option>
                  <el-option label="牙科" value="牙科"></el-option>
                  <el-option label="心脏科" value="心脏科"></el-option>
                  <el-option label="急诊科" value="急诊科"></el-option>
                  <el-option label="骨科" value="骨科"></el-option>
                  <el-option label="神经科" value="神经科"></el-option>
                  <el-option label="肿瘤科" value="肿瘤科"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="职称">
                <el-select v-model="userForm.title" placeholder="请选择职称" style="width: 100%;">
                  <el-option label="医师" value="医师"></el-option>
                  <el-option label="主治医师" value="主治医师"></el-option>
                  <el-option label="副主任医师" value="副主任医师"></el-option>
                  <el-option label="主任医师" value="主任医师"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="专长">
            <el-input v-model="userForm.specialty" placeholder="请输入专长"></el-input>
          </el-form-item>

          <el-form-item label="简介">
            <el-input 
              v-model="userForm.description" 
              type="textarea" 
              :rows="3" 
              placeholder="请输入医生简介"></el-input>
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog title="用户详情" v-model="detailDialogVisible" width="600px">
      <el-descriptions :column="2" border v-if="currentUser">
        <el-descriptions-item label="用户类型">
          <el-tag :type="getUserTypeColor(currentUser.userType)">
            {{ getUserTypeText(currentUser.userType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'">
            {{ currentUser.status === 1 ? '在职' : '离职' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ currentUser.realName }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="工号" v-if="currentUser.employeeId">{{ currentUser.employeeId }}</el-descriptions-item>
        <el-descriptions-item label="科室" v-if="currentUser.department">{{ currentUser.department }}</el-descriptions-item>
        <el-descriptions-item label="职称" v-if="currentUser.title">{{ currentUser.title }}</el-descriptions-item>
        <el-descriptions-item label="专长" v-if="currentUser.specialty" :span="2">{{ currentUser.specialty }}</el-descriptions-item>
        <el-descriptions-item label="简介" v-if="currentUser.description" :span="2">{{ currentUser.description }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus';
import api from '@/services/api';

export default {
  name: 'UserManagement',
  data() {
    return {
      loading: false,
      userType: 'all',
      users: [],
      dialogVisible: false,
      detailDialogVisible: false,
      dialogMode: 'add',
      submitting: false,
      currentUser: null,
      userForm: {
        userType: 'USER',
        username: '',
        password: '',
        realName: '',
        phone: '',
        email: '',
        employeeId: '',
        department: '',
        title: '',
        specialty: '',
        description: '',
        status: 1
      },
      formRules: {
        userType: [{ required: true, message: '请选择用户类型', trigger: 'change' }],
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 50, message: '长度在 3 到 50 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码至少6个字符', trigger: 'blur' }
        ],
        realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
        ]
      }
    };
  },
  mounted() {
    this.loadUsers();
  },
  methods: {
    async loadUsers() {
      this.loading = true;
      try {
        let allUsers = [];

        if (this.userType === 'all' || this.userType === 'USER') {
          const usersResp = await api.get('/api/users');
          const users = usersResp.data.success ? usersResp.data.data : usersResp.data;
          allUsers.push(...users.map(u => ({ ...u, userType: 'USER' })));
        }

        if (this.userType === 'all' || this.userType === 'RECEPTIONIST') {
          const receptResp = await api.get('/api/receptionists');
          const receptionists = receptResp.data;
          allUsers.push(...receptionists.map(r => ({ ...r, userType: 'RECEPTIONIST' })));
        }

        if (this.userType === 'all' || this.userType === 'DOCTOR') {
          const doctorsResp = await api.get('/api/doctors');
          const doctors = doctorsResp.data;
          allUsers.push(...doctors.map(d => ({ ...d, userType: 'DOCTOR' })));
        }

        this.users = allUsers;
      } catch (error) {
        ElMessage.error('加载用户列表失败: ' + error.message);
      } finally {
        this.loading = false;
      }
    },

    showAddDialog() {
      this.dialogMode = 'add';
      this.resetForm();
      this.dialogVisible = true;
    },

    editUser(user) {
      this.dialogMode = 'edit';
      this.currentUser = user;
      this.userForm = {
        id: user.id,
        userType: user.userType,
        username: user.username,
        password: '',
        realName: user.realName,
        phone: user.phone || '',
        email: user.email || '',
        employeeId: user.employeeId || '',
        department: user.department || '',
        title: user.title || '',
        specialty: user.specialty || '',
        description: user.description || '',
        status: user.status !== undefined ? user.status : 1
      };
      this.dialogVisible = true;
    },

    viewDetail(user) {
      this.currentUser = user;
      this.detailDialogVisible = true;
    },

    async submitForm() {
      try {
        await this.$refs.userFormRef.validate();
        this.submitting = true;

        let url = '';
        let method = this.dialogMode === 'add' ? 'post' : 'put';
        
        if (this.userForm.userType === 'USER') {
          url = this.dialogMode === 'add' ? '/api/users' : `/api/users/${this.userForm.id}`;
        } else if (this.userForm.userType === 'RECEPTIONIST') {
          url = this.dialogMode === 'add' ? '/api/receptionists' : `/api/receptionists/${this.userForm.id}`;
        } else if (this.userForm.userType === 'DOCTOR') {
          url = this.dialogMode === 'add' ? '/api/doctors' : `/api/doctors/${this.userForm.id}`;
        }

        const submitData = { ...this.userForm };
        if (this.dialogMode === 'edit' && !submitData.password) {
          delete submitData.password;
        }

        const response = await api[method](url, submitData);
        
        if (response.data.success || response.status === 200) {
          ElMessage.success(this.dialogMode === 'add' ? '添加成功' : '修改成功');
          this.dialogVisible = false;
          this.loadUsers();
        } else {
          ElMessage.error(response.data.message || '操作失败');
        }
      } catch (error) {
        if (error.message) {
          ElMessage.error('操作失败: ' + error.message);
        }
      } finally {
        this.submitting = false;
      }
    },

    async toggleStatus(user) {
      try {
        await ElMessageBox.confirm(
          `确定要${user.status === 1 ? '停用' : '启用'}用户 ${user.realName} 吗？`,
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        );

        const newStatus = user.status === 1 ? 0 : 1;
        let url = '';
        
        if (user.userType === 'RECEPTIONIST') {
          url = `/api/receptionists/${user.id}`;
        } else if (user.userType === 'DOCTOR') {
          url = `/api/doctors/${user.id}`;
        } else {
          ElMessage.warning('普通用户不支持停用操作');
          return;
        }

        await api.put(url, { ...user, status: newStatus });
        ElMessage.success('操作成功');
        this.loadUsers();
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('操作失败: ' + error.message);
        }
      }
    },

    resetForm() {
      this.userForm = {
        userType: 'USER',
        username: '',
        password: '',
        realName: '',
        phone: '',
        email: '',
        employeeId: '',
        department: '',
        title: '',
        specialty: '',
        description: '',
        status: 1
      };
      if (this.$refs.userFormRef) {
        this.$refs.userFormRef.resetFields();
      }
    },

    getUserTypeText(type) {
      const map = {
        'USER': '普通用户',
        'RECEPTIONIST': '前台',
        'DOCTOR': '医生'
      };
      return map[type] || type;
    },

    getUserTypeColor(type) {
      const map = {
        'USER': 'info',
        'RECEPTIONIST': 'success',
        'DOCTOR': 'primary'
      };
      return map[type] || 'info';
    }
  }
};
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}
</style>
