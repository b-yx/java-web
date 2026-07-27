import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: { noLayout: true }
  },
  {
    path: '/',
    component: () => import('@/views/layout/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/Home.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'dept',
        name: 'Dept',
        component: () => import('@/views/dept/Dept.vue'),
        meta: { title: '部门管理', icon: 'HomeFilled' }
      },
      {
        path: 'emp',
        name: 'Emp',
        component: () => import('@/views/emp/Emp.vue'),
        meta: { title: '员工管理', icon: 'User' }
      },
      {
        path: 'clazz',
        name: 'Clazz',
        component: () => import('@/views/clazz/Clazz.vue'),
        meta: { title: '班级管理', icon: 'Reading' }
      },
      {
        path: 'student',
        name: 'Student',
        component: () => import('@/views/student/Student.vue'),
        meta: { title: '学员管理', icon: 'UserFilled' }
      },
      {
        path: 'report/emp',
        name: 'EmpStat',
        component: () => import('@/views/report/EmpStat.vue'),
        meta: { title: '员工信息统计', icon: 'DataAnalysis' }
      },
      {
        path: 'report/student',
        name: 'StudentStat',
        component: () => import('@/views/report/StudentStat.vue'),
        meta: { title: '学员统计', icon: 'PieChart' }
      },
      {
        path: 'report/class',
        name: 'ClassStat',
        component: () => import('@/views/report/ClassStat.vue'),
        meta: { title: '班级统计', icon: 'Histogram' }
      },
      {
        path: 'log',
        name: 'Log',
        component: () => import('@/views/log/Log.vue'),
        meta: { title: '日志管理', icon: 'Document' }
      },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard - check login
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('tlias_token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})

export default router
