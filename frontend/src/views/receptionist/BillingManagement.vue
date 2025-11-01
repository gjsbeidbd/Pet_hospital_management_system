<template>
  <div class="billing-management">
    <el-card class="billing-card">
      <template #header>
        <div class="card-header">
          <span>收费管理</span>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索客户姓名或宠物名称"
              style="width: 200px; margin-right: 10px;"
              clearable
            />
            <el-button type="primary" @click="showAddBillingDialog">添加收费</el-button>
          </div>
        </div>
      </template>

      <!-- 收费列表 -->
      <el-table :data="filteredBillings" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="customerName" label="客户姓名" width="120"></el-table-column>
        <el-table-column prop="petName" label="宠物名称" width="120"></el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="100">
          <template #default="scope">
            ¥{{ scope.row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="paidAmount" label="已付金额" width="100">
          <template #default="scope">
            ¥{{ scope.row.paidAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="支付状态" width="100">
          <template #default="scope">
            <el-tag :type="getPaymentStatusType(scope.row.status)">
              {{ getPaymentStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="100">
          <template #default="scope">
            {{ getPaymentMethodText(scope.row.paymentMethod) }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="showEditBillingDialog(scope.row)">编辑</el-button>
            <el-button 
              size="small" 
              type="success" 
              @click="processPayment(scope.row)" 
              v-if="scope.row.status !== 'PAID'"
            >
              收款
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑收费对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="billingForm" :rules="billingRules" ref="billingFormRef" label-width="100px">
        <el-form-item label="客户姓名" prop="customerName">
          <el-input v-model="billingForm.customerName"></el-input>
        </el-form-item>
        <el-form-item label="宠物名称" prop="petName">
          <el-input v-model="billingForm.petName"></el-input>
        </el-form-item>
        <el-form-item label="总金额" prop="totalAmount">
          <el-input-number v-model="billingForm.totalAmount" :min="0" :precision="2" :step="0.01"></el-input-number>
        </el-form-item>
        <el-form-item label="已付金额" prop="paidAmount">
          <el-input-number v-model="billingForm.paidAmount" :min="0" :precision="2" :step="0.01"></el-input-number>
        </el-form-item>
        <el-form-item label="支付状态" prop="status">
          <el-select v-model="billingForm.status" placeholder="请选择支付状态">
            <el-option label="待支付" value="PENDING"></el-option>
            <el-option label="部分支付" value="PARTIAL"></el-option>
            <el-option label="已支付" value="PAID"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="支付方式" prop="paymentMethod">
          <el-select v-model="billingForm.paymentMethod" placeholder="请选择支付方式">
            <el-option label="现金" value="CASH"></el-option>
            <el-option label="银行卡" value="CARD"></el-option>
            <el-option label="支付宝" value="ALIPAY"></el-option>
            <el-option label="微信" value="WECHAT"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="notes">
          <el-input v-model="billingForm.notes" type="textarea"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveBilling">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus';

export default {
  name: 'BillingManagement',
  data() {
    return {
      loading: false,
      billings: [],
      searchKeyword: '',
      dialogVisible: false,
      isEdit: false,
      billingForm: {
        id: null,
        customerName: '',
        petName: '',
        totalAmount: 0,
        paidAmount: 0,
        status: 'PENDING',
        paymentMethod: '',
        notes: '',
        createdAt: ''
      },
      billingRules: {
        customerName: [
          { required: true, message: '请输入客户姓名', trigger: 'blur' }
        ],
        petName: [
          { required: true, message: '请输入宠物名称', trigger: 'blur' }
        ],
        totalAmount: [
          { required: true, message: '请输入总金额', trigger: 'blur' }
        ]
      }
    };
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑收费' : '添加收费';
    },
    filteredBillings() {
      if (!this.searchKeyword) {
        return this.billings;
      }
      const keyword = this.searchKeyword.toLowerCase();
      return this.billings.filter(billing => 
        (billing.customerName && billing.customerName.toLowerCase().includes(keyword)) ||
        (billing.petName && billing.petName.toLowerCase().includes(keyword))
      );
    }
  },
  mounted() {
    this.loadBillings();
  },
  methods: {
    loadBillings() {
      this.loading = true;
      try {
        // 暂时使用模拟数据
        this.billings = [
          {
            id: 1,
            customerName: '张三',
            petName: '小白',
            totalAmount: 200.00,
            paidAmount: 200.00,
            status: 'PAID',
            paymentMethod: 'WECHAT',
            notes: '疫苗接种费用',
            createdAt: '2023-06-15 09:30'
          },
          {
            id: 2,
            customerName: '李四',
            petName: '小黑',
            totalAmount: 350.00,
            paidAmount: 100.00,
            status: 'PARTIAL',
            paymentMethod: 'CASH',
            notes: '治疗费用',
            createdAt: '2023-06-15 11:00'
          }
        ];
      } catch (error) {
        ElMessage.error('获取收费列表失败: ' + error.message);
      } finally {
        this.loading = false;
      }
    },

    showAddBillingDialog() {
      this.isEdit = false;
      const now = new Date();
      this.billingForm = {
        id: null,
        customerName: '',
        petName: '',
        totalAmount: 0,
        paidAmount: 0,
        status: 'PENDING',
        paymentMethod: '',
        notes: '',
        createdAt: now.toLocaleString('zh-CN')
      };
      this.dialogVisible = true;
    },

    showEditBillingDialog(billing) {
      this.isEdit = true;
      this.billingForm = { ...billing };
      this.dialogVisible = true;
    },

    saveBilling() {
      this.$refs.billingFormRef.validate((valid) => {
        if (valid) {
          if (this.isEdit) {
            // 编辑收费
            const index = this.billings.findIndex(b => b.id === this.billingForm.id);
            if (index !== -1) {
              this.billings.splice(index, 1, { ...this.billingForm });
              ElMessage.success('收费信息更新成功');
            }
          } else {
            // 添加收费
            const newBilling = {
              ...this.billingForm,
              id: this.billings.length + 1
            };
            this.billings.push(newBilling);
            ElMessage.success('收费添加成功');
          }
          this.dialogVisible = false;
        }
      });
    },

    processPayment(billing) {
      ElMessage.success(`处理 ${billing.customerName} 的收款`);
      // 实际项目中这里会调用支付接口
    },

    getPaymentStatusType(status) {
      const statusMap = {
        'PENDING': 'info',
        'PARTIAL': 'warning',
        'PAID': 'success'
      };
      return statusMap[status] || 'info';
    },

    getPaymentStatusText(status) {
      const textMap = {
        'PENDING': '待支付',
        'PARTIAL': '部分支付',
        'PAID': '已支付'
      };
      return textMap[status] || status;
    },

    getPaymentMethodText(method) {
      const textMap = {
        'CASH': '现金',
        'CARD': '银行卡',
        'ALIPAY': '支付宝',
        'WECHAT': '微信'
      };
      return textMap[method] || method;
    }
  }
};
</script>

<style scoped>
.billing-management {
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