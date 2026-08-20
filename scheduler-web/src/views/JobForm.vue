<template>
  <el-dialog v-model="visible" :title="isEdit ? '编辑任务' : '新建任务'" width="600px">
    <el-form ref="formRef" :model="form" label-width="120px">
      <el-form-item label="任务名称" required>
        <el-input v-model="form.jobName" />
      </el-form-item>
      <el-form-item label="任务分组">
        <el-input v-model="form.jobGroup" placeholder="default" />
      </el-form-item>
      <el-form-item label="Cron 表达式" required>
        <el-input v-model="form.cronExpression" placeholder="0 0/5 * * * ?" />
      </el-form-item>
      <el-form-item label="执行器处理器">
        <el-input v-model="form.executorHandler" />
      </el-form-item>
      <el-form-item label="执行参数">
        <el-input v-model="form.executorParam" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item label="最大重试次数">
        <el-input-number v-model="form.maxRetryTimes" :min="0" :max="10" />
      </el-form-item>
      <el-form-item label="重试间隔(秒)">
        <el-input-number v-model="form.retryInterval" :min="10" :step="10" />
      </el-form-item>
      <el-form-item label="告警邮箱">
        <el-input v-model="form.alertEmail" placeholder="user@example.com" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { addJob, updateJob } from '../api/job';

const visible = ref(false);
const isEdit = ref(false);
const form = reactive({
  jobName: '', jobGroup: 'default', cronExpression: '',
  executorHandler: '', executorParam: '', maxRetryTimes: 3,
  retryInterval: 60, alertEmail: ''
});

function openAdd() {
  isEdit.value = false;
  Object.assign(form, { jobName: '', jobGroup: 'default', cronExpression: '', executorHandler: '', executorParam: '', maxRetryTimes: 3, retryInterval: 60, alertEmail: '' });
  visible.value = true;
}

function openEdit(row) {
  isEdit.value = true;
  Object.assign(form, row);
  visible.value = true;
}

async function handleSubmit() {
  const api = isEdit.value ? updateJob : addJob;
  await api(form);
  visible.value = false;
  ElMessage.success('保存成功');
}

defineExpose({ openAdd, openEdit });
</script>