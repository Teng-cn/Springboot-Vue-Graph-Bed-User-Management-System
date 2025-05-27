<template>
  <div class="admin-stats-container">
    <el-row :gutter="20">
      <!-- 数据概览卡片 -->
      <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <h3>数据概览</h3>
            </div>
          </template>
          <div class="stats-overview" v-loading="loading">
            <el-row :gutter="20">
              <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
                <div class="stats-item">
                  <div class="stats-icon">
                    <el-icon><UserFilled /></el-icon>
                  </div>
                  <div class="stats-info">
                    <div class="stats-value">{{ stats.totalUsers }}</div>
                    <div class="stats-label">总用户数</div>
                  </div>
                </div>
              </el-col>
              <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
                <div class="stats-item">
                  <div class="stats-icon new-users">
                    <el-icon><User /></el-icon>
                  </div>
                  <div class="stats-info">
                    <div class="stats-value">{{ stats.newUsersToday }}</div>
                    <div class="stats-label">今日新增用户</div>
                  </div>
                </div>
              </el-col>
              <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
                <div class="stats-item">
                  <div class="stats-icon">
                    <el-icon><PictureFilled /></el-icon>
                  </div>
                  <div class="stats-info">
                    <div class="stats-value">{{ stats.totalImages }}</div>
                    <div class="stats-label">总图片数</div>
                  </div>
                </div>
              </el-col>
              <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
                <div class="stats-item">
                  <div class="stats-icon new-images">
                    <el-icon><Picture /></el-icon>
                  </div>
                  <div class="stats-info">
                    <div class="stats-value">{{ stats.uploadedToday }}</div>
                    <div class="stats-label">今日上传图片</div>
                  </div>
                </div>
              </el-col>
            </el-row>
            <el-row :gutter="20" class="storage-row">
              <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
                <div class="storage-info">
                  <div class="storage-label">总存储空间占用：</div>
                  <div class="storage-value">{{ formatSize(stats.totalStorage) }}</div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>
      
      <!-- 图片上传趋势 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <h3>本周图片上传趋势</h3>
            </div>
          </template>
          <div class="chart-container" v-loading="loading">
            <div id="weekChart" class="chart"></div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 月度图片上传趋势 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <h3>本月图片上传趋势</h3>
            </div>
          </template>
          <div class="chart-container" v-loading="loading">
            <div id="monthChart" class="chart"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled, User, PictureFilled, Picture } from '@element-plus/icons-vue'
import { get } from '../../../utils/request'
import * as echarts from 'echarts'

interface StatItem {
  date: string
  count: number
}

interface AdminStats {
  totalUsers: number
  newUsersToday: number
  totalImages: number
  uploadedToday: number
  totalStorage: number
  weekStats: StatItem[]
  monthStats: StatItem[]
}

interface ApiResponse<T> {
  code: number
  message: string
  data: T
  success: boolean
}

// 状态变量
const loading = ref(false)
const stats = ref<AdminStats>({
  totalUsers: 0,
  newUsersToday: 0,
  totalImages: 0,
  uploadedToday: 0,
  totalStorage: 0,
  weekStats: [],
  monthStats: []
})

// 图表实例
let weekChart: echarts.ECharts | null = null
let monthChart: echarts.ECharts | null = null

// 加载统计数据
const loadStats = async () => {
  loading.value = true
  try {
    const res = await get<ApiResponse<AdminStats>>('/admin/stats')
    
    if (res.success) {
      stats.value = res.data
      
      // 初始化图表
      initWeekChart()
      initMonthChart()
    } else {
      ElMessage.error(res.message || '加载统计数据失败')
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败，请重试')
  } finally {
    loading.value = false
  }
}

// 初始化周图表
const initWeekChart = () => {
  if (!stats.value.weekStats || stats.value.weekStats.length === 0) return
  
  const chartDom = document.getElementById('weekChart')
  if (!chartDom) return
  
  weekChart = echarts.init(chartDom)
  
  const dates = stats.value.weekStats.map(item => item.date)
  const counts = stats.value.weekStats.map(item => item.count)
  
  const option = {
    title: {
      text: '每日上传图片数量',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisTick: {
        alignWithLabel: true
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '上传数量',
        type: 'bar',
        barWidth: '60%',
        data: counts,
        itemStyle: {
          color: '#409EFF'
        }
      }
    ]
  }
  
  weekChart.setOption(option)
}

// 初始化月图表
const initMonthChart = () => {
  if (!stats.value.monthStats || stats.value.monthStats.length === 0) return
  
  const chartDom = document.getElementById('monthChart')
  if (!chartDom) return
  
  monthChart = echarts.init(chartDom)
  
  const dates = stats.value.monthStats.map(item => item.date)
  const counts = stats.value.monthStats.map(item => item.count)
  
  const option = {
    title: {
      text: '本月图片上传趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisTick: {
        alignWithLabel: true
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '上传数量',
        type: 'line',
        smooth: true,
        data: counts,
        itemStyle: {
          color: '#67C23A'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              {
                offset: 0,
                color: 'rgba(103, 194, 58, 0.3)'
              },
              {
                offset: 1,
                color: 'rgba(103, 194, 58, 0.1)'
              }
            ]
          }
        }
      }
    ]
  }
  
  monthChart.setOption(option)
}

// 格式化文件大小
const formatSize = (bytes: number) => {
  if (bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 处理窗口大小变化
const handleResize = () => {
  if (weekChart) weekChart.resize()
  if (monthChart) monthChart.resize()
}

// 组件挂载时加载数据
onMounted(() => {
  loadStats()
  window.addEventListener('resize', handleResize)
})

// 组件卸载时清理
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (weekChart) weekChart.dispose()
  if (monthChart) monthChart.dispose()
})
</script>

<style lang="scss" scoped>
.admin-stats-container {
  .stats-card {
    margin-bottom: 20px;
    
    .card-header {
      h3 {
        margin: 0;
        font-size: 18px;
        font-weight: 500;
      }
    }
  }
  
  .stats-overview {
    .stats-item {
      display: flex;
      align-items: center;
      padding: 20px;
      border-radius: 8px;
      background-color: #f5f7fa;
      margin-bottom: 15px;
      
      .stats-icon {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        background-color: #409EFF;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 15px;
        
        .el-icon {
          font-size: 30px;
          color: #fff;
        }
        
        &.new-users {
          background-color: #67C23A;
        }
        
        &.new-images {
          background-color: #E6A23C;
        }
      }
      
      .stats-info {
        .stats-value {
          font-size: 24px;
          font-weight: bold;
          color: #303133;
          margin-bottom: 5px;
        }
        
        .stats-label {
          font-size: 14px;
          color: #909399;
        }
      }
    }
    
    .storage-row {
      margin-top: 10px;
    }
    
    .storage-info {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 15px;
      background-color: #f5f7fa;
      border-radius: 8px;
      
      .storage-label {
        font-size: 16px;
        color: #606266;
        margin-right: 10px;
      }
      
      .storage-value {
        font-size: 18px;
        font-weight: bold;
        color: #F56C6C;
      }
    }
  }
  
  .chart-container {
    height: 350px;
    
    .chart {
      width: 100%;
      height: 100%;
    }
  }
}

@media (max-width: 768px) {
  .admin-stats-container {
    .stats-overview {
      .stats-item {
        padding: 15px;
        
        .stats-icon {
          width: 50px;
          height: 50px;
          
          .el-icon {
            font-size: 24px;
          }
        }
        
        .stats-info {
          .stats-value {
            font-size: 20px;
          }
          
          .stats-label {
            font-size: 12px;
          }
        }
      }
    }
    
    .chart-container {
      height: 250px;
    }
  }
}
</style> 