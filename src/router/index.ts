import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/employees'
    },
    {
      path: '/employees',
      name: 'Employees',
      component: () => import('@/views/employees/EmployeeList.vue'),
      meta: { title: '员工管理' }
    },
    {
      path: '/employees/new',
      name: 'EmployeeCreate',
      component: () => import('@/views/employees/EmployeeForm.vue'),
      meta: { title: '新增员工' }
    },
    {
      path: '/employees/:id/edit',
      name: 'EmployeeEdit',
      component: () => import('@/views/employees/EmployeeForm.vue'),
      meta: { title: '编辑员工' }
    },
    {
      path: '/employees/:id',
      name: 'EmployeeDetail',
      component: () => import('@/views/employees/EmployeeDetail.vue'),
      meta: { title: '员工详情' }
    },
    {
      path: '/import',
      name: 'Import',
      component: () => import('@/views/import/ImportView.vue'),
      meta: { title: '批量导入' }
    },
    {
      path: '/whitelist',
      name: 'Whitelist',
      component: () => import('@/views/whitelist/WhitelistView.vue'),
      meta: { title: '白名单管理' }
    }
  ]
})

export default router