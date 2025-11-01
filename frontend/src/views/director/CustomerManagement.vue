<template>
  <div class="customer-management">
    <el-card class="customer-card">
      <template #header>
        <div class="card-header">
          <span>客户管理</span>
          <el-button type="primary" @click="showAddCustomerDialog">添加客户</el-button>
        </div>
      </template>

      <!-- 客户列表 -->
      <el-table :data="customers" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="realName" label="客户姓名" width="120"></el-table-column>
        <el-table-column prop="phone" label="联系电话" width="120"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="150"></el-table-column>
        <el-table-column prop="address" label="地址"></el-table-column>
        <el-table-column prop="wechat" label="微信号" width="120"></el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="showEditCustomerDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteCustomer(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next, jumper"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑客户对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="customerForm" :rules="customerRules" ref="customerFormRef" label-width="100px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model.number="customerForm.userId" :disabled="isEdit"></el-input>
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
        <el-form-item label="微信号" prop="wechat">
          <el-input v-model="customerForm.wechat"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveCustomer">保存</el-button>
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
      loading: true,
      customers: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      isEdit: false,
      customerForm: {
        id: null,
        userId: null,
        realName: '',
        phone: '',
        email: '',
        address: '',
        wechat: ''
      },
      customerRules: {
        realName: [
          { required: true, message: '请输入客户姓名', trigger: 'blur' }
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
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑客户' : '添加客户';
    }
  },
  mounted() {
    this.loadCustomers();
  },
  methods: {
    async loadCustomers() {
      this.loading = true;
      try {
        const response = await api.get('/api/customers');
        this.customers = response.data;
        this.total = this.customers.length;
      } catch (error) {
        ElMessage.error('获取客户列表失败: ' + error.message);
      } finally {
        this.loading = false;
      }
    },

    handleCurrentChange(page) {
      this.currentPage = page;
      // 这里可以添加分页逻辑
    },

    showAddCustomerDialog() {
      this.isEdit = false;
      this.customerForm = {
        id: null,
        userId: null,
        realName: '',
        phone: '',
        email: '',
        address: '',
        wechat: ''
      };
      this.dialogVisible = true;
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
            let response;
            if (this.isEdit) {
              // 编辑客户
              response = await api.put(`/api/customers/${this.customerForm.id}`, this.customerForm);
            } else {
              // 添加客户
              response = await api.post('/api/customers', this.customerForm);
            }

            if (response.data.success) {
              ElMessage.success(response.data.message);
              this.dialogVisible = false;
              this.loadCustomers();
            } else {
              ElMessage.error(response.data.message);
            }
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
          const response = await api.delete(`/api/customers/${id}`);
          if (response.data.success) {
            ElMessage.success(response.data.message);
            this.loadCustomers();
          } else {
            ElMessage.error(response.data.message);
          }
        } catch (error) {
          ElMessage.error('删除客户失败: ' + error.message);
        }
      }).catch(() => {
        // 用户取消删除
      });
    },

    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleString('zh-CN');
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>