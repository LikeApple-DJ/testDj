<template>
  <el-dialog
    v-model="visible"
    title="人员调动"
    width="500px"
    :close-on-click-modal="false"
  >
    <el-alert
      title="调动后，该员工相关的审批流/权限将发生变化，确认调动？"
      type="warning"
      :closable="false"
      show-icon
      style="margin-bottom: 20px"
    />
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="员工姓名">
        <el-input v-model="employeeName" disabled />
      </el-form-item>
      <el-form-item label="目标部门" prop="newDeptId">
        <el-select v-model="form.newDeptId" placeholder="请选择目标部门" style="width: 100%">
          <el-option
            v-for="dept in deptOptions"
            :key="dept.id"
            :label="dept.name"
            :value="dept.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="新职位" prop="newPosition">
        <el-input v-model="form.newPosition" placeholder="请输入新职位" />
      </el-form-item>
      <el-form-item label="调动原因">
        <el-input v-model="form.reason" type="textarea" :rows="3" placeholder="请输入调动原因（选填）" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定调动</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { transferEmployee } from '../api/employee'
import { getDepartmentTree } from '../api/department'

const props = defineProps({
  visible: Boolean,
  employee: Object
})

const emit = defineEmits(['update:visible', 'success'])

const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const employeeName = computed(() => props.employee?.name || '')

const formRef = ref(null)
const deptOptions = ref([])

const form = reactive({
  newDeptId: null,
  newPosition: '',
  reason: ''
})

const rules = {
  newDeptId: [{ required: true, message: '请选择目标部门', trigger: 'change' }],
  newPosition: [{ required: true, message: '请输入新职位', trigger: 'blur' }]
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

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }

  if (!props.employee) return

  try {
    const res = await transferEmployee(props.employee.id, form)
    if (res.code === 200) {
      ElMessage.success(res.msg || '调动成功')
      emit('success')
      resetForm()
    }
  } catch (error) {
    ElMessage.error(error.message || '调动失败')
  }
}

const resetForm = () => {
  form.newDeptId = null
  form.newPosition = ''
  form.reason = ''
}

watch(() => props.visible, (val) => {
  if (val) {
    loadDepts()
    resetForm()
  }
})
</script>

<style scoped>
</style>
