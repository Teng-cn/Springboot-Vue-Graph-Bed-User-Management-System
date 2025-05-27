<template>
  <div class="home-container">
    <header class="header">
      <div class="logo">
        <img src="../assets/images/logo.png" alt="Logo" />
        <h1>图床系统</h1>
      </div>
      <div class="nav">
        <router-link to="/login" class="nav-link">登录</router-link>
        <router-link to="/register" class="nav-link register">注册</router-link>
        <router-link to="/login?admin=true" class="nav-link admin">管理员登录</router-link>
      </div>
    </header>
    
    <main class="main">
      <div class="hero">
        <h2>简单、高效的图片托管服务</h2>
        <p>上传、管理和分享您的图片，提供稳定快速的图片访问</p>
        <router-link to="/register" class="start-btn">立即开始</router-link>
      </div>
      
      <div class="features">
        <div class="feature-item">
          <el-icon><Upload /></el-icon>
          <h3>简单上传</h3>
          <p>支持拖拽上传、多图上传、剪贴板上传</p>
        </div>
        <div class="feature-item">
          <el-icon><PictureFilled /></el-icon>
          <h3>图片管理</h3>
          <p>方便管理已上传的图片，复制链接、删除图片</p>
        </div>
        <div class="feature-item">
          <el-icon><Link /></el-icon>
          <h3>多种链接格式</h3>
          <p>支持多种图片链接格式，满足不同场景需求</p>
        </div>
      </div>
    </main>
    
    <footer class="footer">
      <p>© 2025 图床系统 - 提供图片托管服务</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { Upload, PictureFilled, Link } from '@element-plus/icons-vue'
</script>

<style lang="scss" scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 50px;
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  
  .logo {
    display: flex;
    align-items: center;
    
    img {
      height: 40px;
      margin-right: 10px;
    }
    
    h1 {
      font-size: 24px;
      color: #409eff;
      margin: 0;
    }
  }
  
  .nav {
    display: flex;
    align-items: center;
    
    .nav-link {
      margin-left: 20px;
      text-decoration: none;
      color: #606266;
      padding: 8px 15px;
      border-radius: 4px;
      transition: all 0.3s;
      
      &:hover {
        color: #409eff;
      }
      
      &.register {
        background-color: #409eff;
        color: #fff;
        
        &:hover {
          background-color: #66b1ff;
        }
      }
      
      &.admin {
        background-color: #67c23a;
        color: #fff;
        
        &:hover {
          background-color: #85ce61;
        }
      }
    }
  }
}

.main {
  flex: 1;
  padding: 50px;
  
  .hero {
    text-align: center;
    margin-bottom: 80px;
    
    h2 {
      font-size: 36px;
      color: #303133;
      margin-bottom: 20px;
    }
    
    p {
      font-size: 18px;
      color: #606266;
      margin-bottom: 30px;
    }
    
    .start-btn {
      display: inline-block;
      padding: 12px 30px;
      background-color: #409eff;
      color: #fff;
      border-radius: 4px;
      text-decoration: none;
      font-size: 16px;
      transition: all 0.3s;
      
      &:hover {
        background-color: #66b1ff;
      }
    }
  }
  
  .features {
    display: flex;
    justify-content: space-around;
    
    .feature-item {
      text-align: center;
      width: 25%;
      padding: 20px;
      
      .el-icon {
        font-size: 48px;
        color: #409eff;
        margin-bottom: 20px;
      }
      
      h3 {
        font-size: 20px;
        color: #303133;
        margin-bottom: 15px;
      }
      
      p {
        font-size: 16px;
        color: #606266;
      }
    }
  }
}

.footer {
  text-align: center;
  padding: 20px;
  background-color: #f5f7fa;
  color: #909399;
  
  p {
    margin: 0;
  }
}

@media (max-width: 768px) {
  .header {
    padding: 15px 20px;
    
    .logo {
      img {
        height: 30px;
      }
      
      h1 {
        font-size: 20px;
      }
    }
  }
  
  .main {
    padding: 30px 20px;
    
    .hero {
      margin-bottom: 50px;
      
      h2 {
        font-size: 28px;
      }
      
      p {
        font-size: 16px;
      }
    }
    
    .features {
      flex-direction: column;
      
      .feature-item {
        width: 100%;
        margin-bottom: 30px;
      }
    }
  }
}
</style> 