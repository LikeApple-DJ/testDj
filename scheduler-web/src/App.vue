<template>
  <div id="app">
    <el-container style="min-height: 100vh;">
      <el-aside width="220px" style="background: #304156;">
        <el-menu
            :default-active="currentRoute"
            router
            background-color="#304156"
            text-color="#bfcbd9"
            active-text-color="#409EFF">
          <el-menu-item index="/weather">
            <el-icon style="margin-right: 6px;"><Cloudy /></el-icon>
            天气看板
          </el-menu-item>
          <el-menu-item index="/jobs">
            <el-icon style="margin-right: 6px;"><List /></el-icon>
            任务管理
          </el-menu-item>
          <el-menu-item index="/logs">
            <el-icon style="margin-right: 6px;"><Document /></el-icon>
            执行日志
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { Cloudy, List, Document } from '@element-plus/icons-vue';

const route = useRoute();
const currentRoute = computed(() => route.path);
</script>