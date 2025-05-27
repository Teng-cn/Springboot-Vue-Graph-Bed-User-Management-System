<template>
  <div class="test-token">
    <h1>Token 测试</h1>
    
    <div class="token-section">
      <h2>当前 Token</h2>
      <el-input 
        v-model="token" 
        type="textarea" 
        :rows="3" 
        readonly
        placeholder="无token"
      />
      <div class="button-group">
        <el-button type="primary" @click="refreshToken">刷新Token</el-button>
        <el-button type="danger" @click="clearToken">清除Token</el-button>
        <el-button type="success" @click="testToken">测试Token</el-button>
        <el-button type="warning" @click="testAuthStatus">测试认证状态</el-button>
      </div>
    </div>
    
    <div class="result-section" v-if="testResult">
      <h2>测试结果</h2>
      <pre>{{ JSON.stringify(testResult, null, 2) }}</pre>
    </div>
    
    <div class="manual-section">
      <h2>手动设置Token</h2>
      <el-input 
        v-model="manualToken" 
        type="textarea" 
        :rows="3" 
        placeholder="输入token"
      />
      <el-button type="primary" @click="setManualToken">设置Token</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { get } from '../../utils/request'

interface TestResponse {
  data: any;
  success: boolean;
  message: string;
  code: number;
}

const token = ref('')
const manualToken = ref('')
const testResult = ref<any>(null)

// 刷新显示的token
const refreshToken = () => {
  token.value = localStorage.getItem('token') || ''
  if (!token.value) {
    ElMessage.warning('未找到Token')
  }
}

// 清除token
const clearToken = () => {
  localStorage.removeItem('token')
  token.value = ''
  ElMessage.success('Token已清除')
}

// 手动设置token
const setManualToken = () => {
  if (manualToken.value) {
    localStorage.setItem('token', manualToken.value)
    token.value = manualToken.value
    ElMessage.success('Token已设置')
  } else {
    ElMessage.warning('请输入Token')
  }
}

// 测试token
const testToken = async () => {
  try {
    // 打印当前请求头
    console.log('当前token:', localStorage.getItem('token'))
    console.log('请求头Authorization:', `Bearer ${localStorage.getItem('token')}`)
    
    // 发送测试请求
    const res = await get<TestResponse>('/test-user/info')
    testResult.value = res.data
    ElMessage.success('测试成功')
  } catch (error) {
    console.error('测试失败:', error)
    ElMessage.error('测试失败')
  }
}

// 测试认证状态
const testAuthStatus = async () => {
  try {
    // 发送测试请求
    const res = await get<TestResponse>('/test/auth-status')
    testResult.value = res.data
    ElMessage.success('认证状态测试成功')
  } catch (error) {
    console.error('认证状态测试失败:', error)
    ElMessage.error('认证状态测试失败')
  }
}

onMounted(() => {
  // 刷新并打印token信息
  refreshToken()
  console.log('组件挂载，当前token:', localStorage.getItem('token'))
  console.log('上传请求头:', { Authorization: `Bearer ${localStorage.getItem('token')}` })
})
</script>

<style scoped>
.test-token {
  padding: 20px;
}

.token-section,
.result-section,
.manual-section {
  margin-bottom: 30px;
}

.button-group {
  margin-top: 10px;
  display: flex;
  gap: 10px;
}

pre {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-all;
}
</style> 