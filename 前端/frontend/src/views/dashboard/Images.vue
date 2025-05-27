<template>
  <div class="images-container">
    <el-card class="images-card">
      <template #header>
        <div class="card-header">
          <div class="left">
            <h3>图片管理</h3>
            <p>共 {{ totalCount }} 张图片</p>
          </div>
          <div class="right">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索图片名称"
              class="search-input"
              clearable
              @keyup.enter="handleSearch"
            >
              <template #append>
                <el-button @click="handleSearch">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
          </div>
        </div>
      </template>
      
      <!-- 图片列表 -->
      <div v-loading="loading" class="images-list">
        <el-empty v-if="images.length === 0 && !loading" description="暂无图片" />
        
        <el-row :gutter="20" v-else>
          <el-col 
            v-for="image in images" 
            :key="image.id" 
            :xs="24" 
            :sm="12" 
            :md="8" 
            :lg="6" 
            :xl="4"
          >
            <el-card shadow="hover" class="image-item">
              <div class="image-preview" @click="previewImage(image)">
                <el-image 
                  :src="image.url" 
                  fit="cover"
                  :preview-src-list="[image.url]"
                  :initial-index="0"
                />
              </div>
              <div class="image-info">
                <div class="image-name" :title="image.name">{{ image.name }}</div>
                <div class="image-meta">
                  <span>{{ formatDate(image.createTime) }}</span>
                  <span>{{ formatSize(image.size) }}</span>
                </div>
              </div>
              <div class="image-actions">
                <el-dropdown trigger="click" @command="(command) => handleCommand(command, image)">
                  <el-button type="primary" text>
                    <el-icon><MoreFilled /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="copy">复制链接</el-dropdown-item>
                      <el-dropdown-item command="copyMarkdown">复制Markdown</el-dropdown-item>
                      <el-dropdown-item command="download">下载图片</el-dropdown-item>
                      <el-dropdown-item command="delete" divided>删除图片</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-if="totalCount > 0"
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[12, 24, 36, 48]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totalCount"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { MoreFilled, Search } from '@element-plus/icons-vue'
import { get, del } from '../../utils/request'

interface ImageItem {
  id: number
  name: string
  url: string
  size: number
  width: number
  height: number
  mimeType: string
  createTime: string
  accessCount: number
}

interface ImageListResponse {
  code: number
  data: {
    list: ImageItem[]
    total: number
  }
  message: string
  success: boolean
}

const images = ref<ImageItem[]>([])
const loading = ref(false)
const totalCount = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const searchKeyword = ref('')

// 加载图片列表
const loadImages = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value
    }
    
    const res = await get<ImageListResponse>('/image/list', params)
    
    if (res.success) {
      images.value = res.data.list
      totalCount.value = res.data.total
    } else {
      ElMessage.error(res.message || '加载图片列表失败')
    }
  } catch (error) {
    console.error('加载图片列表失败:', error)
    ElMessage.error('加载图片列表失败，请重试')
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  loadImages()
}

// 处理分页大小变化
const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadImages()
}

// 处理页码变化
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadImages()
}

// 预览图片
const previewImage = (image: ImageItem) => {
  // 使用Element Plus的图片预览功能，无需额外处理
}

// 格式化日期
const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 格式化文件大小
const formatSize = (bytes: number) => {
  if (bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 处理下拉菜单命令
const handleCommand = (command: string, image: ImageItem) => {
  switch (command) {
    case 'copy':
      copyToClipboard(image.url)
      break
    case 'copyMarkdown':
      copyToClipboard(`![${image.name}](${image.url})`)
      break
    case 'download':
      downloadImage(image)
      break
    case 'delete':
      confirmDelete(image)
      break
  }
}

// 复制到剪贴板
const copyToClipboard = (text: string) => {
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('复制成功')
  }).catch(err => {
    console.error('复制失败:', err)
    ElMessage.error('复制失败，请手动复制')
  })
}

// 下载图片
const downloadImage = (image: ImageItem) => {
  const a = document.createElement('a')
  a.href = image.url
  a.download = image.name
  a.target = '_blank'
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
}

// 确认删除
const confirmDelete = (image: ImageItem) => {
  ElMessageBox.confirm(
    `确定要删除图片 "${image.name}" 吗？删除后将无法恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await del<any>(`/image/${image.id}`)
      
      if (res.success) {
        ElMessage.success('删除成功')
        loadImages() // 重新加载图片列表
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }).catch(() => {
    // 取消删除，不做任何操作
  })
}

// 组件挂载时加载图片列表
onMounted(() => {
  loadImages()
})
</script>

<style lang="scss" scoped>
.images-container {
  .images-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .left {
        h3 {
          margin: 0;
          font-size: 18px;
          font-weight: 500;
        }
        
        p {
          margin: 5px 0 0 0;
          font-size: 14px;
          color: #909399;
        }
      }
      
      .right {
        .search-input {
          width: 300px;
        }
      }
    }
  }
  
  .images-list {
    min-height: 400px;
    
    .image-item {
      margin-bottom: 20px;
      position: relative;
      height: 100%;
      
      .image-preview {
        cursor: pointer;
        height: 160px;
        overflow: hidden;
        border-radius: 4px;
        
        .el-image {
          width: 100%;
          height: 100%;
          transition: transform 0.3s;
          
          &:hover {
            transform: scale(1.05);
          }
        }
      }
      
      .image-info {
        padding: 10px 0;
        
        .image-name {
          font-size: 14px;
          margin-bottom: 5px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
        
        .image-meta {
          display: flex;
          justify-content: space-between;
          font-size: 12px;
          color: #909399;
        }
      }
      
      .image-actions {
        position: absolute;
        top: 10px;
        right: 10px;
        opacity: 0;
        transition: opacity 0.3s;
        
        .el-button {
          background-color: rgba(255, 255, 255, 0.9);
          border-radius: 50%;
          padding: 6px;
        }
      }
      
      &:hover .image-actions {
        opacity: 1;
      }
    }
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .images-container {
    .images-card {
      .card-header {
        flex-direction: column;
        align-items: flex-start;
        
        .right {
          margin-top: 10px;
          width: 100%;
          
          .search-input {
            width: 100%;
          }
        }
      }
    }
  }
}
</style> 