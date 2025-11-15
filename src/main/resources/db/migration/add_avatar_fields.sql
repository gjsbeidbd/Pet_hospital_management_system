-- 为用户表添加头像字段（如果不存在）
ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar TEXT;

-- 为前台表添加头像字段
ALTER TABLE receptionists ADD COLUMN IF NOT EXISTS avatar TEXT;

-- 为医生表添加头像字段
ALTER TABLE doctors ADD COLUMN IF NOT EXISTS avatar TEXT;

-- 为院长表添加头像字段
ALTER TABLE directors ADD COLUMN IF NOT EXISTS avatar TEXT;
