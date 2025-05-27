<template>
  <div class="upload-container">
    <el-card class="upload-card">
      <template #header>
        <div class="card-header">
          <h3>上传图片</h3>
          <p>支持jpg、jpeg、png、gif格式图片，单张最大10MB</p>
        </div>
      </template>
      
      <el-upload
        class="upload-area"
        drag
        action="/api/image/upload"
        multiple
        :headers="uploadHeaders"
        :on-preview="handlePreview"
        :on-success="handleSuccess"
        :on-error="handleError"
        :before-upload="beforeUpload"
        :on-exceed="handleExceed"
        :limit="10"
        :file-list="fileList"
        list-type="picture"
      >
        <el-icon class="upload-icon"><Upload /></el-icon>
        <div class="upload-text">将图片拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="upload-tip">
            <p>也可以通过粘贴(Ctrl+V)将剪贴板中的图片直接上传</p>
          </div>
        </template>
      </el-upload>
      
      <!-- 上传结果展示 -->
      <div v-if="uploadResults.length > 0" class="upload-results">
        <h4>上传结果</h4>
        <el-table :data="uploadResults" style="width: 100%">
          <el-table-column prop="name" label="图片名称" />
          <el-table-column label="预览" width="100">
            <template #default="scope">
              <el-image 
                style="width: 60px; height: 60px" 
                :src="scope.row.url" 
                :preview-src-list="[scope.row.url]"
                fit="cover"
              />
            </template>
          </el-table-column>
          <el-table-column label="图片链接" show-overflow-tooltip>
            <template #default="scope">
              <div class="url-container">
                <el-input 
                  v-model="scope.row.url" 
                  size="small" 
                  readonly
                />
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="copyUrl(scope.row.url)"
                  style="margin-left: 8px;"
                >
                  复制
                </el-button>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="Markdown链接" show-overflow-tooltip>
            <template #default="scope">
              <div class="url-container">
                <el-input 
                  v-model="scope.row.markdownUrl" 
                  size="small" 
                  readonly
                />
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="copyUrl(scope.row.markdownUrl)"
                  style="margin-left: 8px;"
                >
                  复制
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import type { UploadFile } from 'element-plus'

interface UploadResult {
  id: number
  name: string
  url: string
  markdownUrl: string
  size: number
}

const fileList = ref<UploadFile[]>([])
const uploadResults = ref<UploadResult[]>([])

// 上传请求头
const uploadHeaders = ref({
  Authorization: `Bearer ${localStorage.getItem('token')}`
})

// 刷新 token
const refreshToken = () => {
  const token = localStorage.getItem('token')
  uploadHeaders.value = {
    Authorization: `Bearer ${token}`
  }
  return token
}

// 上传前检查
const beforeUpload = (file: File) => {
  // 更新上传请求头中的token
  const token = refreshToken()
  console.log('上传前更新token:', token)
  
  if (!token) {
    ElMessage.error('未登录或 token 已失效，请重新登录')
    return false
  }
  
  // 检查文件类型
  const isImage = /^image\/(jpeg|png|gif|jpg)$/.test(file.type)
  if (!isImage) {
    ElMessage.error('只能上传JPG/PNG/GIF格式的图片!')
    return false
  }
  
  // 检查文件大小
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过10MB!')
    return false
  }
  
  return true
}

// 上传成功处理
const handleSuccess = (response: any) => {
  if (response.success) {
    const { data } = response
    uploadResults.value.push({
      id: data.id,
      name: data.name,
      url: data.url,
      markdownUrl: `![${data.name}](${data.url})`,
      size: data.size
    })
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 上传失败处理
const handleError = (error: any) => {
  console.error('上传失败:', error)
  ElMessage.error('上传失败，请重试')
}

// 处理超出限制
const handleExceed = () => {
  ElMessage.warning('一次最多上传10张图片')
}

// 预览处理
const handlePreview = (file: UploadFile) => {
  window.open(file.url)
}

// 复制URL
const copyUrl = (url: string) => {
  navigator.clipboard.writeText(url).then(() => {
    ElMessage.success('复制成功')
  }).catch(err => {
    console.error('复制失败:', err)
    ElMessage.error('复制失败，请手动复制')
  })
}

// 监听粘贴事件
const handlePaste = (event: ClipboardEvent) => {
  const items = event.clipboardData?.items
  if (!items) return
  
  for (let i = 0; i < items.length; i++) {
    if (items[i].type.indexOf('image') !== -1) {
      const blob = items[i].getAsFile()
      if (blob) {
        // 创建FormData并上传
        const formData = new FormData()
        formData.append('file', blob, `clipboard_${new Date().getTime()}.png`)
        
        // 获取最新的token
        const token = refreshToken()
        console.log('粘贴上传使用token:', token)
        
        if (!token) {
          ElMessage.error('未登录或 token 已失效，请重新登录')
          return
        }
        
        // 使用fetch API进行上传
        fetch('/api/image/upload', {
          method: 'POST',
          body: formData,
          headers: {
            Authorization: `Bearer ${token}`,
            'X-Requested-With': 'XMLHttpRequest' // 添加额外的请求头，确保服务器识别为AJAX请求
          }
        })
        .then(response => response.json())
        .then(result => {
          if (result.success) {
            const { data } = result
            uploadResults.value.push({
              id: data.id,
              name: data.name,
              url: data.url,
              markdownUrl: `![${data.name}](${data.url})`,
              size: data.size
            })
            ElMessage.success('粘贴上传成功')
          } else {
            console.error('粘贴上传失败:', result)
            ElMessage.error(result.message || '粘贴上传失败')
          }
        })
        .catch(error => {
          console.error('粘贴上传失败:', error)
          ElMessage.error('粘贴上传失败，请重试')
        })
      }
    }
  }
}

onMounted(() => {
  // 添加粘贴事件监听
  document.addEventListener('paste', handlePaste)
  
  // 刷新并打印token信息
  const token = refreshToken()
  console.log('组件挂载，当前token:', token)
  console.log('上传请求头:', uploadHeaders.value)
  
  // 组件卸载时移除监听
  return () => {
    document.removeEventListener('paste', handlePaste)
  }
})
</script>

<style lang="scss" scoped>
.upload-container {
  .upload-card {
    margin-bottom: 20px;
    
    .card-header {
      display: flex;
      flex-direction: column;
      
      h3 {
        margin: 0 0 5px 0;
        font-size: 18px;
        font-weight: 500;
      }
      
      p {
        margin: 0;
        font-size: 14px;
        color: #909399;
      }
    }
  }
  
  .upload-area {
    width: 100%;
    
    :deep(.el-upload) {
      width: 100%;
    }
    
    :deep(.el-upload-dragger) {
      width: 100%;
      height: 200px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
    }
    
    .upload-icon {
      font-size: 48px;
      color: #c0c4cc;
      margin-bottom: 10px;
    }
    
    .upload-text {
      color: #606266;
      font-size: 16px;
      
      em {
        color: #409eff;
        font-style: normal;
      }
    }
    
    .upload-tip {
      margin-top: 10px;
      color: #909399;
      font-size: 14px;
      text-align: center;
    }
  }
  
  .upload-results {
    margin-top: 20px;
    
    h4 {
      margin-top: 0;
      margin-bottom: 15px;
      font-size: 16px;
      font-weight: 500;
    }
    
    .url-container {
      display: flex;
      align-items: center;
    }
  }
}
</style> 