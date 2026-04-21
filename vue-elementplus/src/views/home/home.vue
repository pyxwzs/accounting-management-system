<template>
  <div class="dashboard-container">
    <!-- 顶部标题栏 -->
    <header class="dashboard-header">
      <div class="header-left">
        <h1 class="dashboard-title">管理控制台</h1>
        <div class="welcome-message">欢迎回来，{{userStore.user.username}}</div>
      </div>
      <div class="header-right">
        <div class="date-time-display">
          <div class="current-date">{{ formattedDate }}</div>
          <div class="current-time">{{ formattedTime }}</div>
        </div>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <main class="dashboard-main">
      <!-- 用户信息卡片 -->
      <div class="user-info-card card">
        <div class="card-header">
          <h2 class="card-title">个人信息</h2>
          
        </div>
        <div class="card-content">
          <div class="user-avatar">
            <div class="avatar-placeholder">
              {{ userInitials }}
            </div>
          </div>
          <div class="user-details">
            <div class="user-detail-item">
              <span class="detail-label">姓名</span>
              <span class="detail-value">{{ userStore.user.real_name }}</span>
            </div>
            <div class="user-detail-item">
              <span class="detail-label">角色</span>
              <span class="detail-value">{{ userStore.user.role_name }}</span>
            </div>
            <div class="user-detail-item">
              <span class="detail-label">手机号</span>
              <span class="detail-value">{{ userStore.user.phone }}</span>
            </div>
            <div class="user-detail-item">
              <span class="detail-label">邮箱</span>
              <span class="detail-value">{{ userStore.user.email }}</span>
            </div>
          </div>
        </div>
      </div>

   
      <!-- 快速操作区域 -->
      <div class="quick-actions card">
        <div class="card-header">
          <h2 class="card-title">快速操作</h2>
        </div>
        <div class="card-content">
          <div class="actions-grid">
            <button class="action-btn" @click="handleAction('addUser')">
              <div class="action-icon">
                <svg viewBox="0 0 24 24" width="20" height="20">
                  <path fill="currentColor" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
                </svg>
              </div>
              <span>添加用户</span>
            </button>
            <button class="action-btn" @click="handleAction('report')">
              <div class="action-icon">
                <svg viewBox="0 0 24 24" width="20" height="20">
                  <path fill="currentColor" d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z"/>
                </svg>
              </div>
              <span>添加公告</span>
            </button>
            <button class="action-btn" @click="handleAction('settings')">
              <div class="action-icon">
                <svg viewBox="0 0 24 24" width="20" height="20">
                  <path fill="currentColor" d="M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z"/>
                </svg>
              </div>
              <span>角色设置</span>
            </button>
            <button class="action-btn" @click="handleAction('help')">
              <div class="action-icon">
                <svg viewBox="0 0 24 24" width="20" height="20">
                  <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 17h-2v-2h2v2zm2.07-7.75l-.9.92C13.45 12.9 13 13.5 13 15h-2v-.5c0-1.1.45-2.1 1.17-2.83l1.24-1.26c.37-.36.59-.86.59-1.41 0-1.1-.9-2-2-2s-2 .9-2 2H8c0-2.21 1.79-4 4-4s4 1.79 4 4c0 .88-.36 1.68-.93 2.25z"/>
                </svg>
              </div>
              <span>轮播图设置</span>
            </button>
          </div>
        </div>
      </div>

    </main>

  </div>
</template>

<script setup>
import {ref, watch, reactive, nextTick, onMounted, onUnmounted, computed, onBeforeUnmount} from "vue";
import * as echarts from 'echarts'
import utils from "@/utils/utils.js";
import instances from "@/utils/request";
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/userStore';
import * as siteConfig  from "@/config/index.js";
const router = useRouter();
const userStore = useUserStore()
const chartRef = ref(null)
let chartInstance = null
const chartTimeRange = ref('week')

// 用户信息
const userInitials = computed(() => {
  if (userStore.user.real_name) {
    const names = userStore.user.real_name.split(' ')
    return names.length > 1 
      ? `${names[0].charAt(0)}${names[1].charAt(0)}` 
      : names[0].charAt(0)
  }
  return 'A'
})



// 时间相关
const currentDate = ref(new Date())
const formattedDate = computed(() => {
  return currentDate.value.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

const formattedTime = computed(() => {
  return currentDate.value.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
})

const loginTime = computed(() => {
  return new Date().toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
})

const lastLoginTime = computed(() => {
  const date = new Date()
  date.setDate(date.getDate() - 1)
  return date.toLocaleDateString('zh-CN', {
    month: 'short',
    day: 'numeric'
  }) + ' ' + date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
})

// 统计数据
const stats = reactive({
  totalUsers: 1248,
  userGrowth: 12.5,
  activeTasks: 18,
  taskCompletion: 78,
  newMessages: 23,
  unreadMessages: 5,
  storageUsage: 68
})

// 系统信息
const systemVersion = computed(() => {
  return siteConfig.version || '2.1.0'
})

const currentYear = computed(() => {
  return new Date().getFullYear()
})

// 更新时间的定时器
let timeInterval = null


const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

// 快速操作处理
const handleAction = (action) => {
  console.log('执行操作:', action)
  // 这里可以添加具体的操作逻辑
  switch(action) {
    case 'addUser':
      router.push('/system/user')
      break
    case 'report':
      router.push('/system/notice')
      break
    case 'settings':
      router.push('/system/role')
      break
    case 'help':
      router.push('/system/slideshow')
      break
  }
}

onMounted(() => {
  // 更新时间
  timeInterval = setInterval(() => {
    currentDate.value = new Date()
  }, 1000)
  
  // 初始化图表
  nextTick(() => {
    initChart()
  })
  
  // 模拟获取统计数据
  setTimeout(() => {
    // 这里可以替换为实际的API调用
    console.log('获取统计数据...')
  }, 1000)
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
  
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
  
  window.removeEventListener('resize', handleResize)
})

watch(chartTimeRange, () => {
  // 这里可以根据选择的时间范围更新图表数据
  console.log('切换时间范围:', chartTimeRange.value)
  if (chartInstance) {
    // 更新图表数据逻辑
  }
})
</script>

<style scoped>
.dashboard-container {
  min-height: 100vh;
  background-color: #f8fafc;
  display: flex;
  flex-direction: column;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 头部样式 */
.dashboard-header {
  background-color: white;
  padding: 1.5rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border-bottom: 1px solid #e5e7eb;
}

.header-left {
  display: flex;
  flex-direction: column;
}

.dashboard-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #111827;
  margin: 0 0 0.25rem 0;
}

.welcome-message {
  font-size: 0.95rem;
  color: #6b7280;
}

.date-time-display {
  text-align: right;
}

.current-date {
  font-size: 1rem;
  font-weight: 600;
  color: #111827;
  margin-bottom: 0.25rem;
}

.current-time {
  font-size: 0.9rem;
  color: #6b7280;
  font-family: 'Courier New', monospace;
}

/* 卡片通用样式 */
.card {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e7eb;
  transition: box-shadow 0.2s ease, transform 0.2s ease;
}

.card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid #f3f4f6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.card-actions {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.card-content {
  padding: 1.5rem;
}

/* 主要内容区域 */
.dashboard-main {
  flex: 1;
  padding: 1.5rem 2rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

/* 用户信息卡片 */
.user-info-card .card-content {
  display: flex;
  align-items: flex-start;
  gap: 2rem;
}

.user-avatar {
  flex-shrink: 0;
}

.avatar-placeholder {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4f46e5, #8b5cf6);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.75rem;
  font-weight: 600;
  box-shadow: 0 4px 6px rgba(79, 70, 229, 0.2);
}

.user-details {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.user-detail-item {
  display: flex;
  flex-direction: column;
}

.detail-label {
  font-size: 0.875rem;
  color: #6b7280;
  margin-bottom: 0.25rem;
}

.detail-value {
  font-size: 1rem;
  font-weight: 500;
  color: #111827;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1.5rem;
}

.stat-card {
  padding: 1.5rem;
}

.stat-card-header {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 1rem;
}

.stat-icon.primary {
  background-color: rgba(79, 70, 229, 0.1);
  color: #4f46e5;
}

.stat-icon.secondary {
  background-color: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.stat-icon.accent {
  background-color: rgba(139, 92, 246, 0.1);
  color: #8b5cf6;
}

.stat-icon.warning {
  background-color: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 1.75rem;
  font-weight: 700;
  color: #111827;
  line-height: 1;
}

.stat-label {
  font-size: 0.875rem;
  color: #6b7280;
  margin-top: 0.25rem;
}

.stat-trend {
  font-size: 0.875rem;
  color: #6b7280;
  display: flex;
  align-items: center;
}

.stat-trend.positive {
  color: #10b981;
}

.stat-trend.negative {
  color: #ef4444;
}

.stat-trend span {
  font-weight: 600;
  margin-right: 0.25rem;
}

.progress-bar {
  height: 6px;
  background-color: #e5e7eb;
  border-radius: 3px;
  overflow: hidden;
  margin-top: 0.75rem;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #f59e0b, #fbbf24);
  border-radius: 3px;
  transition: width 0.5s ease;
}

/* 快速操作 */
.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 1.5rem 1rem;
  background-color: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  color: #374151;
  transition: all 0.2s ease;
  cursor: pointer;
}

.action-btn:hover {
  background-color: white;
  border-color: #d1d5db;
  color: #111827;
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background-color: rgba(79, 70, 229, 0.1);
  color: #4f46e5;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 0.75rem;
}

.action-btn span {
  font-weight: 500;
  font-size: 0.95rem;
}

/* 图表区域 */
.chart-container .card-content {
  padding: 1rem;
}

.chart {
  height: 300px;
  width: 100%;
}

.time-select {
  padding: 0.375rem 0.75rem;
  border-radius: 6px;
  border: 1px solid #d1d5db;
  background-color: white;
  color: #374151;
  font-size: 0.875rem;
}

.btn-icon {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f9fafb;
  border: 1px solid #e5e7eb;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-icon:hover {
  background-color: white;
  border-color: #d1d5db;
  color: #374151;
}

/* 底部样式 */
.dashboard-footer {
  background-color: white;
  padding: 1rem 2rem;
  border-top: 1px solid #e5e7eb;
  margin-top: auto;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.system-info {
  display: flex;
  gap: 1.5rem;
  color: #6b7280;
  font-size: 0.875rem;
}

.footer-links {
  display: flex;
  gap: 1.5rem;
}

.footer-link {
  color: #6b7280;
  text-decoration: none;
  font-size: 0.875rem;
  transition: color 0.2s ease;
}

.footer-link:hover {
  color: #4f46e5;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .user-details {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }
  
  .date-time-display {
    text-align: left;
  }
  
  .footer-content {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }
  
  .user-info-card .card-content {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .dashboard-main {
    padding: 1rem;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .system-info {
    flex-direction: column;
    gap: 0.5rem;
  }
}
</style>