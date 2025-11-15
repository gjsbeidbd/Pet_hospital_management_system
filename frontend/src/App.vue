<script setup></script>

<template>
  <router-view />
</template>

<style scoped></style>

<style>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'PingFang SC', 'Microsoft YaHei', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2d3748;
  font-size: 14px;
}

/* Element Plus 全局主题 */
.el-card {
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.el-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.el-card__header {
  background: #f7fafc;
  border-bottom: 1px solid #e2e8f0;
  padding: 16px 20px;
  font-weight: 600;
  color: #2d3748;
  font-size: 15px;
}

.el-button--primary {
  background: linear-gradient(135deg, #3182ce 0%, #2c5282 100%);
  border: none;
  box-shadow: 0 2px 4px rgba(49, 130, 206, 0.3);
  transition: all 0.3s ease;
  font-weight: 600;
  padding: 9px 18px;
  font-size: 14px;
}

.el-button--primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(49, 130, 206, 0.4);
  background: linear-gradient(135deg, #2c5282 0%, #2a4365 100%);
}

.el-button--primary:active {
  transform: translateY(0);
}

.el-button {
  border-radius: 6px;
  transition: all 0.3s ease;
  font-weight: 500;
  padding: 8px 16px;
  font-size: 14px;
}

.el-button:hover {
  transform: translateY(-1px);
}

.el-button--success {
  background: linear-gradient(135deg, #38a169 0%, #2f855a 100%);
  border: none;
  box-shadow: 0 2px 4px rgba(56, 161, 105, 0.3);
}

.el-button--success:hover {
  background: linear-gradient(135deg, #2f855a 0%, #276749 100%);
}

.el-button--warning {
  background: linear-gradient(135deg, #d69e2e 0%, #b7791f 100%);
  border: none;
  box-shadow: 0 2px 4px rgba(214, 158, 46, 0.3);
}

.el-button--warning:hover {
  background: linear-gradient(135deg, #b7791f 0%, #975a16 100%);
}

.el-button--danger {
  background: linear-gradient(135deg, #e53e3e 0%, #c53030 100%);
  border: none;
  box-shadow: 0 2px 4px rgba(229, 62, 62, 0.3);
}

.el-button--danger:hover {
  background: linear-gradient(135deg, #c53030 0%, #9b2c2c 100%);
}

.el-input__wrapper {
  border-radius: 6px;
  box-shadow: 0 0 0 1px #e2e8f0 inset;
  transition: all 0.3s ease;
  padding: 1px 11px;
}

.el-input__wrapper:hover {
  box-shadow: 0 0 0 1px #cbd5e0 inset;
}

.el-input__wrapper.is-focus {
  box-shadow: 0 0 0 1px #3182ce inset;
}

.el-select .el-input__wrapper {
  border-radius: 6px;
}

.el-table {
  border-radius: 10px;
  overflow: hidden;
  font-size: 14px;
  border: 1px solid #e2e8f0;
}

.el-table th {
  background: #f7fafc;
  color: #4a5568;
  font-weight: 600;
  border-bottom: 1px solid #e2e8f0;
  font-size: 13px;
  padding: 11px 0;
}

.el-table tr:hover > td {
  background: #f7fafc !important;
}

.el-table--striped .el-table__body tr.el-table__row--striped td {
  background: #fafbfc;
}

.el-table td {
  border-bottom: 1px solid #edf2f7;
  font-size: 14px;
  padding: 11px 0;
}

.el-tag {
  border-radius: 5px;
  border: none;
  font-weight: 600;
  font-size: 12px;
  padding: 3px 10px;
  height: auto;
  line-height: 1.5;
}

.el-tag.el-tag--success {
  background: #38a169;
  color: white;
}

.el-tag.el-tag--warning {
  background: #d69e2e;
  color: white;
}

.el-tag.el-tag--danger {
  background: #e53e3e;
  color: white;
}

.el-tag.el-tag--info {
  background: #3182ce;
  color: white;
}

.el-dialog {
  border-radius: 10px;
  overflow: hidden;
  border: none;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.el-dialog__header {
  background: linear-gradient(135deg, #3182ce 0%, #2c5282 100%);
  padding: 18px 22px;
  border-bottom: none;
}

.el-dialog__title {
  color: #ffffff;
  font-weight: 700;
  font-size: 17px;
}

.el-dialog__headerbtn .el-dialog__close {
  color: #ffffff;
  font-size: 18px;
}

.el-dialog__headerbtn:hover .el-dialog__close {
  color: #ffffff;
}

.el-pagination {
  margin-top: 18px;
}

.el-pagination .el-pager li {
  border-radius: 5px;
  transition: all 0.3s ease;
  font-weight: 500;
  font-size: 14px;
  min-width: 30px;
  height: 30px;
  line-height: 30px;
}

.el-pagination .el-pager li:hover {
  background: #f7fafc;
  color: #2d3748;
}

.el-pagination .el-pager li.active {
  background: linear-gradient(135deg, #3182ce 0%, #2c5282 100%);
  color: white;
}

/* 滚动条 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f7fafc;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a0aec0;
}

/* 加载动画 */
.el-loading-mask {
  background-color: rgba(255, 255, 255, 0.85);
}

.el-loading-spinner .circular {
  stroke: #3182ce;
}

/* 消息提示 */
.el-message {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: none;
  border-left-width: 3px;
  border-left-style: solid;
  padding: 12px 16px;
  font-size: 14px;
}

.el-message--success {
  background: #f0fff4;
  border-left-color: #38a169;
  color: #22543d;
}

.el-message--warning {
  background: #fffaf0;
  border-left-color: #d69e2e;
  color: #7c2d12;
}

.el-message--error {
  background: #fff5f5;
  border-left-color: #e53e3e;
  color: #742a2a;
}

.el-message--info {
  background: #ebf8ff;
  border-left-color: #3182ce;
  color: #2c5282;
}
</style>
