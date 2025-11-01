<template>
  <div class="inventory-management">
    <el-card class="inventory-card">
      <template #header>
        <div class="card-header">
          <span>药品库存</span>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索药品名称"
              style="width: 200px; margin-right: 10px;"
              clearable
            />
            <el-button type="primary" @click="showAddMedicineDialog">添加药品</el-button>
          </div>
        </div>
      </template>

      <!-- 药品列表 -->
      <el-table :data="filteredMedicines" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="药品名称" width="150"></el-table-column>
        <el-table-column prop="description" label="描述"></el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template #default="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="stockQuantity" label="库存数量" width="100">
          <template #default="scope">
            <el-tag :type="getStockStatusType(scope.row.stockQuantity)">
              {{ scope.row.stockQuantity }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="80"></el-table-column>
        <el-table-column prop="manufacturer" label="生产厂家" width="150"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="showEditMedicineDialog(scope.row)">编辑</el-button>
            <el-button 
              size="small" 
              type="warning" 
              @click="showStockInDialog(scope.row)"
              v-if="scope.row.stockQuantity < 10"
            >
              补货
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑药品对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="medicineForm" :rules="medicineRules" ref="medicineFormRef" label-width="100px">
        <el-form-item label="药品名称" prop="name">
          <el-input v-model="medicineForm.name"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="medicineForm.description" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="medicineForm.price" :min="0" :precision="2" :step="0.01"></el-input-number>
        </el-form-item>
        <el-form-item label="库存数量" prop="stockQuantity">
          <el-input-number v-model="medicineForm.stockQuantity" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="medicineForm.unit"></el-input>
        </el-form-item>
        <el-form-item label="生产厂家" prop="manufacturer">
          <el-input v-model="medicineForm.manufacturer"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveMedicine">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 补货对话框 -->
    <el-dialog title="药品补货" v-model="stockInDialogVisible" width="400px">
      <el-form :model="stockInForm" label-width="100px">
        <el-form-item label="药品名称">
          <el-input v-model="stockInForm.medicineName" disabled></el-input>
        </el-form-item>
        <el-form-item label="当前库存">
          <el-input v-model="stockInForm.currentStock" disabled></el-input>
        </el-form-item>
        <el-form-item label="补货数量" prop="quantity">
          <el-input-number v-model="stockInForm.quantity" :min="1"></el-input-number>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="stockInDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="processStockIn">确认补货</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus';

export default {
  name: 'InventoryManagement',
  data() {
    return {
      loading: false,
      medicines: [],
      searchKeyword: '',
      dialogVisible: false,
      stockInDialogVisible: false,
      isEdit: false,
      medicineForm: {
        id: null,
        name: '',
        description: '',
        price: 0,
        stockQuantity: 0,
        unit: '',
        manufacturer: ''
      },
      stockInForm: {
        medicineId: null,
        medicineName: '',
        currentStock: 0,
        quantity: 1
      },
      medicineRules: {
        name: [
          { required: true, message: '请输入药品名称', trigger: 'blur' }
        ],
        price: [
          { required: true, message: '请输入价格', trigger: 'blur' }
        ],
        stockQuantity: [
          { required: true, message: '请输入库存数量', trigger: 'blur' }
        ]
      }
    };
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑药品' : '添加药品';
    },
    filteredMedicines() {
      if (!this.searchKeyword) {
        return this.medicines;
      }
      const keyword = this.searchKeyword.toLowerCase();
      return this.medicines.filter(medicine => 
        medicine.name && medicine.name.toLowerCase().includes(keyword)
      );
    }
  },
  mounted() {
    this.loadMedicines();
  },
  methods: {
    loadMedicines() {
      this.loading = true;
      try {
        // 暂时使用模拟数据
        this.medicines = [
          {
            id: 1,
            name: '阿莫西林胶囊',
            description: '抗生素类药物，用于治疗细菌感染',
            price: 25.00,
            stockQuantity: 100,
            unit: '盒',
            manufacturer: 'XX制药有限公司'
          },
          {
            id: 2,
            name: '维生素C片',
            description: '补充维生素C，增强免疫力',
            price: 15.00,
            stockQuantity: 5,  // 库存较少，会显示警告
            unit: '瓶',
            manufacturer: 'YY药业有限公司'
          }
        ];
      } catch (error) {
        ElMessage.error('获取药品列表失败: ' + error.message);
      } finally {
        this.loading = false;
      }
    },

    showAddMedicineDialog() {
      this.isEdit = false;
      this.medicineForm = {
        id: null,
        name: '',
        description: '',
        price: 0,
        stockQuantity: 0,
        unit: '',
        manufacturer: ''
      };
      this.dialogVisible = true;
    },

    showEditMedicineDialog(medicine) {
      this.isEdit = true;
      this.medicineForm = { ...medicine };
      this.dialogVisible = true;
    },

    saveMedicine() {
      this.$refs.medicineFormRef.validate((valid) => {
        if (valid) {
          if (this.isEdit) {
            // 编辑药品
            const index = this.medicines.findIndex(m => m.id === this.medicineForm.id);
            if (index !== -1) {
              this.medicines.splice(index, 1, { ...this.medicineForm });
              ElMessage.success('药品信息更新成功');
            }
          } else {
            // 添加药品
            const newMedicine = {
              ...this.medicineForm,
              id: this.medicines.length + 1
            };
            this.medicines.push(newMedicine);
            ElMessage.success('药品添加成功');
          }
          this.dialogVisible = false;
        }
      });
    },

    showStockInDialog(medicine) {
      this.stockInForm = {
        medicineId: medicine.id,
        medicineName: medicine.name,
        currentStock: medicine.stockQuantity,
        quantity: 1
      };
      this.stockInDialogVisible = true;
    },

    processStockIn() {
      const index = this.medicines.findIndex(m => m.id === this.stockInForm.medicineId);
      if (index !== -1) {
        this.medicines[index].stockQuantity += this.stockInForm.quantity;
        ElMessage.success(`药品 ${this.stockInForm.medicineName} 补货成功，当前库存: ${this.medicines[index].stockQuantity}`);
      }
      this.stockInDialogVisible = false;
    },

    getStockStatusType(quantity) {
      if (quantity < 10) {
        return 'danger';  // 库存不足，红色警告
      } else if (quantity < 50) {
        return 'warning'; // 库存较少，黄色警告
      } else {
        return 'success'; // 库存充足，绿色正常
      }
    }
  }
};
</script>

<style scoped>
.inventory-management {
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