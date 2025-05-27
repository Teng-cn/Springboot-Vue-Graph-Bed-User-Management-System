<template>
  <div class="test-auth-container">
    <el-card class="test-card">
      <template #header>
        <div class="card-header">
          <h3>权限测试</h3>
        </div>
      </template>
      
      <div class="test-content" v-loading="loading">
        <div class="button-group">
          <el-button type="primary" @click="testAuth">测试权限</el-button>
          <el-button type="success" @click="simpleTest">简单测试</el-button>
        </div>
        
        <div v-if="result" class="result-box">
          <h4>测试结果：</h4>
          <pre>{{ result }}</pre>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { get } from '../../../utils/request'

const loading = ref(false)
const result = ref('')

// 测试权限
const testAuth = async () => {
  loading.value = true
  try {
    const res = await get<any>('/admin/test-auth')
    
    if (res.success) {
      result.value = res.data
    } else {
      ElMessage.error(res.message || '测试失败')
    }
  } catch (error: any) {
    console.error('测试失败:', error)
    result.value = `测试失败: ${error.message || '未知错误'}`
  } finally {
    loading.value = false
  }
}

// 简单测试
const simpleTest = async () => {
  loading.value = true
  try {
    const res = await get<any>('/admin/simple-test')
    
    if (res.success) {
      result.value = res.data
      ElMessage.success('简单测试成功')
    } else {
      ElMessage.error(res.message || '测试失败')
    }
  } catch (error: any) {
    console.error('简单测试失败:', error)
    result.value = `简单测试失败: ${error.message || '未知错误'}`
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.test-auth-container {
  .test-card {
    .card-header {
      h3 {
        margin: 0;
        font-size: 18px;
        font-weight: 500;
      }
    }
  }
  
  .test-content {
    padding: 20px 0;
    
    .button-group {
      display: flex;
      gap: 10px;
      margin-bottom: 20px;
    }
    
    .result-box {
      margin-top: 20px;
      padding: 15px;
      background-color: #f5f7fa;
      border-radius: 4px;
      
      h4 {
        margin-top: 0;
        margin-bottom: 10px;
      }
      
      pre {
        white-space: pre-wrap;
        word-break: break-all;
        font-family: monospace;
        margin: 0;
      }
    }
  }
}
</style> 