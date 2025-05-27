<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-title">
        <img src="../assets/images/logo.svg" alt="Logo" class="logo" />
        <h2>图床系统 - 注册</h2>
      </div>
      
      <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" label-width="0" size="large">
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="确认密码" prefix-icon="Lock" show-password />
        </el-form-item>
        
        <el-form-item prop="email">
          <el-input v-model="registerForm.email" placeholder="邮箱" prefix-icon="Message" />
        </el-form-item>
        
        <el-form-item>
          <el-checkbox v-model="registerForm.agreement">我已阅读并同意<el-link type="primary" href="javascript:;">服务条款</el-link></el-checkbox>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" class="register-button" :loading="loading" @click="handleRegister">注册</el-button>
        </el-form-item>
      </el-form>
      
      <div class="register-footer">
        <span>已有账号?</span>
        <router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { post } from '../utils/request'

interface RegisterForm {
  username: string
  password: string
  confirmPassword: string
  email: string
  agreement: boolean
}

interface RegisterResponse {
  code: number
  message: string
  success: boolean
}

const registerFormRef = ref<FormInstance>()
const loading = ref(false)
const router = useRouter()

const registerForm = reactive<RegisterForm>({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  agreement: false
})

// 密码验证
const validatePass = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (registerForm.confirmPassword !== '') {
      if (registerFormRef.value) {
        registerFormRef.value.validateField('confirmPassword', () => null)
      }
    }
    callback()
  }
}

// 确认密码验证
const validateConfirmPass = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// 协议验证
const validateAgreement = (rule: any, value: boolean, callback: any) => {
  if (!value) {
    callback(new Error('请阅读并同意服务条款'))
  } else {
    callback()
  }
}

const registerRules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3到20个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePass, trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPass, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  agreement: [
    { validator: validateAgreement, trigger: 'change' }
  ]
})

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await post<RegisterResponse>('/user/register', {
          username: registerForm.username,
          password: registerForm.password,
          email: registerForm.email
        })
        
        if (res.success) {
          ElMessage.success('注册成功，请登录')
          router.push('/login')
        }
      } catch (error) {
        console.error('注册失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.register-container {
  height: 100vh;
  background-color: var(--background-base);
  display: flex;
  justify-content: center;
  align-items: center;
}

.register-box {
  width: 400px;
  padding: 30px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.register-title {
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

.register-button {
  width: 100%;
}

.register-footer {
  margin-top: 20px;
  text-align: center;
  color: var(--text-secondary);
  
  a {
    color: var(--primary-color);
    margin-left: 5px;
  }
}

@media (max-width: 480px) {
  .register-box {
    width: 90%;
    padding: 20px;
  }
}
</style> 