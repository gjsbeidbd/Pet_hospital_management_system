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
  avatar VARCHAR(255) COMMENT '头像URL',
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
  avatar VARCHAR(255) COMMENT '头像URL',
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
  avatar VARCHAR(255) COMMENT '头像URL',
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
  avatar VARCHAR(255) COMMENT '头像URL',
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

-- 创建药品表
CREATE TABLE medicines (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '药品ID',
  name VARCHAR(100) NOT NULL COMMENT '药品名称',
  description TEXT COMMENT '药品描述',
  price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格',
  stock_quantity INT NOT NULL DEFAULT 0 COMMENT '库存数量',
  unit VARCHAR(20) COMMENT '单位',
  manufacturer VARCHAR(100) COMMENT '生产厂家',
  expiry_date DATE COMMENT '过期日期',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_name (name),
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

INSERT INTO doctors (username, password, real_name, employee_id, department, title) VALUES 
('doctor@hospital.com', '123456', '主治医生', 'DOC001', '内科', '主治医师');

INSERT INTO directors (username, password, real_name, employee_id, department) VALUES 
('admin@hospital.com', '123456', '院长', 'ADM001', '医院管理');

-- 插入默认客户信息
INSERT INTO customers (user_id, real_name, phone) VALUES 
(NULL, '张三', '13800138001'),
(NULL, '李四', '13800138002');

-- 插入默认宠物信息
INSERT INTO pets (customer_id, name, species, breed, age, gender) VALUES 
(1, '小白', '犬', '泰迪', 3, 'MALE'),
(1, '小黑', '猫', '英短', 2, 'FEMALE'),
(2, '旺财', '犬', '金毛', 4, 'MALE');

-- 插入默认药品信息
INSERT INTO medicines (name, description, price, stock_quantity, unit, manufacturer) VALUES 
('阿莫西林胶囊', '抗生素类药物，用于治疗细菌感染', 25.00, 100, '盒', 'XX制药有限公司'),
('维生素C片', '补充维生素C，增强免疫力', 15.00, 200, '瓶', 'YY药业有限公司'),
('止痛药', '缓解宠物疼痛', 30.00, 50, '盒', 'ZZ制药有限公司');

-- 插入默认医生排班信息
INSERT INTO doctor_schedules (doctor_id, schedule_date, weekday, shift_type, start_time, end_time, department, notes) VALUES 
(1, '2023-06-19', 1, 'MORNING', '08:00:00', '12:00:00', '内科', ''),
(1, '2023-06-20', 2, 'AFTERNOON', '14:00:00', '18:00:00', '内科', ''),
(1, '2023-06-21', 3, 'MORNING', '08:00:00', '12:00:00', '外科', '协助外科手术'),
(1, '2023-06-22', 4, 'AFTERNOON', '14:00:00', '18:00:00', '内科', ''),
(1, '2023-06-23', 5, 'FULL_DAY', '08:00:00', '18:00:00', '急诊科', '轮值急诊');

COMMIT;
