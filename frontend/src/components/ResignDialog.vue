<template>
  <el-dialog
    v-model="visible"
    title="办理离职"
    width="500px"
    :close-on-click-modal="false"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="员工姓名">
        <el-input v-model="employeeName" disabled />
      </el-form-item>
      <el-form-item label="离职日期" prop="resignDate">
        <el-date-picker
          v-model="form.resignDate"
          type="date"
          placeholder="请选择离职日期"
          style="width: 100%"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="danger" @click="handleSubmit">确认离职</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { resignEmployee } from '../api/employee'

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

const form = reactive({
  resignDate: ''
})

const rules = {
  resignDate: [{ required: true, message: '请选择离职日期', trigger: 'change' }]
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }

  if (!props.employee) return

  try {
    const res = await resignEmployee(props.employee.id, form)
    if (res.code === 200) {
      ElMessage.success('办理离职成功')
      emit('success')
      resetForm()
    }
  } catch (error) {
    ElMessage.error(error.message || '办理离职失败')
  }
}

const resetForm = () => {
  form.resignDate = ''
}

watch(() => props.visible, (val) => {
  if (val) {
    resetForm()
  }
})
</script>

<style scoped>
</style>
