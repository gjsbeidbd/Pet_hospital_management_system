<template>
  <div class="pet-management">
    <el-card class="pet-card">
      <template #header>
        <div class="card-header">
          <span>宠物管理</span>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索宠物名称"
              style="width: 200px; margin-right: 10px;"
              clearable
            />
            <el-button type="primary" @click="showAddPetDialog">添加宠物</el-button>
          </div>
        </div>
      </template>

      <!-- 宠物列表 -->
      <el-table :data="filteredPets" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="宠物名称" width="120"></el-table-column>
        <el-table-column prop="species" label="种类" width="100"></el-table-column>
        <el-table-column prop="breed" label="品种" width="120"></el-table-column>
        <el-table-column prop="age" label="年龄" width="80"></el-table-column>
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.gender === 'MALE' ? 'primary' : 'danger'">
              {{ scope.row.gender === 'MALE' ? '雄性' : '雌性' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="主人姓名" width="120"></el-table-column>
        <el-table-column prop="ownerUsername" label="主人用户名" width="150"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="showEditPetDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deletePet(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑宠物对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="petForm" :rules="petRules" ref="petFormRef" label-width="100px">
        <el-form-item label="宠物名称" prop="name">
          <el-input v-model="petForm.name"></el-input>
        </el-form-item>
        <el-form-item label="种类" prop="species">
          <el-select v-model="petForm.species" placeholder="请选择种类" style="width: 100%;">
            <el-option label="猫" value="猫"></el-option>
            <el-option label="狗" value="狗"></el-option>
            <el-option label="鸟" value="鸟"></el-option>
            <el-option label="兔子" value="兔子"></el-option>
            <el-option label="仓鼠" value="仓鼠"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="品种" prop="breed">
          <el-input v-model="petForm.breed"></el-input>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="petForm.age" :min="0" :max="30"></el-input-number>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="petForm.gender" placeholder="请选择性别">
            <el-option label="雄性" value="MALE"></el-option>
            <el-option label="雌性" value="FEMALE"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="主人姓名" prop="ownerName">
          <el-input v-model="petForm.ownerName" disabled></el-input>
        </el-form-item>
        <el-form-item label="主人用户名" prop="ownerUsername">
          <el-input v-model="petForm.ownerUsername" disabled></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="savePet">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus';
import api from '@/services/api';

export default {
  name: 'PetManagement',
  data() {
    return {
      loading: false,
      pets: [],
      searchKeyword: '',
      dialogVisible: false,
      isEdit: false,
      petForm: {
        id: null,
        name: '',
        species: '',
        breed: '',
        age: 0,
        gender: '',
        ownerName: '',
        ownerUsername: ''
      },
      petRules: {
        name: [
          { required: true, message: '请输入宠物名称', trigger: 'blur' }
        ],
        species: [
          { required: true, message: '请选择宠物种类', trigger: 'change' }
        ],
        breed: [
          { required: true, message: '请输入宠物品种', trigger: 'blur' }
        ],
        gender: [
          { required: true, message: '请选择宠物性别', trigger: 'change' }
        ]
      }
    };
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑宠物' : '添加宠物';
    },
    filteredPets() {
      if (!this.searchKeyword) {
        return this.pets;
      }
      const keyword = this.searchKeyword.toLowerCase();
      return this.pets.filter(pet => 
        pet.name && pet.name.toLowerCase().includes(keyword)
      );
    }
  },
  mounted() {
    this.loadPets();
  },
  methods: {
    async loadPets() {
      this.loading = true;
      try {
        // 从新的接口获取用户和宠物的关联数据
        const response = await api.get('/api/users/pets');
        this.pets = response.data;
      } catch (error) {
        ElMessage.error('获取宠物列表失败: ' + error.message);
      } finally {
        this.loading = false;
      }
    },

    showAddPetDialog() {
      this.isEdit = false;
      this.petForm = {
        id: null,
        name: '',
        species: '',
        breed: '',
        age: 0,
        gender: '',
        ownerName: '',
        ownerUsername: ''
      };
      this.dialogVisible = true;
    },

    showEditPetDialog(pet) {
      this.isEdit = true;
      this.petForm = { ...pet };
      this.dialogVisible = true;
    },

    async savePet() {
      this.$refs.petFormRef.validate(async (valid) => {
        if (valid) {
          try {
            if (this.isEdit) {
              // 编辑宠物信息
              await api.put(`/api/users/pets/${this.petForm.id}`, this.petForm);
              ElMessage.success('宠物信息更新成功');
            } else {
              // 添加宠物（需要先创建宠物，再建立用户和宠物的关联）
              ElMessage.warning('添加宠物功能需要完善用户关联逻辑');
            }
            
            this.dialogVisible = false;
            this.loadPets();
          } catch (error) {
            ElMessage.error('保存宠物信息失败: ' + error.message);
          }
        }
      });
    },

    deletePet(id) {
      ElMessageBox.confirm('确定要删除这个宠物吗？此操作不可恢复！', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await api.delete(`/api/users/pets/${id}`);
          ElMessage.success('宠物删除成功');
          this.loadPets();
        } catch (error) {
          ElMessage.error('删除宠物失败: ' + error.message);
        }
      }).catch(() => {
        // 用户取消删除
      });
    }
  }
};
</script>

<style scoped>
.pet-management {
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