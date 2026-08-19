<template>
  <div class="app-container">
    <el-header class="app-header">
      <h1>🏢 组织架构管理系统</h1>
    </el-header>
    <el-container class="app-body">
      <el-aside width="260px" class="dept-sidebar">
        <DepartmentTree
          @select="handleDeptSelect"
          @move="handleDeptMove"
        />
      </el-aside>
      <el-main>
        <EmployeeTable
          :dept-id="selectedDeptId"
          @add="handleAddEmployee"
          @transfer="handleTransfer"
          @resign="handleResign"
        />
      </el-main>
    </el-container>

    <EmployeeForm
      v-model:visible="employeeFormVisible"
      @success="handleFormSuccess"
    />

    <TransferDialog
      v-model:visible="transferDialogVisible"
      :employee="selectedEmployee"
      @success="handleTransferSuccess"
    />

    <ResignDialog
      v-model:visible="resignDialogVisible"
      :employee="selectedEmployee"
      @success="handleResignSuccess"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import DepartmentTree from './components/DepartmentTree.vue'
import EmployeeTable from './components/EmployeeTable.vue'
import EmployeeForm from './components/EmployeeForm.vue'
import TransferDialog from './components/TransferDialog.vue'
import ResignDialog from './components/ResignDialog.vue'

const selectedDeptId = ref(null)
const employeeFormVisible = ref(false)
const transferDialogVisible = ref(false)
const resignDialogVisible = ref(false)
const selectedEmployee = ref(null)

const handleDeptSelect = (deptId) => {
  selectedDeptId.value = deptId
}

const handleDeptMove = () => {
  // 部门移动后刷新列表
}

const handleAddEmployee = () => {
  employeeFormVisible.value = true
}

const handleFormSuccess = () => {
  employeeFormVisible.value = false
}

const handleTransfer = (employee) => {
  selectedEmployee.value = employee
  transferDialogVisible.value = true
}

const handleTransferSuccess = () => {
  transferDialogVisible.value = false
}

const handleResign = (employee) => {
  selectedEmployee.value = employee
  resignDialogVisible.value = true
}

const handleResignSuccess = () => {
  resignDialogVisible.value = false
}
</script>

<style scoped>
.app-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background: #409eff;
  color: white;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.app-header h1 {
  margin: 0;
  font-size: 20px;
}

.app-body {
  flex: 1;
  overflow: hidden;
}

.dept-sidebar {
  background: #f5f7fa;
  border-right: 1px solid #e4e7ed;
  overflow-y: auto;
}
</style>
