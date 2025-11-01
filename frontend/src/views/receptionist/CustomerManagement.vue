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
            <el-button type="primary" @click="showAddCustomerDialog">添加客户</el-button>
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
        <el-table-column prop="wechat" label="微信号" width="120"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="showEditCustomerDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteCustomer(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑客户对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="customerForm" :rules="customerRules" ref="customerFormRef" label-width="100px">
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
      loading: false,
      customers: [],
      searchKeyword: '',
      dialogVisible: false,
      isEdit: false,
      customerForm: {
        id: null,
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
    },
    filteredCustomers() {
      if (!this.searchKeyword) {
        return this.customers;
      }
      const keyword = this.searchKeyword.toLowerCase();
      return this.customers.filter(customer => 
        (customer.realName && customer.realName.toLowerCase().includes(keyword)) ||
        (customer.phone && customer.phone.includes(keyword))
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
        // 这里应该调用实际的API获取客户列表
        // 暂时使用模拟数据
        this.customers = [
          {
            id: 1,
            realName: '张三',
            phone: '13800138001',
            email: 'zhangsan@example.com',
            address: '北京市朝阳区某某街道',
            wechat: 'zhangsan_wechat'
          },
          {
            id: 2,
            realName: '李四',
            phone: '13800138002',
            email: 'lisi@example.com',
            address: '上海市浦东新区某某街道',
            wechat: 'lisi_wechat'
          }
        ];
      } catch (error) {
        ElMessage.error('获取客户列表失败: ' + error.message);
      } finally {
        this.loading = false;
      }
    },

    showAddCustomerDialog() {
      this.isEdit = false;
      this.customerForm = {
        id: null,
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

    saveCustomer() {
      this.$refs.customerFormRef.validate((valid) => {
        if (valid) {
          if (this.isEdit) {
            // 编辑客户
            const index = this.customers.findIndex(c => c.id === this.customerForm.id);
            if (index !== -1) {
              this.customers.splice(index, 1, { ...this.customerForm });
              ElMessage.success('客户信息更新成功');
            }
          } else {
            // 添加客户
            const newCustomer = {
              ...this.customerForm,
              id: this.customers.length + 1
            };
            this.customers.push(newCustomer);
            ElMessage.success('客户添加成功');
          }
          this.dialogVisible = false;
        }
      });
    },

    deleteCustomer(id) {
      ElMessageBox.confirm('确定要删除这个客户吗？此操作不可恢复！', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.customers = this.customers.filter(customer => customer.id !== id);
        ElMessage.success('客户删除成功');
      }).catch(() => {
        // 用户取消删除
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