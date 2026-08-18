<template>
  <div class="import-container">
    <div class="page-header">
      <h2>批量导入员工</h2>
    </div>

    <el-card class="import-card">
      <template #header>
        <span>上传文件</span>
      </template>

      <el-upload
        drag
        action="#"
        :auto-upload="false"
        :on-change="handleFileChange"
        :limit="1"
        accept=".csv,.xlsx,.xls"
      >
        <el-icon class="el-icon--upload" :size="48"><UploadFilled /></el-icon>
        <div class="el-upload__text">
          将文件拖拽到此处，或 <em>点击选择文件</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持 CSV、Excel (.xlsx/.xls) 格式，单文件不超过 10MB
          </div>
        </template>
      </el-upload>

      <div class="import-actions" v-if="selectedFile">
        <el-button type="primary" @click="handleImport" :loading="importing">
          开始导入
        </el-button>
        <el-button @click="selectedFile = null">取消</el-button>
      </div>
    </el-card>

    <!-- Import Result -->
    <el-card v-if="importResult" class="result-card">
      <template #header>
        <span>导入结果</span>
      </template>
      <el-result
        :icon="importResult.failedRows === 0 ? 'success' : 'warning'"
        :title="`导入完成：成功 ${importResult.successRows} 条，失败 ${importResult.failedRows} 条`"
        :sub-title="`共处理 ${importResult.totalRows} 条数据`"
      >
        <template #extra>
          <el-button type="primary" @click="selectedFile = null; importResult = null">继续导入</el-button>
          <el-button @click="$router.push('/employees')">查看员工列表</el-button>
        </template>
      </el-result>

      <!-- Error Details -->
      <el-table v-if="importResult.errors.length > 0" :data="importResult.errors" stripe style="margin-top: 16px">
        <el-table-column prop="row" label="行号" width="80" />
        <el-table-column prop="column" label="字段" width="120" />
        <el-table-column prop="message" label="错误信息" />
      </el-table>
    </el-card>

    <!-- Download Template -->
    <el-card class="template-card">
      <template #header>
        <span>导入模板</span>
      </template>
      <p>请按照以下格式准备导入文件：</p>
      <el-table :data="templateData" border stripe>
        <el-table-column prop="field" label="字段" />
        <el-table-column prop="required" label="必填" width="80" />
        <el-table-column prop="description" label="说明" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { importEmployees } from '@/api/employee'
import type { ImportResult } from '@/types/employee'

const selectedFile = ref<File | null>(null)
const importing = ref(false)
const importResult = ref<ImportResult | null>(null)

const templateData = [
  { field: '姓名', required: '是', description: '员工姓名' },
  { field: '工号', required: '是', description: '员工唯一工号' },
  { field: '部门', required: '是', description: '所属部门' },
  { field: '职位', required: '否', description: '职位名称' },
  { field: '入职日期', required: '是', description: '格式: yyyy-MM-dd' },
  { field: '电话', required: '否', description: '联系方式' },
  { field: '邮箱', required: '否', description: '电子邮箱' },
  { field: '薪资', required: '否', description: '数字金额' },
  { field: '银行账号', required: '否', description: '银行账号' },
  { field: '教育背景', required: '否', description: '学历信息' },
  { field: '技能证书', required: '否', description: '技能或证书' },
  { field: '合同到期日', required: '否', description: '格式: yyyy-MM-dd' },
  { field: '家庭住址', required: '否', description: '地址信息' },
  { field: '紧急联系人', required: '否', description: '联系人姓名' },
  { field: '紧急联系电话', required: '否', description: '联系人电话' }
]

const handleFileChange = (uploadFile: any) => {
  selectedFile.value = uploadFile.raw
  importResult.value = null
}

const handleImport = async () => {
  if (!selectedFile.value) return
  importing.value = true
  try {
    const res = await importEmployees(selectedFile.value)
    importResult.value = res.data
    selectedFile.value = null
  } catch {
    // handled by interceptor
  } finally {
    importing.value = false
  }
}
</script>

<style scoped>
.import-container {
  max-width: 900px;
  margin: 0 auto;
}
.page-header {
  margin-bottom: 16px;
}
.import-card, .result-card, .template-card {
  margin-bottom: 20px;
}
.import-actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
  justify-content: center;
}
</style>