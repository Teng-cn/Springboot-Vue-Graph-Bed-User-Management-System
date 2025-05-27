<el-menu-item index="/dashboard/profile">
  <el-icon><User /></el-icon>
  <span>个人中心</span>
</el-menu-item>

<el-menu-item index="/dashboard/test-headers">
  <el-icon><Document /></el-icon>
  <span>测试请求头</span>
</el-menu-item>

<el-menu-item index="/dashboard/test-token">
  <el-icon><Key /></el-icon>
  <span>Token测试</span>
</el-menu-item> 

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { 
  House, 
  Upload, 
  Picture, 
  User, 
  Document,
  Key
} from '@element-plus/icons-vue'
</script> 