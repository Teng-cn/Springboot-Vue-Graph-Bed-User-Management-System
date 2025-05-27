<template>
  <div class="test-headers">
    <h1>请求头测试</h1>
    <el-button type="primary" @click="testHeaders" style="margin-right: 10px;">测试请求头</el-button>
    <el-button type="success" @click="testUserInfo">测试用户信息</el-button>
    
    <div v-if="headers" class="headers-container">
      <h2>请求头信息</h2>
      <pre>{{ JSON.stringify(headers, null, 2) }}</pre>
    </div>
    
    <div v-if="userInfo" class="headers-container">
      <h2>用户信息</h2>
      <pre>{{ JSON.stringify(userInfo, null, 2) }}</pre>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { get } from '../../utils/request'
import { ElMessage } from 'element-plus'

const headers = ref<Record<string, string> | null>(null)
const userInfo = ref<Record<string, string> | null>(null)

const testHeaders = async () => {
  try {
    // 检查localStorage中的token
    const token = localStorage.getItem('token')
    console.log('localStorage中的token:', token)
    
    // 发送请求获取请求头信息
    const res = await get('/test/headers')
    headers.value = res.data
    console.log('请求头信息:', res.data)
  } catch (error) {
    console.error('测试请求头失败:', error)
    ElMessage.error('测试请求头失败')
  }
}

const testUserInfo = async () => {
  try {
    // 发送请求获取用户信息
    const res = await get('/test-user/info')
    userInfo.value = res.data
    console.log('用户信息:', res.data)
  } catch (error) {
    console.error('测试用户信息失败:', error)
    ElMessage.error('测试用户信息失败')
  }
}
</script>

<style scoped>
.test-headers {
  padding: 20px;
}

.headers-container {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

pre {
  white-space: pre-wrap;
  word-break: break-all;
}
</style> 