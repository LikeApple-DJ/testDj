<template>
  <div class="employee-detail-container">
    <div class="page-header">
      <h2>员工详情</h2>
      <div class="header-actions">
        <el-button type="primary" @click="router.push(`/employees/${employee.id}/edit`)">编辑</el-button>
        <el-button @click="router.push('/employees')">返回列表</el-button>
      </div>
    </div>

    <el-card v-if="employee" class="detail-card">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="姓名" :span="1">{{ employee.name }}</el-descriptions-item>
        <el-descriptions-item label="工号" :span="1">{{ employee.employeeNo }}</el-descriptions-item>
        <el-descriptions-item label="部门" :span="1">{{ employee.department }}</el-descriptions-item>
        <el-descriptions-item label="职位" :span="1">{{ employee.position }}</el-descriptions-item>
        <el-descriptions-item label="联系方式" :span="1">{{ employee.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱" :span="1">{{ employee.email }}</el-descriptions-item>
        <el-descriptions-item label="入职日期" :span="1">{{ employee.hireDate }}</el-descriptions-item>
        <el-descriptions-item label="薪资" :span="1">{{ employee.salary ? '¥' + employee.salary : '-' }}</el-descriptions-item>
        <el-descriptions-item label="银行账号" :span="1">{{ employee.bankAccount || '-' }}</el-descriptions-item>
        <el-descriptions-item label="教育背景" :span="1">{{ employee.education || '-' }}</el-descriptions-item>
        <el-descriptions-item label="技能证书" :span="2">{{ employee.skills || '-' }}</el-descriptions-item>
        <el-descriptions-item label="合同到期日" :span="1">{{ employee.contractEndDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="家庭住址" :span="2">{{ employee.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="紧急联系人" :span="1">{{ employee.emergencyContact || '-' }}</el-descriptions-item>
        <el-descriptions-item label="紧急联系电话" :span="1">{{ employee.emergencyPhone || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- Cost Budget Section -->
    <el-card class="cost-card">
      <template #header>
        <div class="card-header">
          <span>成本预算</span>
          <el-button size="small" type="primary" @click="openAddCostDialog">添加成本</el-button>
        </div>
      </template>

      <el-table :data="costBudgets" stripe>
        <el-table-column prop="costType" label="成本类型" width="150">
          <template #default="{ row }">
            <el-tag :type="costTypeTag(row.costType)">{{ costTypeLabel(row.costType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="150">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column prop="year" label="年度" width="100" />
        <el-table-column prop="description" label="说明" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEditCostDialog(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDeleteCost(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Add/Edit Cost Dialog -->
    <el-dialog v-model="showCostDialog" :title="isEditingCost ? '编辑成本预算' : '添加成本预算'" width="500px">
      <el-form :model="costForm" label-width="100px">
        <el-form-item label="成本类型" required>
          <el-select v-model="costForm.costType" style="width: 100%">
            <el-option label="薪资" value="SALARY" />
            <el-option label="培训" value="TRAINING" />
            <el-option label="差旅" value="TRAVEL" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额" required>
          <el-input-number v-model="costForm.amount" :min="0.01" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="年度" required>
          <el-input-number v-model="costForm.year" :min="2020" :max="2030" style="width: 100%" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="costForm.description" type="textarea" rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCostDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveCost" :loading="costSubmitting">{{ isEditingCost ? '保存' : '确认' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getEmployee } from '@/api/employee'
import { listCostBudgets, createCostBudget, updateCostBudget, deleteCostBudget } from '@/api/cost'
import type { Employee, CostBudget } from '@/types/employee'

const route = useRoute()
const router = useRouter()
const employee = ref<Employee | null>(null)
const costBudgets = ref<CostBudget[]>([])
const showCostDialog = ref(false)
const isEditingCost = ref(false)
const editingCostId = ref<number | null>(null)
const costSubmitting = ref(false)
const costForm = ref({ costType: 'SALARY', amount: 0, year: new Date().getFullYear(), description: '' })

const costTypeLabel = (type: string) => {
  const map: Record<string, string> = { SALARY: '薪资', TRAINING: '培训', TRAVEL: '差旅', OTHER: '其他' }
  return map[type] || type
}

const costTypeTag = (type: string) => {
  const map: Record<string, string> = { SALARY: 'success', TRAINING: 'warning', TRAVEL: 'info', OTHER: '' }
  return map[type] || ''
}

const loadCostBudgets = async () => {
  const id = Number(route.params.id)
  if (!id) return
  try {
    const res = await listCostBudgets(id)
    costBudgets.value = res.data
  } catch {
    // ignore
  }
}

const openAddCostDialog = () => {
  isEditingCost.value = false
  editingCostId.value = null
  costForm.value = { costType: 'SALARY', amount: 0, year: new Date().getFullYear(), description: '' }
  showCostDialog.value = true
}

const openEditCostDialog = (row: CostBudget) => {
  isEditingCost.value = true
  editingCostId.value = row.id
  costForm.value = {
    costType: row.costType,
    amount: row.amount,
    year: row.year,
    description: row.description || ''
  }
  showCostDialog.value = true
}

const handleSaveCost = async () => {
  costSubmitting.value = true
  try {
    const employeeId = Number(route.params.id)
    if (isEditingCost.value && editingCostId.value !== null) {
      await updateCostBudget(employeeId, editingCostId.value, costForm.value)
      ElMessage.success('更新成功')
    } else {
      await createCostBudget(employeeId, costForm.value)
      ElMessage.success('添加成功')
    }
    showCostDialog.value = false
    costForm.value = { costType: 'SALARY', amount: 0, year: new Date().getFullYear(), description: '' }
    loadCostBudgets()
  } finally {
    costSubmitting.value = false
  }
}

const handleDeleteCost = async (costId: number) => {
  try {
    await deleteCostBudget(Number(route.params.id), costId)
    ElMessage.success('删除成功')
    loadCostBudgets()
  } catch {
    // ignore
  }
}

onMounted(async () => {
  const id = Number(route.params.id)
  if (id) {
    try {
      const res = await getEmployee(id)
      employee.value = res.data
    } catch {
      router.push('/employees')
    }
    loadCostBudgets()
  }
})
</script>

<style scoped>
.employee-detail-container {
  max-width: 1000px;
  margin: 0 auto;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.header-actions {
  display: flex;
  gap: 8px;
}
.detail-card {
  margin-bottom: 20px;
}
.cost-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
