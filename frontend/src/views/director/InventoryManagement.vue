<template>
  <div class="inventory-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>药品库存管理</span>
          <div class="header-actions">
            <el-select
              v-model="selectedCategory"
              placeholder="筛选分类"
              clearable
              style="width: 200px; margin-right: 10px;"
              @change="handleCategoryChange">
              <el-option label="全部分类" value=""></el-option>
              <el-option label="抗寄生虫（体内）" value="抗寄生虫_体内"></el-option>
              <el-option label="抗寄生虫（体外）" value="抗寄生虫_体外"></el-option>
              <el-option label="抗寄生虫（广谱）" value="抗寄生虫_广谱"></el-option>
              <el-option label="皮肤与耳道用药" value="皮肤与耳道用药"></el-option>
              <el-option label="抗感染（抗生素）" value="抗感染_抗生素"></el-option>
              <el-option label="消化道用药" value="消化道用药"></el-option>
              <el-option label="镇痛与麻醉" value="镇痛与麻醉"></el-option>
              <el-option label="心血管用药" value="心血管用药"></el-option>
              <el-option label="呼吸系统用药" value="呼吸系统用药"></el-option>
              <el-option label="眼科用药" value="眼科用药"></el-option>
              <el-option label="解毒与支持治疗" value="解毒与支持治疗"></el-option>
              <el-option label="营养与代谢" value="营养与代谢"></el-option>
            </el-select>
            <el-select
              v-model="selectedSpecies"
              placeholder="筛选物种"
              clearable
              style="width: 150px; margin-right: 10px;"
              @change="handleSpeciesChange">
              <el-option label="全部物种" value=""></el-option>
              <el-option label="猫" value="cat"></el-option>
              <el-option label="狗" value="dog"></el-option>
              <el-option label="鸟" value="bird"></el-option>
              <el-option label="兔子" value="rabbit"></el-option>
              <el-option label="仓鼠" value="hamster"></el-option>
            </el-select>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索药品名称"
              style="width: 200px; margin-right: 10px;"
              clearable
            />
            <el-button type="primary" @click="showAddDialog" icon="el-icon-plus">添加药品</el-button>
          </div>
        </div>
      </template>

      <!-- 统计卡片 -->
      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-icon" style="background: #409EFF;">
                <i class="el-icon-box"></i>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ totalMedicines }}</div>
                <div class="stat-label">药品种类</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-icon" style="background: #67C23A;">
                <i class="el-icon-success"></i>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ normalStockCount }}</div>
                <div class="stat-label">库存正常</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-icon" style="background: #E6A23C;">
                <i class="el-icon-warning"></i>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ lowStockCount }}</div>
                <div class="stat-label">库存预警</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div class="stat-card">
              <div class="stat-icon" style="background: #F56C6C;">
                <i class="el-icon-error"></i>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ criticalStockCount }}</div>
                <div class="stat-label">库存不足</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 药品列表 -->
      <el-table :data="filteredMedicines" style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60"></el-table-column>
        <el-table-column prop="name" label="药品名称" width="150"></el-table-column>
        <el-table-column prop="category" label="分类" width="150">
          <template #default="scope">
            {{ formatCategory(scope.row.category) }}
          </template>
        </el-table-column>
        <el-table-column label="适用物种" width="180">
          <template #default="scope">
            <el-tag v-if="scope.row.applicableCat" size="small" style="margin-right: 3px;">猫</el-tag>
            <el-tag v-if="scope.row.applicableDog" size="small" type="success" style="margin-right: 3px;">狗</el-tag>
            <el-tag v-if="scope.row.applicableBird" size="small" type="warning" style="margin-right: 3px;">鸟</el-tag>
            <el-tag v-if="scope.row.applicableRabbit" size="small" type="info" style="margin-right: 3px;">兔</el-tag>
            <el-tag v-if="scope.row.applicableHamster" size="small" type="danger" style="margin-right: 3px;">鼠</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="180"></el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template #default="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="stockQuantity" label="库存数量" width="120">
          <template #default="scope">
            <el-tag :type="getStockStatusType(scope.row.stockQuantity)">
              {{ scope.row.stockQuantity }} {{ scope.row.unit }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="manufacturer" label="生产厂家" width="150"></el-table-column>
        <el-table-column prop="expiryDate" label="有效期" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.expiryDate) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewDetail(scope.row)">查看</el-button>
            <el-button size="small" type="primary" @click="editMedicine(scope.row)">编辑</el-button>
            <el-button 
              size="small" 
              type="warning" 
              @click="showStockInDialog(scope.row)">
              补货
            </el-button>
            <el-button size="small" type="danger" @click="deleteMedicine(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="filteredMedicines.length === 0 && !loading" description="暂无药品数据"></el-empty>
    </el-card>

    <!-- 添加/编辑药品对话框 -->
    <el-dialog 
      :title="dialogMode === 'add' ? '添加药品' : '编辑药品'" 
      v-model="dialogVisible" 
      width="600px"
      @close="resetForm">
      <el-form :model="medicineForm" :rules="formRules" ref="medicineFormRef" label-width="100px">
        <el-form-item label="药品名称" prop="name">
          <el-input v-model="medicineForm.name" placeholder="请输入药品名称"></el-input>
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="medicineForm.category" placeholder="请选择分类" style="width: 100%;">
            <el-option label="抗寄生虫（体内）" value="抗寄生虫_体内"></el-option>
            <el-option label="抗寄生虫（体外）" value="抗寄生虫_体外"></el-option>
            <el-option label="抗寄生虫（广谱）" value="抗寄生虫_广谱"></el-option>
            <el-option label="皮肤与耳道用药" value="皮肤与耳道用药"></el-option>
            <el-option label="抗感染（抗生素）" value="抗感染_抗生素"></el-option>
            <el-option label="消化道用药" value="消化道用药"></el-option>
            <el-option label="镇痛与麻醉" value="镇痛与麻醉"></el-option>
            <el-option label="心血管用药" value="心血管用药"></el-option>
            <el-option label="呼吸系统用药" value="呼吸系统用药"></el-option>
            <el-option label="眼科用药" value="眼科用药"></el-option>
            <el-option label="解毒与支持治疗" value="解毒与支持治疗"></el-option>
            <el-option label="营养与代谢" value="营养与代谢"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="适用物种">
          <el-checkbox-group v-model="medicineForm.applicableSpecies">
            <el-checkbox label="cat">猫</el-checkbox>
            <el-checkbox label="dog">狗</el-checkbox>
            <el-checkbox label="bird">鸟</el-checkbox>
            <el-checkbox label="rabbit">兔子</el-checkbox>
            <el-checkbox label="hamster">仓鼠</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="medicineForm.description" type="textarea" :rows="2" placeholder="请输入描述"></el-input>
        </el-form-item>
        <el-form-item label="用法用量">
          <el-input v-model="medicineForm.usageInstructions" type="textarea" :rows="2" placeholder="请输入用法用量"></el-input>
        </el-form-item>
        <el-form-item label="禁忌症">
          <el-input v-model="medicineForm.contraindications" type="textarea" :rows="2" placeholder="请输入禁忌症"></el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="medicineForm.price" :min="0" :precision="2" :step="0.01" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-select v-model="medicineForm.unit" placeholder="请选择单位" style="width: 100%;">
                <el-option label="盒" value="盒"></el-option>
                <el-option label="瓶" value="瓶"></el-option>
                <el-option label="支" value="支"></el-option>
                <el-option label="片" value="片"></el-option>
                <el-option label="粒" value="粒"></el-option>
                <el-option label="袋" value="袋"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库存数量" prop="stockQuantity">
              <el-input-number v-model="medicineForm.stockQuantity" :min="0" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="有效期" prop="expiryDate">
              <el-date-picker
                v-model="medicineForm.expiryDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%;"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="生产厂家" prop="manufacturer">
          <el-input v-model="medicineForm.manufacturer" placeholder="请输入生产厂家"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 补货对话框 -->
    <el-dialog title="药品补货" v-model="stockInDialogVisible" width="450px">
      <el-form :model="stockInForm" label-width="100px">
        <el-form-item label="药品名称">
          <el-input v-model="stockInForm.medicineName" disabled></el-input>
        </el-form-item>
        <el-form-item label="当前库存">
          <el-input v-model="stockInForm.currentStock" disabled>
            <template #append>{{ stockInForm.unit }}</template>
          </el-input>
        </el-form-item>
        <el-form-item label="补货数量">
          <el-input-number v-model="stockInForm.quantity" :min="1" style="width: 100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="补货后库存">
          <el-input :value="stockInForm.currentStock + stockInForm.quantity" disabled>
            <template #append>{{ stockInForm.unit }}</template>
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="stockInDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="processStockIn" :loading="stockInSubmitting">确认补货</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog title="药品详情" v-model="detailDialogVisible" width="700px">
      <el-descriptions :column="2" border v-if="currentMedicine">
        <el-descriptions-item label="药品名称" :span="2">{{ currentMedicine.name }}</el-descriptions-item>
        <el-descriptions-item label="分类" :span="2">{{ formatCategory(currentMedicine.category) }}</el-descriptions-item>
        <el-descriptions-item label="适用物种" :span="2">
          <el-tag v-if="currentMedicine.applicableCat" size="small" style="margin-right: 5px;">猫</el-tag>
          <el-tag v-if="currentMedicine.applicableDog" size="small" type="success" style="margin-right: 5px;">狗</el-tag>
          <el-tag v-if="currentMedicine.applicableBird" size="small" type="warning" style="margin-right: 5px;">鸟</el-tag>
          <el-tag v-if="currentMedicine.applicableRabbit" size="small" type="info" style="margin-right: 5px;">兔子</el-tag>
          <el-tag v-if="currentMedicine.applicableHamster" size="small" type="danger" style="margin-right: 5px;">仓鼠</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="价格">¥{{ currentMedicine.price }}</el-descriptions-item>
        <el-descriptions-item label="库存数量">
          <el-tag :type="getStockStatusType(currentMedicine.stockQuantity)">
            {{ currentMedicine.stockQuantity }} {{ currentMedicine.unit }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="单位">{{ currentMedicine.unit }}</el-descriptions-item>
        <el-descriptions-item label="生产厂家">{{ currentMedicine.manufacturer }}</el-descriptions-item>
        <el-descriptions-item label="有效期">{{ formatDate(currentMedicine.expiryDate) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentMedicine.status === 1 ? 'success' : 'info'">
            {{ currentMedicine.status === 1 ? '正常' : '停用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ currentMedicine.description || '-' }}</el-descriptions-item>
        <el-descriptions-item label="用法用量" :span="2">{{ currentMedicine.usageInstructions || '-' }}</el-descriptions-item>
        <el-descriptions-item label="禁忌症" :span="2">{{ currentMedicine.contraindications || '-' }}</el-descriptions-item>
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
  name: 'InventoryManagement',
  data() {
    return {
      loading: false,
      submitting: false,
      stockInSubmitting: false,
      medicines: [],
      searchKeyword: '',
      selectedCategory: '',
      selectedSpecies: '',
      dialogVisible: false,
      stockInDialogVisible: false,
      detailDialogVisible: false,
      dialogMode: 'add',
      currentMedicine: null,
      medicineForm: {
        name: '',
        category: '',
        description: '',
        usageInstructions: '',
        contraindications: '',
        price: 0,
        stockQuantity: 0,
        unit: '盒',
        manufacturer: '',
        expiryDate: '',
        applicableSpecies: ['cat', 'dog'],
        status: 1
      },
      stockInForm: {
        medicineId: null,
        medicineName: '',
        currentStock: 0,
        quantity: 1,
        unit: ''
      },
      formRules: {
        name: [{ required: true, message: '请输入药品名称', trigger: 'blur' }],
        category: [{ required: true, message: '请选择分类', trigger: 'change' }],
        price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
        unit: [{ required: true, message: '请选择单位', trigger: 'change' }],
        stockQuantity: [{ required: true, message: '请输入库存数量', trigger: 'blur' }],
        manufacturer: [{ required: true, message: '请输入生产厂家', trigger: 'blur' }]
      }
    };
  },
  computed: {
    filteredMedicines() {
      let result = this.medicines;
      
      // 按分类筛选
      if (this.selectedCategory) {
        result = result.filter(m => m.category === this.selectedCategory);
      }
      
      // 按物种筛选
      if (this.selectedSpecies) {
        const speciesField = `applicable${this.selectedSpecies.charAt(0).toUpperCase() + this.selectedSpecies.slice(1)}`;
        result = result.filter(m => m[speciesField] === 1);
      }
      
      // 按名称搜索
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase();
        result = result.filter(medicine => 
          medicine.name && medicine.name.toLowerCase().includes(keyword)
        );
      }
      
      return result;
    },
    totalMedicines() {
      return this.medicines.length;
    },
    normalStockCount() {
      return this.medicines.filter(m => m.stockQuantity >= 50).length;
    },
    lowStockCount() {
      return this.medicines.filter(m => m.stockQuantity >= 10 && m.stockQuantity < 50).length;
    },
    criticalStockCount() {
      return this.medicines.filter(m => m.stockQuantity < 10).length;
    }
  },
  mounted() {
    this.loadMedicines();
  },
  methods: {
    async loadMedicines() {
      this.loading = true;
      try {
        const response = await api.get('/api/medicines');
        this.medicines = response.data;
      } catch (error) {
        ElMessage.error('获取药品列表失败: ' + error.message);
      } finally {
        this.loading = false;
      }
    },

    showAddDialog() {
      this.dialogMode = 'add';
      this.resetForm();
      this.dialogVisible = true;
    },

    editMedicine(medicine) {
      this.dialogMode = 'edit';
      this.currentMedicine = medicine;
      
      // 解析适用物种
      const applicableSpecies = [];
      if (medicine.applicableCat) applicableSpecies.push('cat');
      if (medicine.applicableDog) applicableSpecies.push('dog');
      if (medicine.applicableBird) applicableSpecies.push('bird');
      if (medicine.applicableRabbit) applicableSpecies.push('rabbit');
      if (medicine.applicableHamster) applicableSpecies.push('hamster');
      
      this.medicineForm = {
        id: medicine.id,
        name: medicine.name,
        category: medicine.category || '',
        description: medicine.description || '',
        usageInstructions: medicine.usageInstructions || '',
        contraindications: medicine.contraindications || '',
        price: medicine.price,
        stockQuantity: medicine.stockQuantity,
        unit: medicine.unit,
        manufacturer: medicine.manufacturer || '',
        expiryDate: medicine.expiryDate || '',
        applicableSpecies: applicableSpecies,
        status: medicine.status || 1
      };
      this.dialogVisible = true;
    },

    viewDetail(medicine) {
      this.currentMedicine = medicine;
      this.detailDialogVisible = true;
    },

    async submitForm() {
      try {
        await this.$refs.medicineFormRef.validate();
        this.submitting = true;

        // 将适用物种数组转换为字段
        const formData = {
          ...this.medicineForm,
          applicableCat: this.medicineForm.applicableSpecies.includes('cat') ? 1 : 0,
          applicableDog: this.medicineForm.applicableSpecies.includes('dog') ? 1 : 0,
          applicableBird: this.medicineForm.applicableSpecies.includes('bird') ? 1 : 0,
          applicableRabbit: this.medicineForm.applicableSpecies.includes('rabbit') ? 1 : 0,
          applicableHamster: this.medicineForm.applicableSpecies.includes('hamster') ? 1 : 0
        };
        delete formData.applicableSpecies;

        const url = this.dialogMode === 'add' ? '/api/medicines' : `/api/medicines/${this.medicineForm.id}`;
        const method = this.dialogMode === 'add' ? 'post' : 'put';

        const response = await api[method](url, formData);
        
        if (response.data.success || response.status === 200) {
          ElMessage.success(this.dialogMode === 'add' ? '添加成功' : '修改成功');
          this.dialogVisible = false;
          this.loadMedicines();
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

    async deleteMedicine(medicine) {
      try {
        await ElMessageBox.confirm(
          `确定要删除药品 ${medicine.name} 吗？`,
          '警告',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        );

        const response = await api.delete(`/api/medicines/${medicine.id}`);
        if (response.data.success) {
          ElMessage.success('删除成功');
          this.loadMedicines();
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败: ' + error.message);
        }
      }
    },

    showStockInDialog(medicine) {
      this.stockInForm = {
        medicineId: medicine.id,
        medicineName: medicine.name,
        currentStock: medicine.stockQuantity,
        quantity: 1,
        unit: medicine.unit
      };
      this.stockInDialogVisible = true;
    },

    async processStockIn() {
      this.stockInSubmitting = true;
      try {
        const response = await api.put(`/api/medicines/${this.stockInForm.medicineId}/stock-in`, {
          quantity: this.stockInForm.quantity
        });

        if (response.data.success) {
          ElMessage.success('补货成功');
          this.stockInDialogVisible = false;
          this.loadMedicines();
        }
      } catch (error) {
        ElMessage.error('补货失败: ' + error.message);
      } finally {
        this.stockInSubmitting = false;
      }
    },

    resetForm() {
      this.medicineForm = {
        name: '',
        category: '',
        description: '',
        usageInstructions: '',
        contraindications: '',
        price: 0,
        stockQuantity: 0,
        unit: '盒',
        manufacturer: '',
        expiryDate: '',
        applicableSpecies: ['cat', 'dog'],
        status: 1
      };
      if (this.$refs.medicineFormRef) {
        this.$refs.medicineFormRef.resetFields();
      }
    },

    handleCategoryChange() {
      // 分类改变时自动过滤
    },

    handleSpeciesChange() {
      // 物种改变时自动过滤
    },

    formatCategory(category) {
      if (!category) return '-';
      const categoryMap = {
        '抗寄生虫_体内': '抗寄生虫（体内）',
        '抗寄生虫_体外': '抗寄生虫（体外）',
        '抗寄生虫_广谱': '抗寄生虫（广谱）',
        '皮肤与耳道用药': '皮肤与耳道用药',
        '抗感染_抗生素': '抗感染（抗生素）',
        '消化道用药': '消化道用药',
        '镇痛与麻醉': '镇痛与麻醉',
        '心血管用药': '心血管用药',
        '呼吸系统用药': '呼吸系统用药',
        '眼科用药': '眼科用药',
        '解毒与支持治疗': '解毒与支持治疗',
        '营养与代谢': '营养与代谢'
      };
      return categoryMap[category] || category;
    },

    getStockStatusType(quantity) {
      if (quantity < 10) {
        return 'danger';
      } else if (quantity < 50) {
        return 'warning';
      } else {
        return 'success';
      }
    },

    formatDate(date) {
      if (!date) return '-';
      return new Date(date).toLocaleDateString('zh-CN');
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

.stat-card {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-icon i {
  font-size: 28px;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #999;
}
</style>
