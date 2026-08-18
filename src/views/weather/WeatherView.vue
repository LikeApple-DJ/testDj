<template>
  <div class="weather-container">
    <div class="weather-header">
      <h1>
        <span style="margin-right: 8px">🌤</span>
        {{ weatherData?.city || '杭州' }}未来七天天气预报
      </h1>
      <div class="header-right">
        <el-tag v-if="weatherData?.updateDate" type="info" effect="plain">
          更新: {{ weatherData.updateDate }}
        </el-tag>
        <el-tag v-if="weatherData?.fallback" type="warning" effect="dark" style="margin-left: 8px">
          ⚠️ 模拟数据
        </el-tag>
      </div>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="status-center">
      <el-icon class="is-loading" :size="40">
        <Loading />
      </el-icon>
      <p class="status-text">正在获取天气数据...</p>
    </div>

    <!-- 错误状态 -->
    <el-alert
      v-else-if="error"
      :title="error"
      type="error"
      show-icon
      :closable="false"
    />

    <!-- 空数据状态 -->
    <el-empty
      v-else-if="weatherData && weatherData.forecasts?.length === 0"
      description="暂无天气数据，请稍后重试"
    />

    <!-- 天气数据 -->
    <template v-else-if="weatherData?.forecasts?.length">
      <!-- 今日天气概览 -->
      <el-card class="today-card" :body-style="{ padding: '24px' }" v-if="today">
        <div class="today-weather">
          <div class="today-icon">{{ today.weatherIcon }}</div>
          <div class="today-info">
            <div class="today-temp">
              <span class="temp-max">{{ Math.round(today.maxTemp) }}°C</span>
              <span class="temp-sep">/</span>
              <span class="temp-min">{{ Math.round(today.minTemp) }}°C</span>
            </div>
            <div class="today-desc">{{ today.weatherDesc }}</div>
            <div class="today-meta">
              <span>降水概率: {{ Math.round(today.precipitationProbability) }}%</span>
              <span style="margin-left: 20px">风速: {{ Math.round(today.windSpeed) }} km/h</span>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 7天预报（从第二天开始，第一天已在今日卡片展示） -->
      <el-row :gutter="16" class="forecast-row">
        <el-col
          v-for="(day, index) in weatherData.forecasts"
          :key="day.date"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
          :xl="4"
          style="margin-bottom: 16px"
        >
          <el-card
            :class="['forecast-card', { 'is-today': index === 0 }]"
            shadow="hover"
            :body-style="{ padding: '16px' }"
          >
            <div class="forecast-day">
              <div class="day-name">{{ getDayName(day.date, index) }}</div>
              <div class="day-date">{{ formatDate(day.date) }}</div>
            </div>
            <div class="forecast-icon">{{ day.weatherIcon || '❓' }}</div>
            <div class="forecast-temp">
              <span class="temp-max">{{ Math.round(day.maxTemp) }}°</span>
              <span class="temp-sep">/</span>
              <span class="temp-min">{{ Math.round(day.minTemp) }}°</span>
            </div>
            <div class="forecast-desc">{{ day.weatherDesc || '未知' }}</div>
            <div class="forecast-detail">
              <el-tooltip content="降水概率" placement="top">
                <span class="detail-item">
                  <el-icon><WaterLevel /></el-icon>
                  {{ Math.round(day.precipitationProbability) }}%
                </span>
              </el-tooltip>
              <el-tooltip content="风速" placement="top">
                <span class="detail-item">
                  <el-icon><WindPower /></el-icon>
                  {{ Math.round(day.windSpeed) }} km/h
                </span>
              </el-tooltip>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import type { AxiosError } from 'axios'
import { get7DayForecast } from '@/api/weather'
import type { WeatherResponse, DailyForecast } from '@/types/weather'
import { Loading, WaterLevel, WindPower } from '@element-plus/icons-vue'

const weatherData = ref<WeatherResponse | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)

const today = computed<DailyForecast | undefined>(() => weatherData.value?.forecasts?.[0])

const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']

function getDayName(dateStr: string, index: number): string {
  if (index === 0) return '今天'
  if (index === 1) return '明天'
  if (index === 2) return '后天'
  const date = new Date(dateStr)
  return weekDays[date.getDay()]
}

function formatDate(dateStr: string): string {
  try {
    const date = new Date(dateStr)
    if (isNaN(date.getTime())) {
      // 如果是 yyyy-MM-dd 格式，直接拆分
      const parts = dateStr.split('-')
      if (parts.length === 3) return `${parts[1]}/${parts[2]}`
      return dateStr
    }
    return `${String(date.getMonth() + 1).padStart(2, '0')}/${String(date.getDate()).padStart(2, '0')}`
  } catch {
    return dateStr
  }
}

// 请求取消控制
const abortController = ref<AbortController | null>(null)

async function fetchWeather() {
  // 取消上一次未完成的请求
  if (abortController.value) {
    abortController.value.abort()
  }
  abortController.value = new AbortController()

  loading.value = true
  error.value = null
  try {
    const res = await get7DayForecast('杭州')
    weatherData.value = res.data
  } catch (e: unknown) {
    if (e instanceof DOMException && e.name === 'AbortError') {
      // 请求被取消，不处理
      return
    }
    const axiosError = e as AxiosError<{ error?: string }>
    error.value = axiosError.response?.data?.error || axiosError.message || '获取天气数据失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

onMounted(fetchWeather)

onUnmounted(() => {
  if (abortController.value) {
    abortController.value.abort()
  }
})
</script>

<style scoped>
.weather-container {
  max-width: 1200px;
  margin: 0 auto;
}

.weather-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 12px;
}

.weather-header h1 {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
}

.status-center {
  text-align: center;
  padding: 80px 0;
}

.status-text {
  margin-top: 16px;
  color: #909399;
}

.today-card {
  margin-bottom: 24px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.today-card :deep(.el-card__body) {
  padding: 24px !important;
}

.today-weather {
  display: flex;
  align-items: center;
  gap: 32px;
}

.today-icon {
  font-size: 72px;
  line-height: 1;
}

.today-info {
  flex: 1;
}

.today-temp {
  font-size: 48px;
  font-weight: 300;
  line-height: 1.1;
}

.today-temp .temp-max {
  font-weight: 600;
}

.today-temp .temp-sep {
  margin: 0 8px;
  opacity: 0.6;
}

.today-temp .temp-min {
  opacity: 0.8;
}

.today-desc {
  font-size: 18px;
  margin-top: 4px;
  opacity: 0.9;
}

.today-meta {
  font-size: 14px;
  margin-top: 8px;
  opacity: 0.7;
}

.forecast-row {
  margin-top: 8px;
}

.forecast-card {
  border-radius: 10px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.forecast-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.forecast-card.is-today {
  border: 2px solid #409eff;
}

.forecast-day {
  text-align: center;
  margin-bottom: 12px;
}

.day-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.day-date {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.forecast-icon {
  text-align: center;
  font-size: 48px;
  margin: 8px 0;
}

.forecast-temp {
  text-align: center;
  font-size: 18px;
  margin-bottom: 4px;
}

.forecast-temp .temp-max {
  font-weight: 600;
  color: #e6a23c;
}

.forecast-temp .temp-sep {
  margin: 0 4px;
  color: #dcdfe6;
}

.forecast-temp .temp-min {
  color: #409eff;
}

.forecast-desc {
  text-align: center;
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
}

.forecast-detail {
  display: flex;
  justify-content: center;
  gap: 16px;
  font-size: 12px;
  color: #909399;
}

.detail-item {
  display: inline-flex;
  align-items: center;
  gap: 3px;
}
</style>
