<template>
  <div class="customer-management">
    <el-card class="customer-card">
      <template #header>
        <div class="card-header">
          <span>客户管理</span>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索客户姓名或电话"
              style="width: 200px; margin-right: 10px;"
              clearable
            />
          </div>
        </div>
      </template>

      <!-- 客户列表 -->
      <el-table :data="filteredCustomers" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="realName" label="客户姓名" width="120"></el-table-column>
        <el-table-column prop="phone" label="联系电话" width="120"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="150"></el-table-column>
        <el-table-column prop="address" label="地址"></el-table-column>
        <el-table-column prop="username" label="用户名" width="150"></el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="scope">
            <el-button size="small" @click="showEditCustomerDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="warning" @click="showResetPasswordDialog(scope.row)">重置密码</el-button>
            <el-button size="small" type="danger" @click="deleteCustomer(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑客户对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="customerForm" :rules="customerRules" ref="customerFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="customerForm.username" :disabled="isEdit"></el-input>
        </el-form-item>
        <el-form-item label="客户姓名" prop="realName">
          <el-input v-model="customerForm.realName"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="customerForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="customerForm.email"></el-input>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="customerForm.address" type="textarea"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveCustomer">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog title="重置密码" v-model="resetPasswordDialogVisible" width="400px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="passwordForm.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="客户姓名">
          <el-input v-model="passwordForm.realName" disabled></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="resetPasswordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="resetPassword">确认重置</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus';
import api from '@/services/api';

export default {
  name: 'CustomerManagement',
  data() {
    return {
      loading: false,
      customers: [],
      searchKeyword: '',
      dialogVisible: false,
      isEdit: false,
      resetPasswordDialogVisible: false,
      customerForm: {
        id: null,
        username: '',
        realName: '',
        phone: '',
        email: '',
        address: ''
      },
      passwordForm: {
        userId: null,
        username: '',
        realName: '',
        newPassword: '',
        confirmPassword: ''
      },
      customerRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入客户姓名', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        email: [
          { pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      },
      passwordRules: {
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { 
            validator: (rule, value, callback) => {
              if (value !== this.passwordForm.newPassword) {
                callback(new Error('两次输入的密码不一致'));
              } else {
                callback();
              }
            }, 
            trigger: 'blur' 
          }
        ]
      }
    };
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑客户' : '添加客户';
    },
    filteredCustomers() {
      if (!this.searchKeyword) {
        return this.customers;
      }
      const keyword = this.searchKeyword.toLowerCase();
      return this.customers.filter(customer => 
        (customer.realName && customer.realName.toLowerCase().includes(keyword)) ||
        (customer.phone && customer.phone.includes(keyword)) ||
        (customer.username && customer.username.toLowerCase().includes(keyword))
      );
    }
  },
  mounted() {
    this.loadCustomers();
  },
  methods: {
    async loadCustomers() {
      this.loading = true;
      try {
        const response = await api.get('/api/users');
        if (response.data.success) {
          this.customers = response.data.data;
        }
      } catch (error) {
        ElMessage.error('获取客户列表失败: ' + error.message);
      } finally {
        this.loading = false;
      }
    },

    showEditCustomerDialog(customer) {
      this.isEdit = true;
      this.customerForm = { ...customer };
      this.dialogVisible = true;
    },

    async saveCustomer() {
      this.$refs.customerFormRef.validate(async (valid) => {
        if (valid) {
          try {
            // 只保留编辑功能
            const response = await api.put(`/api/users/profile/${this.customerForm.id}`, this.customerForm);
            ElMessage.success('客户信息更新成功');
            
            this.dialogVisible = false;
            this.loadCustomers();
          } catch (error) {
            ElMessage.error('保存客户信息失败: ' + error.message);
          }
        }
      });
    },

    deleteCustomer(id) {
      ElMessageBox.confirm('确定要删除这个客户吗？此操作不可恢复！', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await api.post('/api/users/delete-account', {
            userId: id,
            password: '123456' // 默认密码，实际应用中应该让用户输入
          });
          ElMessage.success('客户删除成功');
          this.loadCustomers();
        } catch (error) {
          ElMessage.error('删除客户失败: ' + error.message);
        }
      }).catch(() => {
        // 用户取消删除
      });
    },

    showResetPasswordDialog(customer) {
      this.passwordForm = {
        userId: customer.id,
        username: customer.username,
        realName: customer.realName,
        newPassword: '',
        confirmPassword: ''
      };
      this.resetPasswordDialogVisible = true;
    },

    resetPassword() {
      this.$refs.passwordFormRef.validate(async (valid) => {
        if (valid) {
          try {
            await api.post(`/api/users/reset-password/${this.passwordForm.userId}`, {
              newPassword: this.passwordForm.newPassword
            });
            ElMessage.success('密码重置成功');
            this.resetPasswordDialogVisible = false;
            // 重置表单
            this.passwordForm = {
              userId: null,
              username: '',
              realName: '',
              newPassword: '',
              confirmPassword: ''
            };
          } catch (error) {
            ElMessage.error('密码重置失败: ' + error.message);
          }
        }
      });
    }
  }
};
</script>

<style scoped>
.customer-management {
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