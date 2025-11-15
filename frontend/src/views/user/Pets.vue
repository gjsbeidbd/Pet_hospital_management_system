<template>
  <div class="pets-page">
    <el-card class="pets-card">
      <template #header>
        <div class="card-header">
          <span>我的宠物</span>
          <el-button type="primary" @click="showAddForm">添加宠物</el-button>
        </div>
      </template>
      
      <!-- 宠物列表 -->
      <el-table :data="pets" style="width: 100%" v-loading="loading">
        <el-table-column label="头像" align="center">
          <template #default="scope">
            <div v-if="scope.row.avatar" class="table-avatar-container">
              <img :src="scope.row.avatar" class="table-avatar">
            </div>
            <div v-else class="table-avatar-placeholder">
              <i class="el-icon-picture-outline"></i>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="宠物名称" align="center"></el-table-column>
        <el-table-column prop="species" label="种类" align="center"></el-table-column>
        <el-table-column prop="breed" label="品种" align="center"></el-table-column>
        <el-table-column prop="age" label="年龄" align="center"></el-table-column>
        <el-table-column prop="gender" label="性别" align="center">
          <template #default="scope">
            <span>{{ scope.row.gender === 'MALE' ? '雄性' : scope.row.gender === 'FEMALE' ? '雌性' : '未知' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="weight" label="体重(kg)" align="center"></el-table-column>
        <el-table-column label="操作" align="center">
          <template #default="scope">
            <div class="operation-buttons">
              <el-button size="small" @click="showEditForm(scope.row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deletePet(scope.row.id)">删除</el-button>
              <el-button size="small" @click="viewDetails(scope.row)">详情</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 无宠物提示 -->
      <el-empty description="暂无宠物信息" v-if="pets.length === 0 && !loading"></el-empty>
    </el-card>
    </div>
    
    <!-- 添加/编辑宠物对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="petForm" :rules="petRules" ref="petFormRef" label-width="100px">
        <el-form-item label="宠物名称" prop="name">
          <el-input v-model="petForm.name"></el-input>
        </el-form-item>
        
        <el-form-item label="种类" prop="species">
          <el-select v-model="petForm.species" placeholder="请选择种类" @change="onSpeciesChange">
            <el-option
              v-for="item in speciesOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="品种" prop="breed">
          <el-select v-model="petForm.breed" placeholder="请选择品种" :disabled="!petForm.species">
            <el-option
              v-for="item in breedOptions"
              :key="item"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="petForm.age" :min="0" :max="100"></el-input-number>
        </el-form-item>
        
        <el-form-item label="性别" prop="gender">
          <el-select v-model="petForm.gender" placeholder="请选择性别">
            <el-option label="雄性" value="MALE"></el-option>
            <el-option label="雌性" value="FEMALE"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="体重(kg)" prop="weight">
          <el-input-number v-model="petForm.weight" :min="0" :precision="2"></el-input-number>
        </el-form-item>
        
        <el-form-item label="宠物图片">
          <div class="avatar-upload-container">
            <div 
              class="avatar-preview" 
              v-if="petForm.avatarPreview">
              <img :src="petForm.avatarPreview" class="avatar-image">
            </div>
            <div class="avatar-uploader-icon-button" @click="triggerFileInput">
              <i class="el-icon-plus"></i>
              <input 
                ref="fileInput" 
                type="file" 
                accept="image/*" 
                style="display: none;"
                @change="handleFileSelect">
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="疫苗情况" prop="vaccinationStatus">
          <el-input type="textarea" v-model="petForm.vaccinationStatus"></el-input>
        </el-form-item>
        
        <el-form-item label="病史" prop="medicalHistory">
          <el-input type="textarea" v-model="petForm.medicalHistory"></el-input>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="savePet">保存</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 宠物详情对话框 -->
    <el-dialog title="宠物详情" v-model="detailDialogVisible" width="600px">
      <el-form label-width="120px" :model="currentPet" disabled>
        <el-row>
          <el-col :span="12">
            <el-form-item label="宠物名称:">
              <span>{{ currentPet.name }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="种类:">
              <span>{{ currentPet.species }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="12">
            <el-form-item label="品种:">
              <span>{{ currentPet.breed }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄:">
              <span>{{ currentPet.age }} 岁</span>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="12">
            <el-form-item label="性别:">
              <span>{{ currentPet.gender === 'MALE' ? '雄性' : currentPet.gender === 'FEMALE' ? '雌性' : '未知' }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体重:">
              <span>{{ currentPet.weight }} kg</span>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="24">
            <el-form-item label="宠物头像:">
              <div v-if="currentPet.avatarPreview || currentPet.avatar" class="avatar-detail-container">
                <img :src="currentPet.avatarPreview || currentPet.avatar" class="avatar-detail">
              </div>
              <span v-else>暂无图片</span>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="疫苗情况:">
          <span>{{ currentPet.vaccinationStatus }}</span>
        </el-form-item>
        
        <el-form-item label="病史:">
          <span>{{ currentPet.medicalHistory }}</span>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
</template>

<script>
import api from '@/services/api';

export default {
  name: 'UserPets',
  data() {
    return {
      loading: true,
      pets: [],
      dialogVisible: false,
      detailDialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      currentPet: {},
      petForm: {
        id: null,
        name: '',
        species: '',
        breed: '',
        age: null,
        gender: '',
        weight: null,
        avatar: '',
        avatarPreview: '',
        vaccinationStatus: '',
        medicalHistory: ''
      },
      petRules: {
        name: [
          { required: true, message: '请输入宠物名称', trigger: 'blur' }
        ],
        species: [
          { required: true, message: '请选择宠物种类', trigger: 'change' }
        ],
        breed: [
          { required: true, message: '请选择宠物品种', trigger: 'change' }
        ]
      },
      // 种类选项
      speciesOptions: [
        { label: '猫', value: '猫' },
        { label: '狗', value: '狗' },
        { label: '鸟', value: '鸟' },
        { label: '兔子', value: '兔子' },
        { label: '仓鼠', value: '仓鼠' }
      ],
      // 品种选项
      breedMap: {
        '猫': ['狸花猫', '英国短毛猫', '美国短毛猫', '暹罗猫', '布偶猫', '波斯猫', '缅因猫', '斯芬克斯无毛猫', '苏格兰折耳猫', '阿比西尼亚猫'],
        '狗': ['金毛寻回犬', '拉布拉多寻回犬', '哈士奇', '泰迪（贵宾犬）', '博美犬', '柯基犬', '柴犬', '萨摩耶犬', '德国牧羊犬', '比熊犬'],
        '兔子': ['荷兰兔', '安哥拉兔', '中国白兔', '新西兰兔', '垂耳兔'],
        '仓鼠': ['金丝熊', '加卡利亚仓鼠（三线仓鼠）', '坎贝尔仓鼠（一线仓鼠）', '罗伯罗夫斯基仓鼠（老公公仓鼠、老婆婆仓鼠）'],
        '龙猫': ['长尾龙猫（绒毛丝鼠）', '短尾龙猫（秘鲁龙猫）'],
        '鸟': ['虎皮鹦鹉', '玄凤鹦鹉', '牡丹鹦鹉', '鸽子', '文鸟', '画眉鸟'],
        '鱼': ['金鱼（草种金鱼、文种金鱼、龙种金鱼、蛋种金鱼）', '孔雀鱼', '斗鱼', '神仙鱼', '锦鲤'],
        '龟': ['巴西红耳龟', '草龟', '中华花龟', '黄缘闭壳龟', '苏卡达陆龟'],
        '豚鼠': ['英国短毛豚鼠', '阿比西尼亚豚鼠', '秘鲁豚鼠', '无毛豚鼠'],
        '刺猬': ['非洲迷你刺猬', '大耳猬']
      }
    };
  },
  computed: {
    // 根据选择的种类动态返回品种选项
    breedOptions() {
      if (this.petForm.species) {
        return this.breedMap[this.petForm.species] || [];
      }
      return [];
    }
  },
  mounted() {
    this.loadPets();
  },
  methods: {
    // 种类改变时清空品种选择
    onSpeciesChange() {
      this.petForm.breed = '';
    },
    
    // 触发文件选择
    triggerFileInput() {
      this.$refs.fileInput.click();
    },
    
    // 处理文件选择
    handleFileSelect(event) {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          this.petForm.avatarPreview = e.target.result;
          // 将图片的Base64数据保存到avatar字段中
          this.petForm.avatar = e.target.result;
        };
        reader.readAsDataURL(file);
      }
    },
    
    // 加载宠物列表
    async loadPets() {
      this.loading = true;
      try {
        const userId = localStorage.getItem('userId');
        if (userId) {
          const response = await api.get(`/api/users/${userId}/pets`);
          if (response.data.success) {
            this.pets = response.data.data;
          }
        }
      } catch (error) {
        console.error('加载宠物信息失败:', error);
        this.$message.error('加载宠物信息失败: ' + (error.response?.data || error.message));
      } finally {
        this.loading = false;
      }
    },
    
    // 显示添加表单
    showAddForm() {
      this.dialogTitle = '添加宠物';
      this.isEdit = false;
      this.petForm = {
        id: null,
        name: '',
        species: '',
        breed: '',
        age: null,
        gender: '',
        weight: null,
        avatar: '',
        avatarPreview: '',
        vaccinationStatus: '',
        medicalHistory: ''
      };
      this.dialogVisible = true;
    },
    
    // 显示编辑表单
    showEditForm(pet) {
      this.dialogTitle = '编辑宠物';
      this.isEdit = true;
      this.petForm = { ...pet };
      this.dialogVisible = true;
    },
    
    // 查看详情
    viewDetails(pet) {
      this.currentPet = { ...pet };
      this.detailDialogVisible = true;
    },
    
    // 保存宠物信息
    savePet() {
      this.$refs.petFormRef.validate(async (valid) => {
        if (valid) {
          try {
            const userId = localStorage.getItem('userId');
            if (this.isEdit) {
              // 更新宠物信息
              // 创建一个不包含avatarPreview字段的对象
              const petData = { ...this.petForm };
              delete petData.avatarPreview;
              
              await api.put(`/api/users/pets/${this.petForm.id}`, petData);
              this.$message.success('宠物信息更新成功');
            } else {
              // 添加宠物信息
              // 首先获取客户ID
              const customerResponse = await api.get(`/api/users/${userId}/customer`);
              const customerId = customerResponse.data.success ? customerResponse.data.data.id : customerResponse.data.id;
              
              // 创建一个不包含avatarPreview字段的对象
              const petData = { ...this.petForm, customerId: customerId };
              delete petData.avatarPreview;
              
              await api.post(`/api/users/pets`, petData);
              this.$message.success('宠物信息添加成功');
            }
            
            this.dialogVisible = false;
            await this.loadPets(); // 重新加载宠物列表
          } catch (error) {
            console.error('保存宠物信息失败:', error);
            this.$message.error('保存宠物信息失败: ' + (error.response?.data || error.message));
          }
        }
      });
    },
    
    // 删除宠物
    deletePet(petId) {
      this.$confirm('确定要删除这只宠物吗？此操作不可恢复！', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await api.delete(`/api/users/pets/${petId}`);
          this.$message.success('宠物信息删除成功');
          await this.loadPets(); // 重新加载宠物列表
        } catch (error) {
          console.error('删除宠物信息失败:', error);
          this.$message.error('删除宠物信息失败: ' + (error.response?.data || error.message));
        }
      }).catch(() => {
        // 用户取消操作
      });
    }
  }
};
</script>

<style scoped>
.pets-page {
  width: 100%;
  height: 100%;
}

.pets-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.pets-card :deep(.el-card__body) {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.pets-card :deep(.el-table) {
  width: 100%;
}

.pets-card :deep(.el-empty) {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-size: 15px;
  font-weight: 700;
  color: #2d3748;
}

.dialog-footer {
  text-align: right;
}

.avatar-upload-container {
  display: flex;
  align-items: center;
  gap: 15px;
}

.avatar-preview {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-uploader-icon-button {
  width: 44px;
  height: 44px;
  border: 2px dashed #cbd5e0;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  background: #f7fafc;
}

.avatar-uploader-icon-button:hover {
  border-color: #3182ce;
  background: #edf2f7;
}

.avatar-uploader-icon-button i {
  font-size: 22px;
  color: #a0aec0;
}

.avatar-uploader-icon-button:hover i {
  color: #3182ce;
}

.avatar-detail-container {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  margin: 10px 0;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.avatar-detail {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.table-avatar-container {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  margin: auto;
  border: 1px solid #e2e8f0;
}

.table-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.table-avatar-placeholder {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: auto;
  background: #f7fafc;
  border: 1px solid #e2e8f0;
  color: #a0aec0;
}

.table-avatar-placeholder i {
  font-size: 24px;
}

.operation-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: nowrap;
}
</style>