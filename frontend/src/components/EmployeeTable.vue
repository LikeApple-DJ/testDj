<template>
  <div class="employee-container">
    <div class="toolbar">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="所属部门">
          <el-select v-model="queryForm.deptId" placeholder="选择部门" clearable @change="handleSearch">
            <el-option
              v-for="dept in deptOptions"
              :key="dept.id"
              :label="dept.name"
              :value="dept.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="选择状态" clearable @change="handleSearch">
            <el-option label="在职" :value="1" />
            <el-option label="离职" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon> 查询
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="success" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增员工
      </el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column type="index" width="50" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="employeeNo" label="工号" width="120" />
      <el-table-column prop="phone" label="手机号" width="150" />
      <el-table-column prop="position" label="职位" width="150" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '在职' : '离职' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button
            type="primary"
            size="small"
            :disabled="row.status === 0"
            @click="handleTransfer(row)"
          >
            调动
          </el-button>
          <el-button
            type="danger"
            size="small"
            :disabled="row.status === 0"
            @click="handleResign(row)"
          >
            离职
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getEmployeeList, getDepartmentTree } from '../api/employee'
import { getDepartmentTree as getDeptTree } from '../api/department'

const props = defineProps({
  deptId: Number
})

const emit = defineEmits(['add', 'transfer', 'resign'])

const loading = ref(false)
const tableData = ref([])
const deptOptions = ref([])
const queryForm = ref({
  deptId: null,
  status: null
})

const pagination = ref({
  page: 1,
  size: 10,
  total: 0
})

const loadDeptOptions = async () => {
  try {
    const res = await getDeptTree()
    if (res.code === 200) {
      deptOptions.value = flattenTree(res.data || [])
    }
  } catch (error) {
    console.error('加载部门选项失败', error)
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

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.value.page,
      size: pagination.value.size,
      ...queryForm.value
    }
    if (props.deptId) {
      params.deptId = props.deptId
    }
    const res = await getEmployeeList(params)
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.value.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error(error.message || '加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.page = 1
  fetchData()
}

const handleReset = () => {
  queryForm.value = {
    deptId: null,
    status: null
  }
  handleSearch()
}

const handleSizeChange = (size) => {
  pagination.value.size = size
  fetchData()
}

const handleCurrentChange = (page) => {
  pagination.value.page = page
  fetchData()
}

const handleAdd = () => {
  emit('add')
}

const handleTransfer = (row) => {
  emit('transfer', row)
}

const handleResign = (row) => {
  emit('resign', row)
}

watch(() => props.deptId, () => {
  fetchData()
})

onMounted(() => {
  loadDeptOptions()
  fetchData()
})

defineExpose({ refresh: fetchData })
</script>

<style scoped>
.employee-container {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
