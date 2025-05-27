<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-title">
        <img src="../assets/images/logo.svg" alt="Logo" class="logo" />
        <h2>{{ isAdminLogin ? '管理员登录' : '图床系统' }}</h2>
      </div>
      
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-width="0" size="large">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" :placeholder="isAdminLogin ? '管理员账号' : '用户名'" prefix-icon="User" />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        
        <el-form-item>
          <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
          <el-link type="primary" class="forgot-pwd" href="javascript:;">忘记密码?</el-link>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" class="login-button" :loading="loading" @click="handleLogin">{{ isAdminLogin ? '管理员登录' : '登录' }}</el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <span v-if="!isAdminLogin">还没有账号?</span>
        <router-link v-if="!isAdminLogin" to="/register">立即注册</router-link>
        <router-link v-if="isAdminLogin" to="/login">普通用户登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { post } from '../utils/request'

interface LoginForm {
  username: string
  password: string
  remember: boolean
}

interface LoginResponse {
  code: number
  message: string
  data: {
    token: string
    user: {
      id: number
      username: string
      nickname: string
      avatar: string
      email: string
      roleType: number
    }
  }
  success: boolean
}

const loginFormRef = ref<FormInstance>()
const loading = ref(false)
const router = useRouter()
const route = useRoute()

// 检查是否是管理员登录
const isAdminLogin = computed(() => {
  return route.query.admin === 'true'
})

// 表单默认值 - 如果是管理员登录，默认填入admin
const loginForm = reactive<LoginForm>({
  username: isAdminLogin.value ? 'admin' : '',
  password: '',
  remember: false
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3到20个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符之间', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 打印登录信息用于调试
        console.log('准备登录，用户名:', loginForm.username);
        console.log('准备登录，密码:', loginForm.password);
        console.log('是否是管理员登录:', isAdminLogin.value);
        
        const res = await post<LoginResponse>('/login', {
          username: loginForm.username,
          password: loginForm.password,
          remember: loginForm.remember
        })
        
        if (res.success) {
          // 打印返回的数据
          console.log('登录成功，返回数据:', res.data)
          
          // 保存token
          localStorage.setItem('token', res.data.token)
          console.log('保存token:', res.data.token)
          
          // 保存用户信息
          localStorage.setItem('user', JSON.stringify(res.data.user))
          console.log('保存用户信息:', res.data.user)
          
          // 根据是否是管理员登录显示不同的提示
          if (isAdminLogin.value) {
            ElMessage.success('管理员登录成功')
          } else {
            ElMessage.success('登录成功')
          }
          
          // 如果有重定向，则跳转到重定向页面
          const redirect = route.query.redirect as string
          router.push(redirect || '/dashboard')
        }
      } catch (error) {
        console.error('登录失败:', error)
        
        // 根据是否是管理员登录显示不同的错误提示
        if (isAdminLogin.value) {
          ElMessage.error('管理员账号或密码错误')
        } else {
          ElMessage.error('用户名或密码错误')
        }
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.login-container {
  height: 100vh;
  background-color: var(--background-base);
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-box {
  width: 400px;
  padding: 30px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  
  .logo {
    width: 60px;
    height: 60px;
  }
  
  h2 {
    margin-top: 10px;
    font-size: 24px;
    color: var(--text-primary);
  }
}

.login-button {
  width: 100%;
}

.forgot-pwd {
  float: right;
}

.login-footer {
  margin-top: 20px;
  text-align: center;
  color: var(--text-secondary);
  
  a {
    color: var(--primary-color);
    margin-left: 5px;
  }
}

@media (max-width: 480px) {
  .login-box {
    width: 90%;
    padding: 20px;
  }
}
</style> 