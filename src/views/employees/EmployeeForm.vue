<template>
  <div class="employee-form-container">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑员工' : '新增员工' }}</h2>
    </div>

    <el-card>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        style="max-width: 800px"
      >
        <el-divider content-position="left">基础信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工号" prop="employeeNo">
              <el-input v-model="form.employeeNo" placeholder="请输入工号" :disabled="isEdit" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="部门" prop="department">
              <el-input v-model="form.department" placeholder="请输入部门" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位" prop="position">
              <el-input v-model="form.position" placeholder="请输入职位" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系方式" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入职日期" prop="hireDate">
              <el-date-picker v-model="form.hireDate" type="date" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">财务信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="薪资" prop="salary">
              <el-input-number v-model="form.salary" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="银行账号" prop="bankAccount">
              <el-input v-model="form.bankAccount" placeholder="请输入银行账号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">人事信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="教育背景" prop="education">
              <el-input v-model="form.education" placeholder="如：本科/硕士" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="技能证书" prop="skills">
              <el-input v-model="form.skills" placeholder="技能/证书" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="合同到期日" prop="contractEndDate">
              <el-date-picker v-model="form.contractEndDate" type="date" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">个人信息</el-divider>
        <el-form-item label="家庭住址" prop="address">
          <el-input v-model="form.address" type="textarea" rows="2" placeholder="请输入家庭住址" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="form.emergencyContact" placeholder="联系人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系电话" prop="emergencyPhone">
              <el-input v-model="form.emergencyPhone" placeholder="联系人电话" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '保存修改' : '创建员工' }}
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createEmployee, getEmployee, updateEmployee } from '@/api/employee'
import type { EmployeeCreateRequest } from '@/types/employee'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const formRef = ref()
const submitting = ref(false)

const form = ref<EmployeeCreateRequest>({
  name: '',
  employeeNo: '',
  department: '',
  position: '',
  phone: '',
  email: '',
  hireDate: '',
  salary: undefined,
  bankAccount: '',
  education: '',
  skills: '',
  contractEndDate: '',
  address: '',
  emergencyContact: '',
  emergencyPhone: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  employeeNo: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  department: [{ required: true, message: '请输入部门', trigger: 'blur' }],
  hireDate: [{ required: true, message: '请选择入职日期', trigger: 'change' }]
}

const formatDate = (date: Date | string): string => {
  if (!date) return ''
  if (typeof date === 'string') return date
  return date.toISOString().split('T')[0]
}

const loadEmployee = async () => {
  const id = Number(route.params.id)
  if (!id) return
  try {
    const res = await getEmployee(id)
    const emp = res.data
    form.value = {
      name: emp.name,
      employeeNo: emp.employeeNo,
      department: emp.department,
      position: emp.position || '',
      phone: emp.phone || '',
      email: emp.email || '',
      hireDate: emp.hireDate,
      salary: emp.salary || undefined,
      bankAccount: emp.bankAccount || '',
      education: emp.education || '',
      skills: emp.skills || '',
      contractEndDate: emp.contractEndDate || '',
      address: emp.address || '',
      emergencyContact: emp.emergencyContact || '',
      emergencyPhone: emp.emergencyPhone || ''
    }
  } catch (e) {
    ElMessage.error('加载员工信息失败')
    router.push('/employees')
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const payload = {
      ...form.value,
      hireDate: formatDate(form.value.hireDate),
      contractEndDate: form.value.contractEndDate ? formatDate(form.value.contractEndDate) : undefined
    }

    if (isEdit.value) {
      await updateEmployee(Number(route.params.id), payload)
      ElMessage.success('修改成功')
    } else {
      await createEmployee(payload)
      ElMessage.success('创建成功')
    }
    router.push('/employees')
  } catch (e) {
    // error handled by interceptor
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (isEdit.value) {
    loadEmployee()
  }
})
</script>

<style scoped>
.employee-form-container {
  max-width: 900px;
  margin: 0 auto;
}
.page-header {
  margin-bottom: 16px;
}
</style>