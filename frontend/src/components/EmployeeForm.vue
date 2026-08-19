<template>
  <el-dialog
    v-model="visible"
    title="新增员工"
    width="500px"
    :close-on-click-modal="false"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" placeholder="请输入姓名" />
      </el-form-item>
      <el-form-item label="工号" prop="employeeNo">
        <el-input
          v-model="form.employeeNo"
          placeholder="请输入工号"
          @blur="handleBlur('employeeNo')"
          :class="{ 'is-error': errors.employeeNo }"
        />
        <div v-if="errors.employeeNo" class="error-text">{{ errors.employeeNo }}</div>
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input
          v-model="form.phone"
          placeholder="请输入手机号"
          @blur="handleBlur('phone')"
          :class="{ 'is-error': errors.phone }"
        />
        <div v-if="errors.phone" class="error-text">{{ errors.phone }}</div>
      </el-form-item>
      <el-form-item label="所属部门" prop="deptId">
        <el-select v-model="form.deptId" placeholder="请选择部门" style="width: 100%">
          <el-option
            v-for="dept in deptOptions"
            :key="dept.id"
            :label="dept.name"
            :value="dept.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="职位" prop="position">
        <el-input v-model="form.position" placeholder="请输入职位" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { createEmployee, checkField } from '../api/employee'
import { getDepartmentTree } from '../api/department'

const props = defineProps({
  visible: Boolean
})

const emit = defineEmits(['update:visible', 'success'])

const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const formRef = ref(null)
const deptOptions = ref([])
const errors = reactive({
  employeeNo: '',
  phone: ''
})

const form = reactive({
  name: '',
  employeeNo: '',
  phone: '',
  deptId: null,
  position: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  employeeNo: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  deptId: [{ required: true, message: '请选择部门', trigger: 'change' }],
  position: [{ required: true, message: '请输入职位', trigger: 'blur' }]
}

const loadDepts = async () => {
  try {
    const res = await getDepartmentTree()
    if (res.code === 200) {
      deptOptions.value = flattenTree(res.data || [])
    }
  } catch (error) {
    console.error('加载部门失败', error)
  }
}

const flattenTree = (nodes) => {
  const result = []
  const traverse = (list) => {
    for (const node of list) {
      result.push({ id: node.id, name: node.name })
      if (node.children && node.children.length > 0) {
        traverse(node.children)
      }
    }
  }
  traverse(nodes)
  return result
}

const handleBlur = async (field) => {
  const value = form[field]
  if (!value) return
  try {
    const res = await checkField(field === 'employeeNo' ? 'employeeNo' : 'phone', value)
    if (res.code === 200 && res.data.isExist) {
      errors[field] = field === 'employeeNo' ? '工号已存在' : '手机号已存在'
    } else {
      errors[field] = ''
    }
  } catch (error) {
    console.error('校验失败', error)
  }
}

const handleSubmit = async () => {
  if (errors.employeeNo || errors.phone) {
    ElMessage.warning('请修正表单错误后再提交')
    return
  }

  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }

  try {
    const res = await createEmployee(form)
    if (res.code === 200) {
      ElMessage.success('新增员工成功')
      emit('success')
      resetForm()
    }
  } catch (error) {
    ElMessage.error(error.message || '新增员工失败')
  }
}

const resetForm = () => {
  form.name = ''
  form.employeeNo = ''
  form.phone = ''
  form.deptId = null
  form.position = ''
  errors.employeeNo = ''
  errors.phone = ''
}

watch(() => props.visible, (val) => {
  if (val) {
    loadDepts()
  }
})

onMounted(() => {
  loadDepts()
})
</script>

<style scoped>
.error-text {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
}

.is-error :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #f56c6c inset;
}
</style>
