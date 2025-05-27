<template>
  <div class="admin-users-container">
    <el-card class="users-card">
      <template #header>
        <div class="card-header">
          <div class="left">
            <h3>用户管理</h3>
          </div>
          <div class="right">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索用户名/邮箱"
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
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>添加用户
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 用户列表 -->
      <div v-loading="loading" class="users-list">
        <el-table :data="users" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="头像" width="80">
            <template #default="scope">
              <el-avatar :size="40" :src="scope.row.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
            </template>
          </el-table-column>
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="nickname" label="昵称" />
          <el-table-column prop="email" label="邮箱" />
          <el-table-column label="角色">
            <template #default="scope">
              <el-tag :type="scope.row.roleType === 1 ? 'danger' : 'info'">
                {{ scope.row.roleType === 1 ? '管理员' : '普通用户' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态">
            <template #default="scope">
              <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">
                {{ scope.row.status === 0 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="注册时间" />
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button 
                type="warning" 
                size="small" 
                @click="handleToggleStatus(scope.row)"
              >
                {{ scope.row.status === 0 ? '禁用' : '启用' }}
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                @click="handleDelete(scope.row)"
                :disabled="scope.row.roleType === 1"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-if="totalCount > 0"
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totalCount"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>
    
    <!-- 用户编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '添加用户'"
      width="500px"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="userForm.nickname" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="userForm.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="角色" prop="roleType">
          <el-select v-model="userForm.roleType" placeholder="请选择角色">
            <el-option label="普通用户" :value="0" />
            <el-option label="管理员" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="userForm.status" placeholder="请选择状态">
            <el-option label="正常" :value="0" />
            <el-option label="禁用" :value="1" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { get, post, del } from '../../../utils/request'
import type { FormInstance, FormRules } from 'element-plus'

interface UserItem {
  id: number
  username: string
  nickname: string
  email: string
  avatar: string
  roleType: number
  status: number
  createTime: string
  updateTime: string
}

interface UserForm {
  id?: number
  username: string
  nickname: string
  email: string
  password?: string
  roleType: number
  status: number
}

interface ApiResponse<T> {
  code: number
  message: string
  data: T
  success: boolean
}

interface UserListResponse {
  list: UserItem[]
  total: number
  page: number
  size: number
}

// 状态变量
const loading = ref(false)
const submitLoading = ref(false)
const users = ref<UserItem[]>([])
const totalCount = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const userFormRef = ref<FormInstance>()

// 表单数据
const userForm = reactive<UserForm>({
  username: '',
  nickname: '',
  email: '',
  password: '',
  roleType: 0,
  status: 0
})

// 表单验证规则
const userRules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
})

// 加载用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value
    }
    
    const res = await get<ApiResponse<UserListResponse>>('/admin/users', params)
    
    if (res.success) {
      users.value = res.data.list
      totalCount.value = res.data.total
    } else {
      ElMessage.error(res.message || '加载用户列表失败')
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败，请重试')
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  loadUsers()
}

// 处理分页大小变化
const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadUsers()
}

// 处理页码变化
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadUsers()
}

// 处理添加用户
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 处理编辑用户
const handleEdit = (row: UserItem) => {
  isEdit.value = true
  resetForm()
  
  userForm.id = row.id
  userForm.username = row.username
  userForm.nickname = row.nickname
  userForm.email = row.email
  userForm.roleType = row.roleType
  userForm.status = row.status
  
  dialogVisible.value = true
}

// 处理切换用户状态
const handleToggleStatus = (row: UserItem) => {
  const newStatus = row.status === 0 ? 1 : 0
  const statusText = newStatus === 0 ? '启用' : '禁用'
  
  ElMessageBox.confirm(`确定要${statusText}用户 "${row.username}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await post<ApiResponse<null>>('/admin/user/update', {
        id: row.id,
        status: newStatus,
        nickname: row.nickname,
        email: row.email,
        roleType: row.roleType
      })
      
      if (res.success) {
        ElMessage.success(`${statusText}成功`)
        loadUsers()
      } else {
        ElMessage.error(res.message || `${statusText}失败`)
      }
    } catch (error) {
      console.error(`${statusText}失败:`, error)
      ElMessage.error(`${statusText}失败，请重试`)
    }
  }).catch(() => {})
}

// 处理删除用户
const handleDelete = (row: UserItem) => {
  if (row.roleType === 1) {
    ElMessage.warning('不能删除管理员账号')
    return
  }
  
  ElMessageBox.confirm(`确定要删除用户 "${row.username}" 吗？此操作不可恢复！`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await del<ApiResponse<null>>(`/admin/user/${row.id}`)
      
      if (res.success) {
        ElMessage.success('删除成功')
        loadUsers()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }).catch(() => {})
}

// 提交表单
const submitForm = async () => {
  if (!userFormRef.value) return
  
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        let res
        if (isEdit.value) {
          // 编辑用户
          res = await post<ApiResponse<null>>('/admin/user/update', {
            id: userForm.id,
            nickname: userForm.nickname,
            email: userForm.email,
            roleType: userForm.roleType,
            status: userForm.status
          })
        } else {
          // 添加用户
          res = await post<ApiResponse<null>>('/user/register', {
            username: userForm.username,
            password: userForm.password,
            email: userForm.email,
            nickname: userForm.nickname
          })
        }
        
        if (res.success) {
          ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
          dialogVisible.value = false
          loadUsers()
        } else {
          ElMessage.error(res.message || (isEdit.value ? '更新失败' : '添加失败'))
        }
      } catch (error) {
        console.error(isEdit.value ? '更新失败' : '添加失败', error)
        ElMessage.error((isEdit.value ? '更新' : '添加') + '失败，请重试')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  if (userFormRef.value) {
    userFormRef.value.resetFields()
  }
  
  userForm.id = undefined
  userForm.username = ''
  userForm.nickname = ''
  userForm.email = ''
  userForm.password = ''
  userForm.roleType = 0
  userForm.status = 0
}

// 组件挂载时加载用户列表
onMounted(() => {
  loadUsers()
})
</script>

<style lang="scss" scoped>
.admin-users-container {
  .users-card {
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
      }
      
      .right {
        display: flex;
        gap: 10px;
        
        .search-input {
          width: 250px;
        }
      }
    }
  }
  
  .users-list {
    min-height: 400px;
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}
</style> 