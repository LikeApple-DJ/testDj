<template>
  <div id="app-container">
    <el-container>
      <el-header class="app-header">
        <div class="header-title">
          <el-icon :size="24"><UserFilled /></el-icon>
          <span>人员看板管理系统</span>
        </div>
      </el-header>
      <el-container>
        <el-aside width="220px" class="app-aside">
          <el-menu
            :default-active="route.path"
            router
            background-color="#304156"
            text-color="#bfcbd9"
            active-text-color="#409EFF"
          >
            <el-menu-item index="/employees">
              <el-icon><User /></el-icon>
              <span>员工管理</span>
            </el-menu-item>
            <el-menu-item index="/import">
              <el-icon><Upload /></el-icon>
              <span>批量导入</span>
            </el-menu-item>
            <el-menu-item index="/whitelist">
              <el-icon><Lock /></el-icon>
              <span>白名单管理</span>
            </el-menu-item>
            <el-menu-item index="/weather">
              <el-icon><PartlyCloudy /></el-icon>
              <span>天气预报</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main class="app-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import { PartlyCloudy } from '@element-plus/icons-vue'
const route = useRoute()
</script>

<style>
html, body, #app, #app-container {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', sans-serif;
}
.app-header {
  background-color: #304156;
  color: #fff;
  display: flex;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}
.header-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
}
.app-aside {
  background-color: #304156;
  min-height: calc(100vh - 60px);
}
.app-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
