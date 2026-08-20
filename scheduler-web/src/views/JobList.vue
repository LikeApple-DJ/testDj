<template>
  <div class="job-list">
    <div class="header">
      <h2>任务管理</h2>
      <el-button type="primary" @click="openAddDialog">新建任务</el-button>
    </div>
    <el-table :data="jobList" stripe>
      <el-table-column prop="id" label="任务ID" width="80" />
      <el-table-column prop="jobName" label="任务名称" min-width="150" />
      <el-table-column prop="jobGroup" label="分组" width="120" />
      <el-table-column prop="cronExpression" label="Cron 表达式" width="160" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template #default="scope">
          <el-button size="small" @click="editJob(scope.row)">编辑</el-button>
          <el-button size="small" @click="handleTrigger(scope.row.id)">执行</el-button>
          <el-button
              size="small"
              :type="scope.row.status === 1 ? 'warning' : 'success'"
              @click="handleToggle(scope.row)">
            {{ scope.row.status === 1 ? '暂停' : '恢复' }}
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        layout="total, prev, pager, next"
        @change="loadJobs" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { listJobs, triggerJob, pauseJob, resumeJob, deleteJob } from '../api/job';

const jobList = ref([]);
const total = ref(0);
const query = ref({ page: 1, size: 10 });

async function loadJobs() {
  const res = await listJobs(query.value);
  jobList.value = res.data.records;
  total.value = res.data.total;
}

function handleTrigger(jobId) {
  triggerJob(jobId).then(() => ElMessage.success('已触发执行'));
}

function handleToggle(row) {
  const action = row.status === 1 ? pauseJob(row.id) : resumeJob(row.id);
  action.then(() => { row.status = row.status === 1 ? 0 : 1; });
}

function handleDelete(jobId) {
  ElMessageBox.confirm('确认删除该任务?').then(() => {
    deleteJob(jobId).then(() => loadJobs());
  });
}

onMounted(loadJobs);
</script>