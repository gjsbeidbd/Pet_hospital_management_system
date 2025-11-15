-- 创建数据库
CREATE DATABASE IF NOT EXISTS pet_hospital DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE pet_hospital;

-- 删除现有表（如果有）
DROP TABLE IF EXISTS billing_items;
DROP TABLE IF EXISTS billing;
DROP TABLE IF EXISTS prescriptions;
DROP TABLE IF EXISTS medicines;
DROP TABLE IF EXISTS medical_records;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS pets;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS directors;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS receptionists;
DROP TABLE IF EXISTS users;

-- 创建普通用户表
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(100) NOT NULL UNIQUE COMMENT '用户名（手机号或邮箱）',
  password VARCHAR(255) NOT NULL COMMENT '密码',
  real_name VARCHAR(50) COMMENT '真实姓名',
  phone VARCHAR(20) COMMENT '联系电话',
  email VARCHAR(100) COMMENT '邮箱',
  avatar VARCHAR(500) COMMENT '头像URL路径',
  gender ENUM('MALE', 'FEMALE', 'OTHER') COMMENT '性别',
  birthday DATE COMMENT '出生日期',
  address VARCHAR(255) COMMENT '地址',
  emergency_contact VARCHAR(50) COMMENT '紧急联系人',
  emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_username (username),
  INDEX idx_status (status)
) COMMENT '普通用户表';

-- 创建前台用户表
CREATE TABLE receptionists (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '前台ID',
  username VARCHAR(100) NOT NULL UNIQUE COMMENT '用户名（手机号或邮箱）',
  password VARCHAR(255) NOT NULL COMMENT '密码',
  real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
  employee_id VARCHAR(20) UNIQUE COMMENT '工号',
  phone VARCHAR(20) COMMENT '联系电话',
  email VARCHAR(100) COMMENT '邮箱',
  avatar VARCHAR(500) COMMENT '头像URL路径',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_username (username),
  INDEX idx_employee_id (employee_id),
  INDEX idx_status (status)
) COMMENT '前台用户表';

-- 创建医生表
CREATE TABLE doctors (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '医生ID',
  username VARCHAR(100) NOT NULL UNIQUE COMMENT '用户名（手机号或邮箱）',
  password VARCHAR(255) NOT NULL COMMENT '密码',
  real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
  employee_id VARCHAR(20) UNIQUE COMMENT '工号',
  department VARCHAR(50) COMMENT '科室',
  title VARCHAR(50) COMMENT '职称',
  specialty TEXT COMMENT '专长',
  phone VARCHAR(20) COMMENT '联系电话',
  email VARCHAR(100) COMMENT '邮箱',
  avatar VARCHAR(500) COMMENT '头像URL路径',
  description TEXT COMMENT '简介',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-在职，0-离职',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_username (username),
  INDEX idx_employee_id (employee_id),
  INDEX idx_real_name (real_name),
  INDEX idx_status (status)
) COMMENT '医生表';

-- 创建院长表
CREATE TABLE directors (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '院长ID',
  username VARCHAR(100) NOT NULL UNIQUE COMMENT '用户名（手机号或邮箱）',
  password VARCHAR(255) NOT NULL COMMENT '密码',
  real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
  employee_id VARCHAR(20) UNIQUE COMMENT '工号',
  department VARCHAR(50) COMMENT '管理部门',
  phone VARCHAR(20) COMMENT '联系电话',
  email VARCHAR(100) COMMENT '邮箱',
  avatar VARCHAR(500) COMMENT '头像URL路径',
  description TEXT COMMENT '简介',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_username (username),
  INDEX idx_employee_id (employee_id),
  INDEX idx_status (status)
) COMMENT '院长表';

-- 创建客户表（宠物主人）
-- 注意：客户信息可以从普通用户表同步，也可以是独立的客户
CREATE TABLE customers (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '客户ID',
  user_id BIGINT COMMENT '关联的用户ID',
  real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
  phone VARCHAR(20) COMMENT '联系电话',
  email VARCHAR(100) COMMENT '邮箱',
  address VARCHAR(255) COMMENT '地址',
  wechat VARCHAR(100) COMMENT '微信号',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
  INDEX idx_phone (phone),
  INDEX idx_real_name (real_name)
) COMMENT '客户表';

-- 创建宠物表
CREATE TABLE pets (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '宠物ID',
  customer_id BIGINT NOT NULL COMMENT '客户ID',
  name VARCHAR(50) NOT NULL COMMENT '宠物名称',
  species VARCHAR(50) COMMENT '种类',
  breed VARCHAR(50) COMMENT '品种',
  age INT COMMENT '年龄',
  gender ENUM('MALE', 'FEMALE') COMMENT '性别',
  weight DECIMAL(5,2) COMMENT '体重(kg)',
  avatar MEDIUMTEXT COMMENT '宠物头像',
  vaccination_status TEXT COMMENT '疫苗接种情况',
  medical_history TEXT COMMENT '过往病史',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
  INDEX idx_customer_id (customer_id),
  INDEX idx_name (name)
) COMMENT '宠物表';

-- 创建预约表
CREATE TABLE appointments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
  pet_id BIGINT NOT NULL COMMENT '宠物ID',
  customer_id BIGINT NOT NULL COMMENT '客户ID',
  doctor_id BIGINT NOT NULL COMMENT '医生ID',
  appointment_time DATETIME NOT NULL COMMENT '预约时间',
  status ENUM('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING' COMMENT '预约状态',
  notes TEXT COMMENT '备注',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
  FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
  FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
  INDEX idx_appointment_time (appointment_time),
  INDEX idx_status (status),
  INDEX idx_doctor_id (doctor_id)
) COMMENT '预约表';

-- 创建病例表
CREATE TABLE medical_records (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '病例ID',
  pet_id BIGINT NOT NULL COMMENT '宠物ID',
  customer_id BIGINT NOT NULL COMMENT '客户ID',
  doctor_id BIGINT NOT NULL COMMENT '医生ID',
  appointment_id BIGINT COMMENT '预约ID',
  symptoms TEXT COMMENT '症状',
  diagnosis TEXT COMMENT '诊断',
  treatment TEXT COMMENT '治疗方案',
  notes TEXT COMMENT '备注',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
  FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
  FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
  FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL,
  INDEX idx_pet_id (pet_id),
  INDEX idx_doctor_id (doctor_id),
  INDEX idx_created_at (created_at)
) COMMENT '病例表';

-- 创建就诊队列表
CREATE TABLE consultation_queue (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '队列ID',
  queue_number VARCHAR(20) NOT NULL COMMENT '排队号码',
  pet_id BIGINT NOT NULL COMMENT '宠物ID',
  customer_id BIGINT NOT NULL COMMENT '客户ID',
  doctor_id BIGINT NOT NULL COMMENT '医生ID',
  appointment_id BIGINT COMMENT '预约ID（如果有预约）',
  visit_type ENUM('APPOINTMENT', 'WALKIN') DEFAULT 'WALKIN' COMMENT '就诊类型：预约/现场',
  status ENUM('WAITING', 'CONSULTING', 'COMPLETED', 'CANCELLED') DEFAULT 'WAITING' COMMENT '状态',
  queue_date DATE NOT NULL COMMENT '排队日期',
  queue_time TIME NOT NULL COMMENT '取号时间',
  call_time DATETIME COMMENT '叫号时间',
  complete_time DATETIME COMMENT '完成时间',
  notes TEXT COMMENT '备注',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
  FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
  FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
  FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL,
  INDEX idx_doctor_id (doctor_id),
  INDEX idx_queue_date (queue_date),
  INDEX idx_status (status),
  UNIQUE KEY uk_queue_number_date (queue_number, queue_date)
) COMMENT '就诊队列表';

-- 创建药品表
CREATE TABLE medicines (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '药品ID',
  name VARCHAR(100) NOT NULL COMMENT '药品名称',
  category ENUM(
    '抗寄生虫_体内',
    '抗寄生虫_体外', 
    '抗寄生虫_广谱',
    '皮肤与耳道用药',
    '抗感染_抗生素',
    '消化道用药',
    '镇痛与麻醉',
    '心血管用药',
    '呼吸系统用药',
    '眼科用药',
    '解毒与支持治疗',
    '营养与代谢'
  ) NOT NULL COMMENT '药品分类',
  description TEXT COMMENT '药品描述',
  usage_instructions TEXT COMMENT '用法用量',
  contraindications TEXT COMMENT '禁忌症',
  price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格',
  stock_quantity INT NOT NULL DEFAULT 0 COMMENT '库存数量',
  unit VARCHAR(20) COMMENT '单位',
  manufacturer VARCHAR(100) COMMENT '生产厂家',
  expiry_date DATE COMMENT '过期日期',
  -- 适用物种标记（0-不适用 1-适用）
  applicable_cat TINYINT NOT NULL DEFAULT 1 COMMENT '适用于猫',
  applicable_dog TINYINT NOT NULL DEFAULT 1 COMMENT '适用于狗',
  applicable_bird TINYINT NOT NULL DEFAULT 1 COMMENT '适用于鸟',
  applicable_rabbit TINYINT NOT NULL DEFAULT 1 COMMENT '适用于兔子',
  applicable_hamster TINYINT NOT NULL DEFAULT 1 COMMENT '适用于仓鼠',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_name (name),
  INDEX idx_category (category),
  INDEX idx_status (status)
) COMMENT '药品表';

-- 创建处方表
CREATE TABLE prescriptions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '处方ID',
  medical_record_id BIGINT NOT NULL COMMENT '病例ID',
  medicine_id BIGINT NOT NULL COMMENT '药品ID',
  quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
  dosage VARCHAR(100) COMMENT '用法用量',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (medical_record_id) REFERENCES medical_records(id) ON DELETE CASCADE,
  FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE CASCADE,
  INDEX idx_medical_record_id (medical_record_id)
) COMMENT '处方表';

-- 创建用户宠物关联表
CREATE TABLE user_pets (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  pet_id BIGINT NOT NULL COMMENT '宠物ID',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
  UNIQUE KEY uk_user_pet (user_id, pet_id),
  INDEX idx_user_id (user_id),
  INDEX idx_pet_id (pet_id)
) COMMENT '用户宠物关联表';

-- 创建收费表
CREATE TABLE billing (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收费ID',
  pet_id BIGINT NOT NULL COMMENT '宠物ID',
  customer_id BIGINT NOT NULL COMMENT '客户ID',
  appointment_id BIGINT COMMENT '预约ID',
  total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总金额',
  paid_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '已付金额',
  status ENUM('PENDING', 'PARTIAL', 'PAID') DEFAULT 'PENDING' COMMENT '支付状态',
  payment_method ENUM('CASH', 'CARD', 'ALIPAY', 'WECHAT') COMMENT '支付方式',
  notes TEXT COMMENT '备注',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
  FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
  FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL,
  INDEX idx_customer_id (customer_id),
  INDEX idx_status (status)
) COMMENT '收费表';

-- 创建收费明细表
CREATE TABLE billing_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收费明细ID',
  billing_id BIGINT NOT NULL COMMENT '收费ID',
  item_type ENUM('SERVICE', 'MEDICINE') NOT NULL COMMENT '项目类型',
  item_id BIGINT COMMENT '项目ID（服务ID或药品ID）',
  item_name VARCHAR(100) NOT NULL COMMENT '项目名称',
  unit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '单价',
  quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
  amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '金额',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (billing_id) REFERENCES billing(id) ON DELETE CASCADE,
  INDEX idx_billing_id (billing_id)
) COMMENT '收费明细表';

-- 创建医生排班表
CREATE TABLE doctor_schedules (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '排班ID',
  doctor_id BIGINT NOT NULL COMMENT '医生ID',
  schedule_date DATE NOT NULL COMMENT '排班日期',
  weekday TINYINT NOT NULL COMMENT '星期几（1-7表示周一到周日）',
  shift_type ENUM('MORNING', 'AFTERNOON', 'EVENING', 'NIGHT', 'FULL_DAY') NOT NULL COMMENT '班次类型',
  start_time TIME NOT NULL COMMENT '开始时间',
  end_time TIME NOT NULL COMMENT '结束时间',
  department VARCHAR(50) COMMENT '科室',
  notes TEXT COMMENT '备注',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
  INDEX idx_doctor_id (doctor_id),
  INDEX idx_schedule_date (schedule_date)
) COMMENT '医生排班表';

-- 插入默认用户（密码设置为123456）
INSERT INTO users (username, password, real_name) VALUES 
('user@hospital.com', '123456', '普通用户');

INSERT INTO receptionists (username, password, real_name, employee_id) VALUES 
('reception@hospital.com', '123456', '前台接待员', 'REC001');

INSERT INTO doctors (username, password, real_name, employee_id, department, title, specialty, phone, email, description, status) VALUES 
('doctor@hospital.com', '123456', '李明', 'DOC001', '内科', '主治医师', '犬猫内科常见病诊疗', '13800138001', 'liming@hospital.com', '从业10年，擅长犬猫消化系统疾病治疗', 1),
('doctor.wang@hospital.com', '123456', '王芳', 'DOC002', '外科', '副主任医师', '宠物骨科、软组织外科', '13800138002', 'wangfang@hospital.com', '从业15年，擅长骨折修复和软组织手术', 1),
('doctor.zhang@hospital.com', '123456', '张伟', 'DOC003', '眼科', '主治医师', '眼科疾病诊疗', '13800138003', 'zhangwei@hospital.com', '从业8年，擅长白内障、青光眼等眼科疾病', 1),
('doctor.liu@hospital.com', '123456', '刘娜', 'DOC004', '皮肤科', '主治医师', '皮肤病、过敏症', '13800138004', 'liuna@hospital.com', '从业7年，擅长各类皮肤病和过敏性疾病治疗', 1),
('doctor.chen@hospital.com', '123456', '陈强', 'DOC005', '牙科', '副主任医师', '口腔疾病、牙科手术', '13800138005', 'chenqiang@hospital.com', '从业12年，擅长牙科疾病治疗和口腔手术', 1),
('doctor.zhao@hospital.com', '123456', '赵敏', 'DOC006', '心脏科', '主任医师', '心血管疾病', '13800138006', 'zhaomin@hospital.com', '从业20年，擅长心脏病诊断和治疗', 1),
('doctor.sun@hospital.com', '123456', '孙杰', 'DOC007', '急诊科', '主治医师', '急诊急救', '13800138007', 'sunjie@hospital.com', '从业9年，擅长各类急诊病例处理', 1),
('doctor.zhou@hospital.com', '123456', '周莉', 'DOC008', '内科', '医师', '犬猫常见病', '13800138008', 'zhouli@hospital.com', '从业5年，主要负责常规内科诊疗', 1);

INSERT INTO directors (username, password, real_name, employee_id, department) VALUES 
('directors@hospital.com', '123456', '院长', 'ADM001', '医院管理');

-- 插入默认药品信息（按12个分类，标注适用物种）
INSERT INTO medicines (name, category, description, usage_instructions, contraindications, price, stock_quantity, unit, manufacturer, expiry_date, applicable_cat, applicable_dog, applicable_bird, applicable_rabbit, applicable_hamster) VALUES 
-- 抗寄生虫（体内）
('吡喹酮片', '抗寄生虫_体内', '广谱驱虫药，用于治疗绦虫、吸虫等寄生虫感染', '口服，按体重5-10mg/kg，每日1次，连用3天', '肝肾功能不全者慎用', 45.00, 80, '盒', '拜耳动物保健', '2026-12-31', 1, 1, 0, 1, 1),
('阿苯达唑片', '抗寄生虫_体内', '驱除体内线虫、绦虫等寄生虫', '口服，按体重10mg/kg，每日1次，连用3-5天', '怀孕早期禁用', 38.00, 100, '盒', '硕腾宠物药业', '2026-10-15', 1, 1, 0, 1, 0),
('米尔倍肟片', '抗寄生虫_体内', '预防和治疗心丝虫、蛔虫等寄生虫', '口服，按体重0.5mg/kg，每月1次', '柯利犬及其杂交犬慎用', 120.00, 60, '盒', '诺华动物保健', '2027-03-20', 1, 1, 0, 0, 0),
-- 抗寄生虫（体外）
('非泼罗尼滴剂', '抗寄生虫_体外', '杀灭跳蚤、蜱虫等体外寄生虫', '滴于颈背部皮肤，每月1次', '幼龄宠物（8周以下）禁用', 85.00, 90, '支', '梅里亚宠物药业', '2026-08-30', 1, 1, 0, 0, 0),
('塞拉菌素滴剂', '抗寄生虫_体外', '驱杀跳蚤、螨虫、耳螨等', '外用滴剂，按体重使用，每月1次', '避免接触眼睛', 95.00, 75, '支', '辉瑞动物保健', '2026-11-25', 1, 1, 1, 1, 1),
('伊维菌素喷剂', '抗寄生虫_体外', '杀灭体外寄生虫，适用于小型宠物', '喷洒于全身皮毛，避开口鼻眼', '柯利犬禁用，鸟类需专用制剂', 58.00, 110, '瓶', '福来恩宠物药业', '2026-09-18', 0, 0, 1, 1, 1),
-- 抗寄生虫（广谱）
('莫西菌素复合滴剂', '抗寄生虫_广谱', '内外同驱，预防心丝虫，驱杀跳蚤、蜱虫、蛔虫', '滴于颈背部皮肤，每月1次', '怀孕哺乳期慎用', 135.00, 55, '支', '拜耳动物保健', '2027-01-10', 1, 1, 0, 0, 0),
('大宠爱滴剂', '抗寄生虫_广谱', '内外同驱复合制剂，预防心丝虫和耳螨', '外用滴剂，按体重使用，每月1次', '幼龄宠物（6周以下）禁用', 145.00, 50, '支', '硕腾宠物药业', '2026-12-05', 1, 1, 0, 0, 0),
-- 皮肤与耳道用药
('酮康唑乳膏', '皮肤与耳道用药', '抗真菌药物，治疗皮肤癣、真菌性皮炎', '外用涂抹患处，每日2次，连用2-4周', '避免舔舐，可戴伊丽莎白圈', 32.00, 85, '支', '上海兽药厂', '2026-07-22', 1, 1, 0, 0, 0),
('克霉唑喷剂', '皮肤与耳道用药', '治疗真菌感染、耳道炎', '喷洒患处，每日2-3次', '开放性伤口慎用', 42.00, 95, '瓶', '北京宠物药业', '2026-10-08', 1, 1, 1, 1, 1),
('伊曲康唑胶囊', '皮肤与耳道用药', '深部真菌感染治疗', '口服，按体重5mg/kg，每日1次', '肝功能不全禁用', 88.00, 40, '盒', '杨森制药', '2027-02-14', 1, 1, 0, 0, 0),
('洗耳液', '皮肤与耳道用药', '清洁耳道，预防和辅助治疗耳炎', '滴入耳道，轻揉耳根，每周2-3次', '鼓膜穿孔禁用', 28.00, 120, '瓶', '维克宠物护理', '2026-11-30', 1, 1, 1, 1, 1),
-- 抗感染（抗生素）
('阿莫西林克拉维酸', '抗感染_抗生素', '广谱抗生素，治疗细菌感染', '口服，按体重12.5-25mg/kg，每日2次', '青霉素过敏禁用', 45.00, 110, '盒', '史克肠虫清', '2026-09-25', 1, 1, 0, 0, 0),
('头孢氨苄片', '抗感染_抗生素', '第一代头孢类抗生素，治疗呼吸道、泌尿系统感染', '口服，按体重15-30mg/kg，每日2次', '头孢过敏禁用', 38.00, 100, '盒', '华北制药', '2026-10-12', 1, 1, 0, 1, 0),
('恩诺沙星注射液', '抗感染_抗生素', '喹诺酮类抗生素，治疗严重细菌感染', '皮下或肌肉注射，按体重5mg/kg，每日1次', '幼龄宠物、软骨发育未完成者禁用', 65.00, 70, '盒', '齐鲁动物保健', '2026-08-18', 1, 1, 1, 0, 0),
('多西环素片', '抗感染_抗生素', '四环素类抗生素，治疗支原体、衣原体感染', '口服，按体重5-10mg/kg，每日1次', '妊娠期禁用', 52.00, 80, '盒', '辉瑞制药', '2027-01-05', 1, 1, 1, 0, 0),
-- 消化道用药
('蒙脱石散', '消化道用药', '止泻药，吸附肠道毒素，保护肠黏膜', '口服，按体重0.5-1g/kg，每日2-3次', '肠梗阻禁用', 18.00, 150, '盒', '博福-益普生', '2026-12-20', 1, 1, 1, 1, 1),
('碱式水杨酸铋片', '消化道用药', '止泻、抗菌，治疗胃肠炎', '口服，按体重20mg/kg，每日2次', '肾功能不全慎用', 22.00, 130, '盒', '上海信谊', '2026-11-08', 1, 1, 0, 1, 0),
('益生菌粉', '消化道用药', '调节肠道菌群，改善消化功能', '拌入食物或温水，每日1-2次', '抗生素同时使用需间隔2小时', 35.00, 160, '盒', '拜耳益生菌', '2027-03-15', 1, 1, 1, 1, 1),
('乳果糖口服液', '消化道用药', '缓解便秘，调节肠道pH值', '口服，按体重0.5ml/kg，每日1-2次', '肠梗阻禁用', 42.00, 90, '瓶', '雅培制药', '2026-09-30', 1, 1, 0, 1, 1),
-- 镇痛与麻醉
('丁丙诺啡注射液', '镇痛与麻醉', '强效镇痛药，用于术后镇痛', '皮下或静脉注射，按体重0.01-0.03mg/kg', '呼吸抑制患者禁用', 95.00, 45, '盒', '瑞金医疗', '2026-10-25', 1, 0, 0, 0, 0),
('美洛昔康片', '镇痛与麻醉', '非甾体抗炎镇痛药，缓解疼痛和炎症', '口服，按体重0.1mg/kg，每日1次', '肾功能不全、胃溃疡禁用', 68.00, 75, '盒', '勃林格殷格翰', '2027-01-18', 1, 1, 0, 0, 0),
('卡洛芬片', '镇痛与麻醉', '犬用非甾体抗炎药，术后镇痛', '口服，按体重2-4mg/kg，每日1次', '猫禁用', 72.00, 65, '盒', '辉瑞动物保健', '2026-11-12', 0, 1, 0, 0, 0),
('异氟烷吸入麻醉剂', '镇痛与麻醉', '吸入性全身麻醉药', '吸入麻醉，由专业兽医使用', '严重心肺疾病禁用', 280.00, 20, '瓶', '百特医疗', '2027-02-28', 1, 1, 1, 1, 1),
-- 心血管用药
('贝那普利片', '心血管用药', 'ACE抑制剂，治疗心力衰竭', '口服，按体重0.25-0.5mg/kg，每日1次', '低血压、肾衰竭禁用', 85.00, 50, '盒', '诺华制药', '2027-01-20', 1, 1, 0, 0, 0),
('呋塞米片', '心血管用药', '利尿剂，治疗心力衰竭引起的水肿', '口服或注射，按体重1-4mg/kg', '脱水、电解质紊乱禁用', 32.00, 90, '盒', '赛诺菲', '2026-10-05', 1, 1, 1, 0, 0),
('地高辛片', '心血管用药', '强心药，治疗心房颤动', '口服，按体重0.005-0.01mg/kg，每日1-2次', '心肌梗死急性期禁用', 48.00, 60, '盒', '上海信谊', '2026-12-08', 1, 1, 0, 0, 0),
-- 呼吸系统用药
('氨茶碱片', '呼吸系统用药', '支气管扩张剂，缓解呼吸困难', '口服，按体重5-10mg/kg，每日2次', '心律失常禁用', 28.00, 100, '盒', '天津力生', '2026-11-18', 1, 1, 1, 0, 0),
('沙丁胺醇雾化液', '呼吸系统用药', '选择性β2受体激动剂，缓解支气管痉挛', '雾化吸入，每次0.1-0.2ml', '甲状腺功能亢进慎用', 38.00, 85, '盒', '葛兰素史克', '2026-09-22', 1, 1, 1, 0, 0),
('溴己新片', '呼吸系统用药', '祛痰药，稀释痰液', '口服，按体重1mg/kg，每日2次', '胃溃疡慎用', 22.00, 110, '盒', '上海医药', '2027-02-10', 1, 1, 0, 0, 0),
-- 眼科用药
('妥布霉素滴眼液', '眼科用药', '抗生素眼药水，治疗细菌性结膜炎', '滴眼，每日3-4次', '真菌、病毒性感染无效', 25.00, 120, '瓶', '参天制药', '2026-08-15', 1, 1, 1, 1, 1),
('氯霉素眼膏', '眼科用药', '广谱抗菌眼膏', '涂于眼睑内，每日2-3次', '骨髓抑制者慎用', 18.00, 140, '支', '上海新亚', '2026-10-28', 1, 1, 1, 1, 1),
('阿托品滴眼液', '眼科用药', '散瞳药，用于眼科检查和葡萄膜炎治疗', '滴眼，每日1-2次', '青光眼禁用', 32.00, 70, '瓶', '沈阳兴齐', '2026-11-05', 1, 1, 1, 0, 0),
-- 解毒与支持治疗
('活性炭粉', '解毒与支持治疗', '吸附毒物，用于中毒急救', '口服，按体重1-5g/kg', '腐蚀性毒物中毒禁用', 15.00, 100, '瓶', '北京协和', '2027-06-30', 1, 1, 1, 1, 1),
('乙酰半胱氨酸注射液', '解毒与支持治疗', '对乙酰氨基酚中毒解毒剂，祛痰药', '静脉注射，按体重140mg/kg首次剂量', '支气管哮喘慎用', 125.00, 40, '盒', '赞邦制药', '2026-09-12', 1, 1, 0, 0, 0),
('葡萄糖注射液5%', '解毒与支持治疗', '补充能量和体液，支持治疗', '静脉输注，根据需要调整速度', '高血糖、糖尿病慎用', 8.00, 200, '瓶', '石家庄四药', '2026-12-31', 1, 1, 1, 1, 1),
('补液盐散', '解毒与支持治疗', '口服补液，纠正脱水和电解质紊乱', '溶于温水口服，按需使用', '严重脱水需静脉补液', 12.00, 180, '袋', '华润双鹤', '2027-03-25', 1, 1, 1, 1, 1),
-- 营养与代谢
('维生素C片', '营养与代谢', '补充维生素C，增强免疫力（豚鼠、鸟类必需）', '口服或拌入食物，每日10-20mg', '肾结石患者慎用', 15.00, 200, '瓶', '汤臣倍健', '2027-01-30', 0, 0, 1, 1, 1),
('复合维生素B片', '营养与代谢', '补充B族维生素，促进代谢', '口服，每日1次', '过敏体质慎用', 18.00, 190, '瓶', '善存', '2026-11-22', 1, 1, 1, 1, 1),
('钙片+维生素D3', '营养与代谢', '补充钙质，促进骨骼发育', '口服，按体重使用', '高钙血症禁用', 35.00, 150, '瓶', '迪巧', '2027-02-18', 1, 1, 1, 1, 1),
('羊奶粉（幼宠专用）', '营养与代谢', '替代母乳，幼宠营养补充', '温水冲调，按需喂养', '乳糖不耐受需选择无乳糖配方', 88.00, 80, '罐', '卫仕宠物营养', '2026-10-20', 1, 1, 1, 1, 1);

-- 插入默认医生排班信息（从2025-11-10开始的两周）
INSERT INTO doctor_schedules (doctor_id, schedule_date, weekday, shift_type, start_time, end_time, department, notes) VALUES 
-- 李明医生（内科）排班
(1, '2025-11-10', 1, 'MORNING', '08:00:00', '12:00:00', '内科', ''),
(1, '2025-11-11', 2, 'AFTERNOON', '14:00:00', '18:00:00', '内科', ''),
(1, '2025-11-12', 3, 'FULL_DAY', '08:00:00', '18:00:00', '内科', ''),
(1, '2025-11-14', 5, 'MORNING', '08:00:00', '12:00:00', '内科', ''),
(1, '2025-11-17', 1, 'MORNING', '08:00:00', '12:00:00', '内科', ''),
(1, '2025-11-18', 2, 'AFTERNOON', '14:00:00', '18:00:00', '内科', ''),
(1, '2025-11-20', 4, 'FULL_DAY', '08:00:00', '18:00:00', '内科', ''),
-- 王芳医生（外科）排班
(2, '2025-11-10', 1, 'AFTERNOON', '14:00:00', '18:00:00', '外科', ''),
(2, '2025-11-11', 2, 'MORNING', '08:00:00', '12:00:00', '外科', ''),
(2, '2025-11-13', 4, 'FULL_DAY', '08:00:00', '18:00:00', '外科', '手术日'),
(2, '2025-11-15', 6, 'MORNING', '08:00:00', '12:00:00', '外科', ''),
(2, '2025-11-17', 1, 'AFTERNOON', '14:00:00', '18:00:00', '外科', ''),
(2, '2025-11-19', 3, 'FULL_DAY', '08:00:00', '18:00:00', '外科', '手术日'),
(2, '2025-11-21', 5, 'MORNING', '08:00:00', '12:00:00', '外科', ''),
-- 张伟医生（眼科）排班
(3, '2025-11-10', 1, 'FULL_DAY', '08:00:00', '18:00:00', '眼科', ''),
(3, '2025-11-12', 3, 'MORNING', '08:00:00', '12:00:00', '眼科', ''),
(3, '2025-11-14', 5, 'AFTERNOON', '14:00:00', '18:00:00', '眼科', ''),
(3, '2025-11-16', 0, 'MORNING', '08:00:00', '12:00:00', '眼科', ''),
(3, '2025-11-18', 2, 'FULL_DAY', '08:00:00', '18:00:00', '眼科', ''),
(3, '2025-11-21', 5, 'AFTERNOON', '14:00:00', '18:00:00', '眼科', ''),
-- 刘娜医生（皮肤科）排班
(4, '2025-11-11', 2, 'FULL_DAY', '08:00:00', '18:00:00', '皮肤科', ''),
(4, '2025-11-13', 4, 'MORNING', '08:00:00', '12:00:00', '皮肤科', ''),
(4, '2025-11-15', 6, 'AFTERNOON', '14:00:00', '18:00:00', '皮肤科', ''),
(4, '2025-11-17', 1, 'FULL_DAY', '08:00:00', '18:00:00', '皮肤科', ''),
(4, '2025-11-19', 3, 'MORNING', '08:00:00', '12:00:00', '皮肤科', ''),
(4, '2025-11-22', 6, 'AFTERNOON', '14:00:00', '18:00:00', '皮肤科', ''),
-- 陈强医生（牙科）排班
(5, '2025-11-10', 1, 'MORNING', '08:00:00', '12:00:00', '牙科', ''),
(5, '2025-11-12', 3, 'FULL_DAY', '08:00:00', '18:00:00', '牙科', ''),
(5, '2025-11-14', 5, 'MORNING', '08:00:00', '12:00:00', '牙科', ''),
(5, '2025-11-16', 0, 'AFTERNOON', '14:00:00', '18:00:00', '牙科', ''),
(5, '2025-11-18', 2, 'MORNING', '08:00:00', '12:00:00', '牙科', ''),
(5, '2025-11-20', 4, 'FULL_DAY', '08:00:00', '18:00:00', '牙科', ''),
-- 赵敏医生（心脏科）排班
(6, '2025-11-11', 2, 'MORNING', '08:00:00', '12:00:00', '心脏科', ''),
(6, '2025-11-13', 4, 'AFTERNOON', '14:00:00', '18:00:00', '心脏科', ''),
(6, '2025-11-15', 6, 'FULL_DAY', '08:00:00', '18:00:00', '心脏科', ''),
(6, '2025-11-17', 1, 'MORNING', '08:00:00', '12:00:00', '心脏科', ''),
(6, '2025-11-19', 3, 'AFTERNOON', '14:00:00', '18:00:00', '心脏科', ''),
(6, '2025-11-22', 6, 'FULL_DAY', '08:00:00', '18:00:00', '心脏科', ''),
-- 孙杰医生（急诊科）排班
(7, '2025-11-10', 1, 'EVENING', '18:00:00', '22:00:00', '急诊科', ''),
(7, '2025-11-11', 2, 'EVENING', '18:00:00', '22:00:00', '急诊科', ''),
(7, '2025-11-12', 3, 'NIGHT', '22:00:00', '08:00:00', '急诊科', '夜班'),
(7, '2025-11-14', 5, 'FULL_DAY', '08:00:00', '18:00:00', '急诊科', ''),
(7, '2025-11-16', 0, 'EVENING', '18:00:00', '22:00:00', '急诊科', ''),
(7, '2025-11-18', 2, 'EVENING', '18:00:00', '22:00:00', '急诊科', ''),
(7, '2025-11-19', 3, 'NIGHT', '22:00:00', '08:00:00', '急诊科', '夜班'),
(7, '2025-11-21', 5, 'FULL_DAY', '08:00:00', '18:00:00', '急诊科', ''),
-- 周莉医生（内科）排班
(8, '2025-11-10', 1, 'AFTERNOON', '14:00:00', '18:00:00', '内科', ''),
(8, '2025-11-12', 3, 'MORNING', '08:00:00', '12:00:00', '内科', ''),
(8, '2025-11-13', 4, 'AFTERNOON', '14:00:00', '18:00:00', '内科', ''),
(8, '2025-11-15', 6, 'MORNING', '08:00:00', '12:00:00', '内科', ''),
(8, '2025-11-17', 1, 'AFTERNOON', '14:00:00', '18:00:00', '内科', ''),
(8, '2025-11-19', 3, 'MORNING', '08:00:00', '12:00:00', '内科', ''),
(8, '2025-11-21', 5, 'AFTERNOON', '14:00:00', '18:00:00', '内科', '');

COMMIT;
