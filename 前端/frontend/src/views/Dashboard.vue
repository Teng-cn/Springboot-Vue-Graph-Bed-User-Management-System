<template>
  <div class="dashboard-container">
    <el-container class="dashboard-layout">
      <!-- 侧边菜单 -->
      <el-aside width="220px" class="dashboard-aside">
        <div class="logo">
          <img src="../assets/images/logo.svg" alt="Logo" />
          <h1>图床系统</h1>
        </div>
        
        <el-menu
          :default-active="activeMenu"
          class="dashboard-menu"
          router
          :collapse="isCollapse"
        >
          <el-menu-item index="/dashboard/upload">
            <el-icon><Upload /></el-icon>
            <template #title>上传图片</template>
          </el-menu-item>
          
          <el-menu-item index="/dashboard/images">
            <el-icon><PictureFilled /></el-icon>
            <template #title>图片管理</template>
          </el-menu-item>
          
          <el-menu-item index="/dashboard/profile">
            <el-icon><User /></el-icon>
            <template #title>个人中心</template>
          </el-menu-item>

          <!-- 管理员菜单 -->
          <template v-if="isAdmin">
            <el-divider />
            <el-sub-menu index="/dashboard/admin">
              <template #title>
                <el-icon><Setting /></el-icon>
                <span>管理员功能</span>
              </template>
              <el-menu-item index="/dashboard/admin/users">
                <el-icon><UserFilled /></el-icon>
                <span>用户管理</span>
              </el-menu-item>
              <el-menu-item index="/dashboard/admin/all-images">
                <el-icon><Picture /></el-icon>
                <span>所有图片</span>
              </el-menu-item>
              <el-menu-item index="/dashboard/admin/stats">
                <el-icon><DataLine /></el-icon>
                <span>系统统计</span>
              </el-menu-item>
              <el-menu-item index="/dashboard/admin/test-auth">
                <el-icon><Key /></el-icon>
                <span>权限测试</span>
              </el-menu-item>
            </el-sub-menu>
          </template>
        </el-menu>
      </el-aside>
      
      <!-- 主内容区 -->
      <el-container class="dashboard-main-container">
        <el-header class="dashboard-header">
          <div class="header-left">
            <el-icon class="toggle-sidebar" @click="toggleSidebar">
              <Fold v-if="!isCollapse" />
              <Expand v-else />
            </el-icon>
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentRoute }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="header-right">
            <el-dropdown trigger="click" @command="handleCommand">
              <div class="user-info">
                <el-avatar :size="32" :src="userInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
                <span class="username">{{ userInfo.nickname || userInfo.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <el-main class="dashboard-main">
          <router-view />
        </el-main>
        
        <el-footer class="dashboard-footer">
          <p>© 2025 图床系统 - 提供图片托管服务</p>
        </el-footer>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Fold, Expand, ArrowDown, Upload, PictureFilled, User, Setting, UserFilled, Picture, DataLine, Key } from '@element-plus/icons-vue'

interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar: string
  roleType?: number
}

const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)
const userInfo = ref<UserInfo>({
  id: 0,
  username: '',
  nickname: '',
  avatar: ''
})

// 获取当前激活的菜单项
const activeMenu = computed(() => {
  return route.path
})

// 获取当前路由名称
const currentRoute = computed(() => {
  switch (route.name) {
    case 'Upload':
      return '上传图片'
    case 'Images':
      return '图片管理'
    case 'Profile':
      return '个人中心'
    default:
      return '控制台'
  }
})

// 切换侧边栏
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 下拉菜单命令处理
const handleCommand = (command: string) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      // 清除token和用户信息
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      ElMessage.success('退出登录成功')
      router.push('/login')
    }).catch(() => {
      // 取消操作
    })
  } else if (command === 'profile') {
    router.push('/dashboard/profile')
  }
}

// 在组件挂载时获取用户信息
onMounted(() => {
  const userString = localStorage.getItem('user')
  if (userString) {
    try {
      userInfo.value = JSON.parse(userString)
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }
  
  // 如果没有用户数据或没有选中的菜单项，则默认跳转到上传页面
  if (route.path === '/dashboard') {
    router.push('/dashboard/upload')
  }
})

// 判断是否为管理员
const isAdmin = computed(() => {
  return userInfo.value.roleType === 1;
});
</script>

<style lang="scss" scoped>
.dashboard-container {
  height: 100vh;
  overflow: hidden;
}

.dashboard-layout {
  height: 100%;
}

.dashboard-aside {
  background-color: #304156;
  color: #fff;
  transition: width 0.3s;
  
  .logo {
    display: flex;
    align-items: center;
    padding: 15px;
    height: 60px;
    background-color: #263445;
    
    img {
      width: 30px;
      height: 30px;
      margin-right: 10px;
    }
    
    h1 {
      font-size: 18px;
      color: #fff;
      margin: 0;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
  
  .dashboard-menu {
    border-right: none;
    background-color: #304156;
    
    :deep(.el-menu-item) {
      color: #bfcbd9;
      
      &.is-active {
        color: #409eff;
        background-color: #263445;
      }
      
      &:hover {
        background-color: #263445;
      }
    }
    
    :deep(.el-submenu__title) {
      color: #bfcbd9;
      
      &:hover {
        background-color: #263445;
      }
    }
  }
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  
  .header-left {
    display: flex;
    align-items: center;
    
    .toggle-sidebar {
      font-size: 20px;
      margin-right: 15px;
      cursor: pointer;
      color: #606266;
    }
  }
  
  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      
      .username {
        margin: 0 10px;
        color: #606266;
      }
    }
  }
}

.dashboard-main-container {
  flex-direction: column;
  overflow: hidden;
}

.dashboard-main {
  padding: 20px;
  overflow-y: auto;
  background-color: #f0f2f5;
}

.dashboard-footer {
  text-align: center;
  background-color: #fff;
  padding: 15px 0;
  color: #909399;
  font-size: 12px;
  border-top: 1px solid #e6e6e6;
}
</style> 