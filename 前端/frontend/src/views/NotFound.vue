<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <img src="../assets/images/404.svg" alt="404" class="not-found-image" />
      <h1>404</h1>
      <h2>页面不存在</h2>
      <p>您访问的页面可能已被删除、名称已更改或暂时不可用</p>
      <div class="not-found-actions">
        <el-button type="primary" @click="goHome">返回首页</el-button>
        <el-button @click="goBack">返回上一页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style lang="scss" scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.not-found-content {
  text-align: center;
  padding: 30px;
  
  .not-found-image {
    width: 200px;
    height: 200px;
    margin-bottom: 20px;
  }
  
  h1 {
    font-size: 80px;
    color: #409eff;
    margin: 0;
    line-height: 1.2;
  }
  
  h2 {
    font-size: 24px;
    color: #303133;
    margin: 10px 0;
  }
  
  p {
    font-size: 16px;
    color: #606266;
    margin-bottom: 30px;
  }
  
  .not-found-actions {
    display: flex;
    justify-content: center;
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .not-found-content {
    padding: 20px;
    
    .not-found-image {
      width: 150px;
      height: 150px;
    }
    
    h1 {
      font-size: 60px;
    }
    
    h2 {
      font-size: 20px;
    }
    
    p {
      font-size: 14px;
    }
    
    .not-found-actions {
      flex-direction: column;
      gap: 10px;
      
      .el-button {
        width: 100%;
      }
    }
  }
}
</style> 