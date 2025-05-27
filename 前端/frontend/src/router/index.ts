import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { guest: true }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'upload',
        name: 'Upload',
        component: () => import('../views/dashboard/Upload.vue')
      },
      {
        path: 'images',
        name: 'Images',
        component: () => import('../views/dashboard/Images.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/dashboard/Profile.vue'),
        meta: { requiresAuth: true, title: '个人中心' }
      },
      {
        path: 'test-headers',
        name: 'TestHeaders',
        component: () => import('../views/dashboard/TestHeaders.vue'),
        meta: { requiresAuth: true, title: '请求头测试' }
      },
      {
        path: 'test-token',
        name: 'TestToken',
        component: () => import('../views/dashboard/TestToken.vue'),
        meta: { requiresAuth: true, title: 'Token测试' }
      },
      // 管理员路由
      {
        path: 'admin/users',
        name: 'AdminUsers',
        component: () => import('../views/dashboard/admin/Users.vue'),
        meta: { requiresAuth: true, requiresAdmin: true, title: '用户管理' }
      },
      {
        path: 'admin/all-images',
        name: 'AdminAllImages',
        component: () => import('../views/dashboard/admin/AllImages.vue'),
        meta: { requiresAuth: true, requiresAdmin: true, title: '所有图片' }
      },
      {
        path: 'admin/stats',
        name: 'AdminStats',
        component: () => import('../views/dashboard/admin/Stats.vue'),
        meta: { requiresAuth: true, requiresAdmin: true, title: '系统统计' }
      },
      {
        path: 'admin/test-auth',
        name: 'AdminTestAuth',
        component: () => import('../views/dashboard/admin/TestAuth.vue'),
        meta: { requiresAuth: true, requiresAdmin: true, title: '权限测试' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userStr = localStorage.getItem('user')
  let user = null
  
  if (userStr) {
    try {
      user = JSON.parse(userStr)
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }
  
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 需要登录的路由
    if (!token) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else if (to.matched.some(record => record.meta.requiresAdmin)) {
      // 需要管理员权限的路由
      if (user && user.roleType === 1) {
        next()
      } else {
        next({ path: '/dashboard' })
      }
    } else {
      next()
    }
  } else if (to.matched.some(record => record.meta.guest)) {
    // 游客路由
    if (token) {
      next({ path: '/dashboard' })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router 