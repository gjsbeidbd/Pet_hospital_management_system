<template>
  <div class="queue-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>取号排队</span>
        </div>
      </template>

      <!-- 就诊类型选择 -->
      <div class="visit-type-selector">
        <el-radio-group v-model="visitType" size="large" @change="onVisitTypeChange">
          <el-radio-button label="WALKIN">现场就诊</el-radio-button>
          <el-radio-button label="APPOINTMENT">预约就诊</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 预约就诊：选择预约记录 -->
      <div v-if="visitType === 'APPOINTMENT'" class="appointment-section">
        <el-alert
          title="提示"
          type="info"
          description="请从今日预约列表中选择需要取号的预约记录"
          :closable="false"
          style="margin-bottom: 20px;">
        </el-alert>

        <el-table 
          :data="todayAppointments" 
          style="width: 100%" 
          v-loading="loadingAppointments"
          @row-click="selectAppointment"
          highlight-current-row>
          <el-table-column type="index" label="序号" width="60"></el-table-column>
          <el-table-column prop="appointmentTime" label="预约时间" width="180">
            <template #default="scope">
              {{ formatDateTime(scope.row.appointmentTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="customerName" label="客户姓名" width="120"></el-table-column>
          <el-table-column prop="petName" label="宠物名称" width="120"></el-table-column>
          <el-table-column prop="petSpecies" label="宠物种类" width="100"></el-table-column>
          <el-table-column prop="doctorName" label="医生" width="120"></el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getAppointmentStatusType(scope.row.status)">
                {{ getAppointmentStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="scope">
              <el-button 
                size="small" 
                type="primary"
                @click.stop="takeNumberForAppointment(scope.row)">
                取号
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-empty v-if="todayAppointments.length === 0 && !loadingAppointments" 
          description="今日暂无预约记录" 
          :image-size="150">
        </el-empty>
      </div>

      <!-- 现场就诊：手动填写 -->
      <div v-else class="walkin-section">
        <!-- 第一步：客户信息 -->
        <el-card class="step-card" v-if="currentStep === 1">
          <template #header>
            <span>第一步：客户信息确认</span>
          </template>

          <el-radio-group v-model="customerType" @change="onCustomerTypeChange" size="large" style="margin-bottom: 20px;">
            <el-radio-button label="existing">已有账号</el-radio-button>
            <el-radio-button label="new">新客户注册</el-radio-button>
          </el-radio-group>

          <!-- 已有账号：搜索客户 -->
          <div v-if="customerType === 'existing'">
            <el-form label-width="120px" style="max-width: 600px;">
              <el-form-item label="搜索客户">
                <el-input 
                  v-model="searchKeyword" 
                  placeholder="输入手机号或姓名搜索"
                  @input="searchCustomers"
                  clearable>
                  <template #prefix>
                    <el-icon><i class="el-icon-search"></i></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-form>

            <el-table 
              :data="filteredCustomers" 
              style="width: 100%; margin-top: 10px;"
              @row-click="selectCustomer"
              highlight-current-row
              v-loading="loadingCustomers">
              <el-table-column type="index" label="序号" width="60"></el-table-column>
              <el-table-column prop="realName" label="姓名" width="120"></el-table-column>
              <el-table-column prop="phone" label="手机号" width="150"></el-table-column>
              <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="scope">
                  <el-button size="small" type="primary" @click="selectCustomer(scope.row)">选择</el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-empty v-if="filteredCustomers.length === 0 && !loadingCustomers" 
              description="未找到客户，请检查搜索条件或选择新客户注册" 
              :image-size="100">
            </el-empty>
          </div>

          <!-- 新客户：注册表单 -->
          <div v-else>
            <el-form :model="newCustomerForm" :rules="customerRules" ref="customerFormRef" label-width="120px" style="max-width: 600px;">
              <el-form-item label="姓名" prop="realName">
                <el-input v-model="newCustomerForm.realName" placeholder="请输入客户姓名"></el-input>
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="newCustomerForm.phone" placeholder="请输入手机号" maxlength="11" @blur="syncUsername"></el-input>
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="newCustomerForm.email" placeholder="请输入邮箱（可选）"></el-input>
              </el-form-item>
              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="newCustomerForm.gender">
                  <el-radio label="MALE">男</el-radio>
                  <el-radio label="FEMALE">女</el-radio>
                  <el-radio label="OTHER">其他</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="地址">
                <el-input v-model="newCustomerForm.address" placeholder="请输入地址（可选）"></el-input>
              </el-form-item>
              <el-alert
                title="初始密码为：123456，请提醒用户登录后修改密码"
                type="info"
                :closable="false"
                style="margin-bottom: 15px;">
              </el-alert>
              <el-form-item>
                <el-button type="primary" @click="registerCustomer" :loading="registering">注册并继续</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>

        <!-- 第二步：选择宠物 -->
        <el-card class="step-card" v-if="currentStep === 2">
          <template #header>
            <div class="step-header">
              <span>第二步：选择宠物</span>
              <el-button size="small" @click="currentStep = 1">上一步</el-button>
            </div>
          </template>

          <el-alert
            :title="`客户：${selectedCustomer?.realName} (${selectedCustomer?.phone})`"
            type="success"
            :closable="false"
            style="margin-bottom: 20px;">
          </el-alert>

          <el-button type="primary" @click="showAddPetDialog" style="margin-bottom: 10px;">
            <i class="el-icon-plus"></i> 添加新宠物
          </el-button>

          <el-table 
            :data="customerPets" 
            style="width: 100%"
            @row-click="selectPet"
            highlight-current-row
            v-loading="loadingPets">
            <el-table-column type="index" label="序号" width="60"></el-table-column>
            <el-table-column prop="name" label="宠物名称" width="120"></el-table-column>
            <el-table-column prop="species" label="种类" width="100"></el-table-column>
            <el-table-column prop="breed" label="品种" width="120"></el-table-column>
            <el-table-column prop="age" label="年龄" width="80">
              <template #default="scope">
                {{ scope.row.age }}岁
              </template>
            </el-table-column>
            <el-table-column prop="gender" label="性别" width="80">
              <template #default="scope">
                {{ scope.row.gender === 'MALE' ? '雄性' : '雌性' }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button size="small" type="primary" @click="selectPet(scope.row)">选择</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-empty v-if="customerPets.length === 0 && !loadingPets" 
            description="该客户暂无宠物，请点击上方按钮添加" 
            :image-size="100">
          </el-empty>
        </el-card>

        <!-- 第三步：选择医生并取号 -->
        <el-card class="step-card" v-if="currentStep === 3">
          <template #header>
            <div class="step-header">
              <span>第三步：选择医生并取号</span>
              <el-button size="small" @click="currentStep = 2">上一步</el-button>
            </div>
          </template>

          <el-descriptions :column="2" border style="margin-bottom: 20px;">
            <el-descriptions-item label="客户">{{ selectedCustomer?.realName }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ selectedCustomer?.phone }}</el-descriptions-item>
            <el-descriptions-item label="宠物">{{ selectedPet?.name }}</el-descriptions-item>
            <el-descriptions-item label="种类">{{ selectedPet?.species }}</el-descriptions-item>
          </el-descriptions>

          <el-form label-width="120px" style="max-width: 600px;">
            <el-form-item label="选择医生" required>
              <el-select 
                v-model="queueForm.doctorId" 
                placeholder="请选择医生"
                filterable
                style="width: 100%;">
                <el-option 
                  v-for="doctor in doctors" 
                  :key="doctor.id" 
                  :label="`${doctor.realName} - ${doctor.department} (${doctor.title})`" 
                  :value="doctor.id">
                </el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="备注">
              <el-input 
                v-model="queueForm.notes" 
                type="textarea" 
                :rows="3" 
                placeholder="请填写就诊原因或备注信息（可选）">
              </el-input>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="takeNumberForWalkin" size="large" :loading="submitting">
                <i class="el-icon-tickets"></i> 立即取号
              </el-button>
              <el-button @click="resetAll" size="large">重新开始</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </el-card>

    <!-- 取号成功对话框 -->
    <el-dialog title="取号成功" v-model="successDialogVisible" width="400px">
      <div class="success-info">
        <el-result icon="success" title="取号成功">
          <template #sub-title>
            <div class="queue-info">
              <p><strong>排队号码：</strong><span class="queue-number">{{ currentQueueNumber }}</span></p>
              <p><strong>客户姓名：</strong>{{ currentCustomerName }}</p>
              <p><strong>宠物名称：</strong>{{ currentPetName }}</p>
              <p><strong>医生：</strong>{{ currentDoctorName }}</p>
              <p class="notice">请在候诊区等待叫号</p>
            </div>
          </template>
        </el-result>
      </div>
      <template #footer>
        <el-button type="primary" @click="successDialogVisible = false">确定</el-button>
        <el-button @click="printTicket">打印小票</el-button>
      </template>
    </el-dialog>

    <!-- 添加宠物对话框 -->
    <el-dialog title="添加宠物" v-model="addPetDialogVisible" width="500px">
      <el-form :model="newPetForm" :rules="petRules" ref="petFormRef" label-width="100px">
        <el-form-item label="宠物名称" prop="name">
          <el-input v-model="newPetForm.name" placeholder="请输入宠物名称"></el-input>
        </el-form-item>
        <el-form-item label="种类" prop="species">
          <el-select v-model="newPetForm.species" placeholder="请选择种类" style="width: 100%;">
            <el-option label="犬" value="犬"></el-option>
            <el-option label="猫" value="猫"></el-option>
            <el-option label="兔" value="兔"></el-option>
            <el-option label="仓鼠" value="仓鼠"></el-option>
            <el-option label="鸟" value="鸟"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="品种">
          <el-input v-model="newPetForm.breed" placeholder="请输入品种（可选）"></el-input>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="newPetForm.age" :min="0" :max="30" placeholder="年龄"></el-input-number>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="newPetForm.gender">
            <el-radio label="MALE">雄性</el-radio>
            <el-radio label="FEMALE">雌性</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="颜色">
          <el-input v-model="newPetForm.color" placeholder="请输入颜色（可选）"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addPetDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addPet" :loading="addingPet">添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus';
import api from '@/services/api';

export default {
  name: 'QueueManagement',
  data() {
    return {
      visitType: 'WALKIN', // WALKIN 或 APPOINTMENT
      
      // 现场就诊流程控制
      currentStep: 1, // 1:客户信息 2:选择宠物 3:选择医生取号
      customerType: 'existing', // existing:已有账号 new:新客户
      
      // 搜索和加载状态
      searchKeyword: '',
      loadingCustomers: false,
      loadingPets: false,
      loadingAppointments: false,
      registering: false,
      submitting: false,
      addingPet: false,
      
      // 数据列表
      customers: [],
      filteredCustomers: [],
      customerPets: [],
      doctors: [],
      todayAppointments: [],
      
      // 选中的数据
      selectedCustomer: null,
      selectedPet: null,
      
      // 表单数据
      queueForm: {
        customerId: null,
        petId: null,
        doctorId: null,
        appointmentId: null,
        visitType: 'WALKIN',
        notes: '',
        queueDate: new Date().toISOString().split('T')[0],
        queueTime: new Date().toTimeString().split(' ')[0]
      },
      
      // 新用户注册表单
      newCustomerForm: {
        username: '', // 用户名（手机号）
        password: '123456', // 默认密码
        realName: '',
        phone: '',
        email: '',
        gender: 'MALE',
        address: ''
      },
      
      // 新宠物表单
      newPetForm: {
        name: '',
        species: '',
        breed: '',
        age: 1,
        gender: 'MALE',
        color: ''
      },
      
      // 验证规则
      customerRules: {
        realName: [
          { required: true, message: '请输入客户姓名', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在2到50个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        gender: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ]
      },
      
      petRules: {
        name: [
          { required: true, message: '请输入宠物名称', trigger: 'blur' }
        ],
        species: [
          { required: true, message: '请选择宠物种类', trigger: 'change' }
        ],
        age: [
          { required: true, message: '请输入年龄', trigger: 'blur' }
        ],
        gender: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ]
      },
      
      // 对话框控制
      successDialogVisible: false,
      addPetDialogVisible: false,
      
      // 取号成功信息
      currentQueueNumber: '',
      currentCustomerName: '',
      currentPetName: '',
      currentDoctorName: ''
    };
  },
  mounted() {
    this.loadCustomers();
    this.loadDoctors();
    this.loadTodayAppointments();
    
    // 初始化时加载所有客户到filteredCustomers以便搜索
    this.filteredCustomers = this.customers;
  },
  methods: {
    onVisitTypeChange() {
      this.resetAll();
      if (this.visitType === 'APPOINTMENT') {
        this.loadTodayAppointments();
      }
    },
    
    onCustomerTypeChange() {
      this.searchKeyword = '';
      this.filteredCustomers = [];
      this.selectedCustomer = null;
    },
    
    // 搜索客户
    searchCustomers() {
      if (!this.searchKeyword.trim()) {
        this.filteredCustomers = [];
        return;
      }
      
      const keyword = this.searchKeyword.toLowerCase();
      this.filteredCustomers = this.customers.filter(customer => {
        return customer.realName.toLowerCase().includes(keyword) || 
               customer.phone.includes(keyword);
      });
    },
    
    // 选择客户
    async selectCustomer(customer) {
      this.selectedCustomer = customer;
      this.queueForm.customerId = customer.id;
      
      // 加载客户的宠物列表
      await this.loadCustomerPets(customer.id);
      
      // 进入下一步
      this.currentStep = 2;
    },
    
    // 同步用户名（使用手机号作为用户名）
    syncUsername() {
      if (this.newCustomerForm.phone) {
        this.newCustomerForm.username = this.newCustomerForm.phone;
      }
    },
    
    // 注册新客户（注册为普通用户）
    async registerCustomer() {
      this.$refs.customerFormRef.validate(async (valid) => {
        if (!valid) return;
        
        this.registering = true;
        try {
          // 使用手机号作为用户名
          const userData = {
            username: this.newCustomerForm.phone,
            password: this.newCustomerForm.password,
            realName: this.newCustomerForm.realName,
            phone: this.newCustomerForm.phone,
            email: this.newCustomerForm.email,
            gender: this.newCustomerForm.gender,
            address: this.newCustomerForm.address,
            status: 1
          };
          
          const response = await api.post('/api/users', userData);
          if (response.data.success) {
            ElMessage.success('客户注册成功，初始密码为：123456');
            this.selectedCustomer = response.data.data;
            this.queueForm.customerId = response.data.data.id;
            
            // 刷新客户列表
            await this.loadCustomers();
            
            // 进入下一步
            this.currentStep = 2;
          }
        } catch (error) {
          ElMessage.error('注册失败: ' + error.message);
        } finally {
          this.registering = false;
        }
      });
    },
    
    // 加载客户的宠物列表
    async loadCustomerPets(customerId) {
      this.loadingPets = true;
      try {
        const response = await api.get(`/api/users/${customerId}/pets`);
        if (response.data.success) {
          this.customerPets = response.data.data;
        }
      } catch (error) {
        ElMessage.error('获取宠物列表失败: ' + error.message);
      } finally {
        this.loadingPets = false;
      }
    },
    
    // 选择宠物
    selectPet(pet) {
      this.selectedPet = pet;
      this.queueForm.petId = pet.id;
      
      // 进入下一步
      this.currentStep = 3;
    },
    
    // 显示添加宠物对话框
    showAddPetDialog() {
      this.newPetForm = {
        name: '',
        species: '',
        breed: '',
        age: 1,
        gender: 'MALE',
        color: ''
      };
      this.addPetDialogVisible = true;
    },
    
    // 添加宠物
    async addPet() {
      this.$refs.petFormRef.validate(async (valid) => {
        if (!valid) return;
        
        this.addingPet = true;
        try {
          const petData = {
            ...this.newPetForm,
            customerId: this.selectedCustomer.id
          };
          
          const response = await api.post('/api/pets', petData);
          if (response.data.success) {
            ElMessage.success('宠物添加成功');
            this.addPetDialogVisible = false;
            
            // 刷新宠物列表
            await this.loadCustomerPets(this.selectedCustomer.id);
          }
        } catch (error) {
          ElMessage.error('添加宠物失败: ' + error.message);
        } finally {
          this.addingPet = false;
        }
      });
    },

    async loadTodayAppointments() {
      this.loadingAppointments = true;
      try {
        const response = await api.get('/api/appointments');
        let appointments = response.data.success ? response.data.data : response.data;
        
        // 筛选今日的预约记录
        const today = new Date().toISOString().split('T')[0];
        appointments = appointments.filter(app => {
          const appDate = new Date(app.appointmentTime).toISOString().split('T')[0];
          return appDate === today;
        });
        
        // 获取详细信息
        for (let appointment of appointments) {
          try {
            // 获取客户信息（从用户表）
            const customerResp = await api.get(`/api/users/${appointment.customerId}`);
            if (customerResp.data.success) {
              appointment.customerName = customerResp.data.data.realName;
            }
            
            // 获取宠物信息
            const petResp = await api.get(`/api/users/pet/${appointment.petId}`);
            if (petResp.data) {
              appointment.petName = petResp.data.name;
              appointment.petSpecies = petResp.data.species;
            }
            
            // 获取医生信息
            const doctorResp = await api.get(`/api/doctors/${appointment.doctorId}`);
            if (doctorResp.data) {
              appointment.doctorName = doctorResp.data.realName;
            }
          } catch (error) {
            console.error('获取预约详情失败:', error);
          }
        }
        
        this.todayAppointments = appointments;
      } catch (error) {
        ElMessage.error('获取预约列表失败: ' + error.message);
      } finally {
        this.loadingAppointments = false;
      }
    },

    async loadCustomers() {
      this.loadingCustomers = true;
      try {
        const response = await api.get('/api/users');
        if (response.data.success) {
          this.customers = response.data.data;
          this.filteredCustomers = this.customers; // 初始化过滤列表
        }
      } catch (error) {
        ElMessage.error('获取客户列表失败: ' + error.message);
      } finally {
        this.loadingCustomers = false;
      }
    },

    async loadDoctors() {
      try {
        const response = await api.get('/api/doctors');
        this.doctors = response.data;
      } catch (error) {
        ElMessage.error('获取医生列表失败: ' + error.message);
      }
    },

    async onCustomerChange(customerId) {
      this.queueForm.petId = null;
      this.customerPets = [];
      
      if (!customerId) return;
      
      try {
        const response = await api.get(`/api/users/${customerId}/pets`);
        if (response.data.success) {
          this.customerPets = response.data.data;
        }
      } catch (error) {
        ElMessage.error('获取宠物列表失败: ' + error.message);
      }
    },

    selectAppointment(row) {
      // 用于高亮显示选中的行
      console.log('选中预约:', row);
    },

    async takeNumberForAppointment(appointment) {
      try {
        const queueData = {
          customerId: appointment.customerId,
          petId: appointment.petId,
          doctorId: appointment.doctorId,
          appointmentId: appointment.id,
          visitType: 'APPOINTMENT',
          notes: '预约就诊',
          queueDate: new Date().toISOString().split('T')[0],
          queueTime: new Date().toTimeString().split(' ')[0]
        };

        const response = await api.post('/api/consultation-queue', queueData);
        if (response.data.success) {
          const queue = response.data.data;
          
          // 更新预约状态为CONFIRMED（待就诊）
          await api.put(`/api/appointments/${appointment.id}/confirm`);
          
          // 显示成功信息
          this.currentQueueNumber = queue.queueNumber;
          this.currentCustomerName = appointment.customerName;
          this.currentPetName = appointment.petName;
          this.currentDoctorName = appointment.doctorName;
          
          this.successDialogVisible = true;
          
          // 刷新预约列表
          this.loadTodayAppointments();
        }
      } catch (error) {
        ElMessage.error('取号失败: ' + error.message);
      }
    },

    async takeNumberForWalkin() {
      if (!this.queueForm.doctorId) {
        ElMessage.warning('请选择医生');
        return;
      }

      this.submitting = true;
      try {
        const queueData = {
          customerId: this.selectedCustomer.id,
          petId: this.selectedPet.id,
          doctorId: this.queueForm.doctorId,
          visitType: 'WALKIN',
          notes: this.queueForm.notes,
          queueDate: new Date().toISOString().split('T')[0],
          queueTime: new Date().toTimeString().split(' ')[0]
        };

        const response = await api.post('/api/consultation-queue', queueData);
        if (response.data.success) {
          const queue = response.data.data;
          
          // 保存信息用于显示
          this.currentQueueNumber = queue.queueNumber;
          this.currentCustomerName = this.selectedCustomer.realName;
          this.currentPetName = this.selectedPet.name;
          
          const doctor = this.doctors.find(d => d.id === this.queueForm.doctorId);
          this.currentDoctorName = doctor?.realName || '';
          
          this.successDialogVisible = true;
          this.resetAll();
        }
      } catch (error) {
        ElMessage.error('取号失败: ' + error.message);
      } finally {
        this.submitting = false;
      }
    },

    resetAll() {
      this.currentStep = 1;
      this.customerType = 'existing';
      this.searchKeyword = '';
      this.selectedCustomer = null;
      this.selectedPet = null;
      this.filteredCustomers = [];
      this.customerPets = [];
      
      this.queueForm = {
        customerId: null,
        petId: null,
        doctorId: null,
        appointmentId: null,
        visitType: 'WALKIN',
        notes: '',
        queueDate: new Date().toISOString().split('T')[0],
        queueTime: new Date().toTimeString().split(' ')[0]
      };
      
      this.newCustomerForm = {
        username: '',
        password: '123456',
        realName: '',
        phone: '',
        email: '',
        gender: 'MALE',
        address: ''
      };
    },

    resetForm() {
      this.queueForm = {
        customerId: null,
        petId: null,
        doctorId: null,
        appointmentId: null,
        visitType: 'WALKIN',
        notes: '',
        queueDate: new Date().toISOString().split('T')[0],
        queueTime: new Date().toTimeString().split(' ')[0]
      };
      this.customerPets = [];
    },

    printTicket() {
      window.print();
    },

    formatDateTime(date) {
      if (!date) return '';
      return new Date(date).toLocaleString('zh-CN');
    },

    getAppointmentStatusType(status) {
      const map = {
        'PENDING': 'warning',
        'CONFIRMED': 'primary',
        'IN_PROGRESS': 'success',
        'COMPLETED': 'info',
        'CANCELLED': 'danger'
      };
      return map[status] || 'info';
    },

    getAppointmentStatusText(status) {
      const map = {
        'PENDING': '待取号',
        'CONFIRMED': '待就诊',
        'IN_PROGRESS': '就诊中',
        'COMPLETED': '已就诊',
        'CANCELLED': '已取消'
      };
      return map[status] || status;
    }
  }
};
</script>

<style scoped>
.queue-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.visit-type-selector {
  margin-bottom: 30px;
  text-align: center;
}

.appointment-section {
  margin-top: 20px;
}

.walkin-section {
  margin-top: 20px;
}

.step-card {
  margin-bottom: 20px;
}

.step-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.success-info {
  text-align: center;
}

.queue-info {
  margin-top: 20px;
  text-align: left;
}

.queue-info p {
  margin: 10px 0;
  font-size: 16px;
}

.queue-number {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
}

.notice {
  margin-top: 20px;
  color: #E6A23C;
  font-size: 14px;
  font-weight: bold;
}

:deep(.el-table__row) {
  cursor: pointer;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}
</style>