<template>
  <div class="job-log">
    <h2>执行日志</h2>
    <el-form :inline="true" :model="query" style="margin-bottom: 16px;">
      <el-form-item label="任务ID">
        <el-input v-model="query.jobId" placeholder="输入任务ID" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadLogs">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="logList" stripe>
      <el-table-column prop="id" label="日志ID" width="80" />
      <el-table-column prop="jobId" label="任务ID" width="80" />
      <el-table-column prop="triggerTime" label="触发时间" width="180" />
      <el-table-column prop="finishTime" label="完成时间" width="180" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="statusType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="result" label="执行结果" min-width="200" />
      <el-table-column prop="retryTimes" label="重试次数" width="80" />
    </el-table>
    <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        layout="total, prev, pager, next"
        @change="loadLogs" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { listJobLogs } from '../api/job';

const logList = ref([]);
const total = ref(0);
const query = ref({ page: 1, size: 10, jobId: '' });

async function loadLogs() {
  const res = await listJobLogs(query.value);
  logList.value = res.data.records;
  total.value = res.data.total;
}

function statusType(status) {
  return ['', 'success', 'danger', 'warning'][status] || 'info';
}

function statusText(status) {
  return ['运行中', '成功', '失败', '超时'][status] || '未知';
}

onMounted(loadLogs);
</script>