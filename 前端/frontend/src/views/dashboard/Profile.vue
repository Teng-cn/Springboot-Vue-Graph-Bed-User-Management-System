<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 个人资料卡片 -->
      <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="6">
        <el-card class="profile-card">
          <div class="avatar-container">
            <el-avatar :size="100" :src="userInfo.avatar || defaultAvatar" />
            <el-upload
              class="avatar-uploader"
              action="/api/user/avatar"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <el-button type="primary" size="small" class="change-avatar-btn">更换头像</el-button>
            </el-upload>
          </div>
          
          <div class="user-info">
            <h3>{{ userInfo.nickname || userInfo.username }}</h3>
            <p class="user-role">{{ userInfo.roleType === 1 ? '管理员' : '普通用户' }}</p>
            <div class="user-stats">
              <div class="stat-item">
                <div class="stat-value">{{ userStats.imageCount }}</div>
                <div class="stat-label">上传图片</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ formatSize(userStats.totalSize) }}</div>
                <div class="stat-label">总存储</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 资料编辑卡片 -->
      <el-col :xs="24" :sm="24" :md="16" :lg="16" :xl="18">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3>个人资料</h3>
            </div>
          </template>
          
          <el-form
            ref="profileFormRef"
            :model="profileForm"
            :rules="profileRules"
            label-width="100px"
            size="default"
          >
            <el-form-item label="用户名" prop="username">
              <el-input v-model="profileForm.username" disabled />
            </el-form-item>
            
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
            </el-form-item>
            
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="updateProfile" :loading="updating">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <!-- 密码修改卡片 -->
        <el-card class="password-card">
          <template #header>
            <div class="card-header">
              <h3>修改密码</h3>
            </div>
          </template>
          
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="100px"
            size="default"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input 
                v-model="passwordForm.oldPassword" 
                type="password" 
                placeholder="请输入原密码" 
                show-password 
              />
            </el-form-item>
            
            <el-form-item label="新密码" prop="newPassword">
              <el-input 
                v-model="passwordForm.newPassword" 
                type="password" 
                placeholder="请输入新密码" 
                show-password 
              />
            </el-form-item>
            
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input 
                v-model="passwordForm.confirmPassword" 
                type="password" 
                placeholder="请再次输入新密码" 
                show-password 
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="updatePassword" :loading="updatingPassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { get, post } from '../../utils/request'

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 上传头像的请求头
const uploadHeaders = ref({
  Authorization: `Bearer ${localStorage.getItem('token')}`
})

// 用户基础信息
interface UserInfo {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  avatar: string
  roleType: number
}

// 用户统计信息
interface UserStats {
  imageCount: number
  totalSize: number
}

// 表单类型
interface ProfileForm {
  username: string
  nickname: string
  email: string
  phone: string
}

interface PasswordForm {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

// 响应类型
interface UserResponse {
  code: number
  data: UserInfo
  message: string
  success: boolean
}

interface StatsResponse {
  code: number
  data: UserStats
  message: string
  success: boolean
}

// 响应式数据
const userInfo = ref<UserInfo>({
  id: 0,
  username: '',
  nickname: '',
  email: '',
  phone: '',
  avatar: '',
  roleType: 0
})

const userStats = ref<UserStats>({
  imageCount: 0,
  totalSize: 0
})

const profileFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()
const updating = ref(false)
const updatingPassword = ref(false)

// 表单数据
const profileForm = reactive<ProfileForm>({
  username: '',
  nickname: '',
  email: '',
  phone: ''
})

const passwordForm = reactive<PasswordForm>({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const profileRules = reactive<FormRules>({
  nickname: [
    { max: 20, message: '昵称长度不能超过20个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
})

// 密码表单验证规则
const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = reactive<FormRules>({
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符之间', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
})

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const res = await get<UserResponse>('/user/info')
    
    if (res.success) {
      userInfo.value = res.data
      
      // 更新表单数据
      profileForm.username = res.data.username
      profileForm.nickname = res.data.nickname || ''
      profileForm.email = res.data.email || ''
      profileForm.phone = res.data.phone || ''
      
      // 更新本地存储的用户信息
      localStorage.setItem('user', JSON.stringify(res.data))
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 加载用户统计信息
const loadUserStats = async () => {
  try {
    const res = await get<StatsResponse>('/user/stats')
    
    if (res.success) {
      userStats.value = res.data
    }
  } catch (error) {
    console.error('获取用户统计信息失败:', error)
  }
}

// 上传头像前的检查
const beforeAvatarUpload = (file: File) => {
  const isImage = /^image\/(jpeg|png|gif|jpg)$/.test(file.type)
  const isLt2M = file.size / 1024 / 1024 < 2
  
  if (!isImage) {
    ElMessage.error('头像只能是图片格式!')
    return false
  }
  
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过2MB!')
    return false
  }
  
  return true
}

// 头像上传成功的处理
const handleAvatarSuccess = (response: any) => {
  if (response.success) {
    userInfo.value.avatar = response.data.url
    
    // 更新本地存储的用户信息
    const userStr = localStorage.getItem('user')
    if (userStr) {
      try {
        const user = JSON.parse(userStr)
        user.avatar = response.data.url
        localStorage.setItem('user', JSON.stringify(user))
      } catch (e) {
        console.error('解析用户信息失败:', e)
      }
    }
    
    ElMessage.success('头像更新成功')
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

// 更新个人资料
const updateProfile = async () => {
  if (!profileFormRef.value) return
  
  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      updating.value = true
      try {
        const res = await post('/user/update', {
          nickname: profileForm.nickname,
          email: profileForm.email,
          phone: profileForm.phone
        })
        
        if (res.success) {
          ElMessage.success('个人资料更新成功')
          loadUserInfo() // 重新加载用户信息
        } else {
          ElMessage.error(res.message || '更新失败')
        }
      } catch (error) {
        console.error('更新个人资料失败:', error)
        ElMessage.error('更新个人资料失败，请重试')
      } finally {
        updating.value = false
      }
    }
  })
}

// 修改密码
const updatePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      updatingPassword.value = true
      try {
        const res = await post('/user/password', {
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        
        if (res.success) {
          ElMessage.success('密码修改成功，请重新登录')
          
          // 清除密码表单
          passwordForm.oldPassword = ''
          passwordForm.newPassword = ''
          passwordForm.confirmPassword = ''
          
          // 清除token和用户信息，跳转到登录页
          setTimeout(() => {
            localStorage.removeItem('token')
            localStorage.removeItem('user')
            window.location.href = '/login'
          }, 1500)
        } else {
          ElMessage.error(res.message || '密码修改失败')
        }
      } catch (error) {
        console.error('修改密码失败:', error)
        ElMessage.error('修改密码失败，请重试')
      } finally {
        updatingPassword.value = false
      }
    }
  })
}

// 格式化文件大小
const formatSize = (bytes: number) => {
  if (bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 组件挂载时加载信息
onMounted(() => {
  loadUserInfo()
  loadUserStats()
})
</script>

<style lang="scss" scoped>
.profile-container {
  .profile-card {
    margin-bottom: 20px;
    
    .avatar-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-bottom: 20px;
      
      .change-avatar-btn {
        margin-top: 10px;
      }
    }
    
    .user-info {
      text-align: center;
      
      h3 {
        margin: 10px 0;
        font-size: 18px;
      }
      
      .user-role {
        color: #909399;
        margin-bottom: 15px;
      }
      
      .user-stats {
        display: flex;
        justify-content: space-around;
        border-top: 1px solid #ebeef5;
        padding-top: 15px;
        
        .stat-item {
          .stat-value {
            font-size: 18px;
            font-weight: 500;
            color: #303133;
          }
          
          .stat-label {
            font-size: 14px;
            color: #909399;
            margin-top: 5px;
          }
        }
      }
    }
  }
  
  .password-card {
    margin-top: 20px;
  }
  
  .card-header {
    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 500;
    }
  }
}

@media (max-width: 768px) {
  .profile-container {
    .profile-card {
      margin-bottom: 20px;
    }
  }
}
</style> 