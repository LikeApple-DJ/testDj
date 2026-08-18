<template>
  <div class="whitelist-container">
    <div class="page-header">
      <h2>白名单管理</h2>
    </div>

    <el-tabs v-model="activeTab">
      <!-- Import Whitelist -->
      <el-tab-pane label="导入白名单" name="import">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>导入白名单配置</span>
              <el-button size="small" type="primary" @click="showImportDialog = true">
                <el-icon><Plus /></el-icon>新增
              </el-button>
            </div>
          </template>
          <el-table :data="importWhitelist" stripe>
            <el-table-column prop="department" label="部门" />
            <el-table-column prop="allowedAction" label="允许操作" width="150">
              <template #default="{ row }">
                <el-tag>{{ row.allowedAction === 'IMPORT' ? '导入' : row.allowedAction }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="创建时间" width="180" />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button link type="danger" size="small" @click="handleDeleteImport(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- Permission Whitelist -->
      <el-tab-pane label="操作权限白名单" name="permission">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>操作权限白名单配置</span>
              <el-button size="small" type="primary" @click="showPermissionDialog = true">
                <el-icon><Plus /></el-icon>新增
              </el-button>
            </div>
          </template>
          <el-table :data="permissionWhitelist" stripe>
            <el-table-column prop="userId" label="用户ID" />
            <el-table-column prop="permission" label="权限" width="200">
              <template #default="{ row }">
                <el-tag :type="permissionTag(row.permission)">{{ permissionLabel(row.permission) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="创建时间" width="180" />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button link type="danger" size="small" @click="handleDeletePermission(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- Add Import Whitelist Dialog -->
    <el-dialog v-model="showImportDialog" title="新增导入白名单" width="400px">
      <el-form :model="importForm" label-width="100px">
        <el-form-item label="部门" required>
          <el-input v-model="importForm.department" placeholder="部门名称" />
        </el-form-item>
        <el-form-item label="允许操作" required>
          <el-select v-model="importForm.allowedAction" style="width: 100%">
            <el-option label="导入" value="IMPORT" />
            <el-option label="导出" value="EXPORT" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showImportDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddImport" :loading="importSubmitting">确认</el-button>
      </template>
    </el-dialog>

    <!-- Add Permission Whitelist Dialog -->
    <el-dialog v-model="showPermissionDialog" title="新增操作权限" width="400px">
      <el-form :model="permissionForm" label-width="100px">
        <el-form-item label="用户ID" required>
          <el-input v-model="permissionForm.userId" placeholder="用户标识" />
        </el-form-item>
        <el-form-item label="权限" required>
          <el-select v-model="permissionForm.permission" style="width: 100%">
            <el-option label="查看成本预算" value="VIEW_COST" />
            <el-option label="编辑成本预算" value="EDIT_COST" />
            <el-option label="查看全部" value="VIEW_ALL" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPermissionDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddPermission" :loading="permissionSubmitting">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  listImportWhitelist, createImportWhitelist, deleteImportWhitelist,
  listPermissionWhitelist, createPermissionWhitelist, deletePermissionWhitelist
} from '@/api/whitelist'
import type { WhitelistEntry, PermissionWhitelistEntry } from '@/api/whitelist'

const activeTab = ref('import')
const importWhitelist = ref<WhitelistEntry[]>([])
const permissionWhitelist = ref<PermissionWhitelistEntry[]>([])
const showImportDialog = ref(false)
const showPermissionDialog = ref(false)
const importSubmitting = ref(false)
const permissionSubmitting = ref(false)
const importForm = ref({ department: '', allowedAction: 'IMPORT' })
const permissionForm = ref({ userId: '', permission: 'VIEW_COST' })

const permissionLabel = (perm: string) => {
  const map: Record<string, string> = { VIEW_COST: '查看成本预算', EDIT_COST: '编辑成本预算', VIEW_ALL: '查看全部' }
  return map[perm] || perm
}

const permissionTag = (perm: string) => {
  const map: Record<string, string> = { VIEW_COST: 'success', EDIT_COST: 'warning', VIEW_ALL: '' }
  return map[perm] || ''
}

const loadData = async () => {
  try {
    const [importRes, permRes] = await Promise.all([
      listImportWhitelist(),
      listPermissionWhitelist()
    ])
    importWhitelist.value = importRes.data
    permissionWhitelist.value = permRes.data
  } catch {
    // ignore
  }
}

const handleAddImport = async () => {
  importSubmitting.value = true
  try {
    await createImportWhitelist(importForm.value)
    ElMessage.success('添加成功')
    showImportDialog.value = false
    importForm.value = { department: '', allowedAction: 'IMPORT' }
    loadData()
  } finally {
    importSubmitting.value = false
  }
}

const handleDeleteImport = async (id: number) => {
  try {
    await deleteImportWhitelist(id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // ignore
  }
}

const handleAddPermission = async () => {
  permissionSubmitting.value = true
  try {
    await createPermissionWhitelist(permissionForm.value)
    ElMessage.success('添加成功')
    showPermissionDialog.value = false
    permissionForm.value = { userId: '', permission: 'VIEW_COST' }
    loadData()
  } finally {
    permissionSubmitting.value = false
  }
}

const handleDeletePermission = async (id: number) => {
  try {
    await deletePermissionWhitelist(id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // ignore
  }
}

onMounted(loadData)
</script>

<style scoped>
.whitelist-container {
  max-width: 900px;
  margin: 0 auto;
}
.page-header {
  margin-bottom: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>