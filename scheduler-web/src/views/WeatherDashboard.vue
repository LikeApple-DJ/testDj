<template>
  <div class="weather-dashboard">
    <!-- 头部标题 -->
    <div class="dashboard-header">
      <div class="header-left">
        <h1>
          <el-icon style="vertical-align: middle; margin-right: 8px;"><Cloudy /></el-icon>
          杭州 · 未来7天天气看板
        </h1>
        <p class="subtitle">🗓️ 2026年8月20日（周四）~ 8月26日（周三）</p>
      </div>
      <div class="header-right">
        <el-tag type="danger" size="large" effect="dark">
          🔥 高温预警 · 注意防暑
        </el-tag>
      </div>
    </div>

    <!-- 今日概览 -->
    <el-card class="today-card" shadow="always">
      <div class="today-header">
        <el-statistic title="今日日期" :value="'2026年8月20日 周四'" />
      </div>
      <div class="today-body">
        <div class="today-weather-icon">
          <span style="font-size: 72px;">☀️</span>
        </div>
        <div class="today-temp">
          <span class="temp-high">35°</span>
          <span class="temp-sep">/</span>
          <span class="temp-low">27°</span>
          <span class="temp-unit">℃</span>
        </div>
        <div class="today-desc">
          <p>☀️ 晴间多云，午后局部雷阵雨</p>
          <p>💧 湿度 78% · 🌬️ 东南风 3-4级</p>
        </div>
      </div>
    </el-card>

    <!-- 7天预报卡片 -->
    <h2 class="section-title">📅 每日详细预报</h2>
    <el-row :gutter="16">
      <el-col :span="24" :md="12" :lg="8" v-for="day in forecast" :key="day.date">
        <el-card class="day-card" :class="day.weatherClass" shadow="hover">
          <div class="day-header">
            <span class="day-name">{{ day.weekday }}</span>
            <span class="day-date">{{ day.date }}</span>
          </div>
          <div class="day-weather-icon">{{ day.icon }}</div>
          <div class="day-temp-range">
            <span class="high">{{ day.high }}°</span>
            <span class="sep">/</span>
            <span class="low">{{ day.low }}°</span>
            <span class="unit">℃</span>
          </div>
          <div class="day-weather-desc">{{ day.weather }}</div>
          <el-divider />
          <div class="day-details">
            <p>🌡️ 体感温度：{{ day.feelsLike }}℃</p>
            <p>💧 湿度：{{ day.humidity }}</p>
            <p>🌬️ 风力：{{ day.wind }}</p>
            <p>☔ 降水概率：{{ day.precipitation }}</p>
          </div>
          <div class="day-advice">
            <el-tag :type="day.adviceTag" size="small" effect="plain">
              {{ day.advice }}
            </el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 穿衣指南 -->
    <h2 class="section-title">👗 穿衣指南</h2>
    <el-card class="advice-card" shadow="hover">
      <el-row :gutter="24">
        <el-col :span="24" :md="8">
          <div class="advice-item">
            <el-icon class="advice-icon" size="32"><Sunny /></el-icon>
            <h3>☀️ 白天穿搭</h3>
            <p>短袖T恤、短裤/薄裙、遮阳帽、太阳镜</p>
            <p>推荐材质：棉麻、真丝等透气面料</p>
          </div>
        </el-col>
        <el-col :span="24" :md="8">
          <div class="advice-item">
            <el-icon class="advice-icon" size="32"><Moon /></el-icon>
            <h3>🌙 早晚温差</h3>
            <p>早晚温差约 8℃，建议备一件薄外套</p>
            <p>推荐：防晒衫、薄款开衫、亚麻衬衫</p>
          </div>
        </el-col>
        <el-col :span="24" :md="8">
          <div class="advice-item">
            <el-icon class="advice-icon" size="32"><Umbrella /></el-icon>
            <h3>☔ 防雨装备</h3>
            <p>午后雷阵雨频繁，建议随身携带雨伞</p>
            <p>推荐：折叠伞、防水鞋套</p>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 活动推荐 -->
    <h2 class="section-title">🎯 活动推荐</h2>
    <el-row :gutter="16">
      <el-col :span="24" :md="12" :lg="8" v-for="act in activities" :key="act.title">
        <el-card class="activity-card" shadow="hover">
          <div class="activity-header">
            <span class="activity-icon">{{ act.icon }}</span>
            <h3>{{ act.title }}</h3>
          </div>
          <p class="activity-time">⏰ {{ act.time }}</p>
          <p class="activity-desc">{{ act.desc }}</p>
          <el-tag :type="act.tagType" size="small">{{ act.tag }}</el-tag>
        </el-card>
      </el-col>
    </el-row>

    <!-- 风险提示 -->
    <h2 class="section-title">⚠️ 风险提示</h2>
    <el-card class="risk-card" shadow="hover">
      <el-alert
        title="🔥 高温预警：8月20日~24日持续高温"
        type="warning"
        :closable="false"
        show-icon
        description="最高气温可达35~38℃，紫外线指数高（8级），请做好防暑防晒措施。建议10:00~16:00避免长时间户外活动。"
      />
      <el-alert
        title="⛈️ 午后雷阵雨：8月21日~24日、26日"
        type="warning"
        :closable="false"
        show-icon
        description="午后至傍晚易出现短时强降雨和雷电，户外活动时注意天气变化，雷雨时远离开阔水域和高地。"
        style="margin-top: 12px;"
      />
      <el-alert
        title="🌬️ 台风动态：关注8月下旬台风路径"
        type="info"
        :closable="false"
        show-icon
        description="8月下旬是台风活跃期，请关注最新天气预报，如遇台风预警请避免前往沿海景区。"
        style="margin-top: 12px;"
      />
    </el-card>

    <!-- 安全提示 -->
    <h2 class="section-title">🛡️ 安全出行建议</h2>
    <el-card class="safety-card" shadow="hover">
      <el-timeline>
        <el-timeline-item
          v-for="tip in safetyTips"
          :key="tip.title"
          :timestamp="tip.title"
          :type="tip.type"
          size="large"
        >
          {{ tip.content }}
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { Cloudy, Sunny, Moon, Umbrella } from '@element-plus/icons-vue';

const forecast = ref([
  {
    date: '8月20日',
    weekday: '周四',
    icon: '☀️',
    weather: '晴间多云',
    high: 35,
    low: 27,
    feelsLike: 38,
    humidity: '72%',
    wind: '东南风 3级',
    precipitation: '20%',
    weatherClass: 'hot-day',
    advice: '高温日 · 注意防暑',
    adviceTag: 'danger',
  },
  {
    date: '8月21日',
    weekday: '周五',
    icon: '⛅',
    weather: '晴转阴，午后雷阵雨',
    high: 34,
    low: 26,
    feelsLike: 37,
    humidity: '78%',
    wind: '东南风 3-4级',
    precipitation: '65%',
    weatherClass: 'rain-day',
    advice: '午后带伞 · 防雷雨',
    adviceTag: 'warning',
  },
  {
    date: '8月22日',
    weekday: '周六',
    icon: '⛈️',
    weather: '多云转雷阵雨',
    high: 33,
    low: 26,
    feelsLike: 36,
    humidity: '82%',
    wind: '南风 3级',
    precipitation: '70%',
    weatherClass: 'rain-day',
    advice: '雷雨天气 · 减少外出',
    adviceTag: 'warning',
  },
  {
    date: '8月23日',
    weekday: '周日',
    icon: '🌤️',
    weather: '多云间晴',
    high: 35,
    low: 27,
    feelsLike: 38,
    humidity: '75%',
    wind: '西南风 2-3级',
    precipitation: '30%',
    weatherClass: 'hot-day',
    advice: '适宜出游 · 注意防晒',
    adviceTag: 'warning',
  },
  {
    date: '8月24日',
    weekday: '周一',
    icon: '☀️',
    weather: '晴到多云',
    high: 36,
    low: 27,
    feelsLike: 39,
    humidity: '70%',
    wind: '东南风 3级',
    precipitation: '15%',
    weatherClass: 'hot-day',
    advice: '高温持续 · 多补水',
    adviceTag: 'danger',
  },
  {
    date: '8月25日',
    weekday: '周二',
    icon: '🌥️',
    weather: '多云转阴',
    high: 33,
    low: 26,
    feelsLike: 35,
    humidity: '76%',
    wind: '东风 3-4级',
    precipitation: '40%',
    weatherClass: 'cloudy-day',
    advice: '体感舒适 · 适宜出行',
    adviceTag: 'success',
  },
  {
    date: '8月26日',
    weekday: '周三',
    icon: '🌦️',
    weather: '阴有阵雨',
    high: 32,
    low: 25,
    feelsLike: 34,
    humidity: '80%',
    wind: '东北风 3级',
    precipitation: '60%',
    weatherClass: 'rain-day',
    advice: '阵雨天气 · 带好雨具',
    adviceTag: 'warning',
  },
]);

const activities = ref([
  {
    icon: '🏛️',
    title: '西湖文化漫步',
    time: '推荐 8月23日（周日）· 上午 7:00~10:00',
    desc: '清晨西湖边凉风习习，可避开高温时段游览苏堤、白堤，欣赏接天莲叶无穷碧的荷花盛景。',
    tag: '☀️ 晴好天气',
    tagType: 'success',
  },
  {
    icon: '🍵',
    title: '龙井茶村品茗',
    time: '推荐 8月25日（周二）· 全天',
    desc: '多云天气，体感舒适，适宜前往龙井村、梅家坞体验采茶、品茶，在茶山间感受自然清凉。',
    tag: '🌥️ 多云舒适',
    tagType: 'success',
  },
  {
    icon: '🏊',
    title: '室内备选方案',
    time: '推荐 8月21日~22日（雷雨天）',
    desc: '雷阵雨天气适合室内活动：浙江省博物馆、杭州大厦购物、或者找一家精致的茶馆避雨听风。',
    tag: '⛈️ 雷雨备选',
    tagType: 'warning',
  },
  {
    icon: '🌿',
    title: '西溪湿地探幽',
    time: '推荐 8月23日~24日· 傍晚 16:00~18:30',
    desc: '傍晚时分气温下降，西溪湿地绿意盎然，乘船穿行于芦苇荡中，感受"一曲溪流一曲烟"。',
    tag: '🌤️ 推荐傍晚',
    tagType: 'success',
  },
  {
    icon: '🌃',
    title: '钱塘江夜游',
    time: '推荐 8月20日~24日· 晚上 19:00~21:00',
    desc: '夜晚江风拂面，城市灯光秀璀璨夺目，是夏季避暑的绝佳选择。',
    tag: '🌙 夜间活动',
    tagType: 'primary',
  },
  {
    icon: '🛍️',
    title: '湖滨银泰商圈',
    time: '推荐 8月24日（周一）· 白天',
    desc: '高温天气的最佳避暑方式——逛商场！湖滨银泰、in77等商圈美食购物一应俱全。',
    tag: '❄️ 室内避暑',
    tagType: 'info',
  },
]);

const safetyTips = ref([
  {
    title: '☀️ 防暑降温',
    type: 'warning',
    content: '白天高温时段（10:00~16:00）尽量减少户外活动，随身携带饮用水，备好藿香正气水、清凉油等防暑药品。',
  },
  {
    title: '⛈️ 防雷防雨',
    type: 'warning',
    content: '雷雨天气时请勿在空旷地带、高大树木下或水边停留，关闭手机等电子设备，尽快进入室内避险。',
  },
  {
    title: '🌊 防溺水',
    type: 'danger',
    content: '杭州水系发达，切勿在西湖、运河等非游泳区域下水游泳，尤其是雷雨天气更要远离水域。',
  },
  {
    title: '🧴 防晒护肤',
    type: 'primary',
    content: '紫外线指数高（8级），出门前30分钟涂抹SPF50+防晒霜，每2小时补涂一次，佩戴遮阳帽和太阳镜。',
  },
  {
    title: '🥤 饮食卫生',
    type: 'success',
    content: '夏季食物易变质，注意饮食卫生。推荐品尝杭州特色：龙井虾仁、西湖醋鱼、片儿川，清淡开胃。',
  },
]);
</script>

<style scoped>
.weather-dashboard {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 12px;
}

.header-left h1 {
  margin: 0;
  font-size: 28px;
  color: #303133;
}

.subtitle {
  margin: 4px 0 0;
  color: #909399;
  font-size: 14px;
}

.section-title {
  font-size: 22px;
  color: #303133;
  margin: 32px 0 16px;
  padding-left: 12px;
  border-left: 4px solid #409EFF;
}

/* 今日卡片 */
.today-card {
  margin-bottom: 24px;
  background: linear-gradient(135deg, #fff7e6 0%, #fff 100%);
}

.today-body {
  display: flex;
  align-items: center;
  gap: 32px;
  padding: 16px 0;
  flex-wrap: wrap;
}

.today-weather-icon {
  font-size: 72px;
  line-height: 1;
}

.today-temp {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.temp-high {
  font-size: 56px;
  font-weight: 700;
  color: #F56C6C;
}

.temp-low {
  font-size: 36px;
  color: #409EFF;
}

.temp-sep {
  font-size: 36px;
  color: #C0C4CC;
  margin: 0 4px;
}

.temp-unit {
  font-size: 24px;
  color: #909399;
}

.today-desc p {
  margin: 4px 0;
  font-size: 16px;
  color: #606266;
}

/* 每日预报卡片 */
.day-card {
  margin-bottom: 16px;
  transition: transform 0.3s;
}

.day-card:hover {
  transform: translateY(-4px);
}

.day-card.hot-day {
  border-top: 3px solid #F56C6C;
}

.day-card.rain-day {
  border-top: 3px solid #409EFF;
}

.day-card.cloudy-day {
  border-top: 3px solid #E6A23C;
}

.day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.day-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.day-date {
  font-size: 13px;
  color: #909399;
}

.day-weather-icon {
  font-size: 48px;
  text-align: center;
  margin: 8px 0;
}

.day-temp-range {
  text-align: center;
  margin: 8px 0;
}

.day-temp-range .high {
  font-size: 28px;
  font-weight: 700;
  color: #F56C6C;
}

.day-temp-range .low {
  font-size: 20px;
  color: #409EFF;
}

.day-temp-range .sep {
  color: #C0C4CC;
  margin: 0 4px;
  font-size: 20px;
}

.day-temp-range .unit {
  font-size: 14px;
  color: #909399;
}

.day-weather-desc {
  text-align: center;
  color: #606266;
  font-size: 14px;
  margin-bottom: 8px;
}

.day-details p {
  margin: 4px 0;
  font-size: 13px;
  color: #909399;
}

.day-advice {
  margin-top: 8px;
  text-align: center;
}

/* 穿衣指南 */
.advice-card {
  background: linear-gradient(135deg, #f0f9ff 0%, #fff 100%);
}

.advice-item {
  text-align: center;
  padding: 16px 8px;
}

.advice-item h3 {
  margin: 8px 0;
  font-size: 18px;
  color: #303133;
}

.advice-item p {
  margin: 4px 0;
  color: #606266;
  font-size: 14px;
}

.advice-icon {
  color: #409EFF;
}

/* 活动卡片 */
.activity-card {
  margin-bottom: 16px;
  height: 100%;
}

.activity-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.activity-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.activity-icon {
  font-size: 28px;
}

.activity-time {
  font-size: 13px;
  color: #909399;
  margin: 4px 0;
}

.activity-desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 8px 0 12px;
}

/* 风险提示 */
.risk-card {
  background: linear-gradient(135deg, #fef0f0 0%, #fff 100%);
}

/* 安全提示 */
.safety-card {
  background: linear-gradient(135deg, #f0f9eb 0%, #fff 100%);
}

/* 响应式 */
@media (max-width: 768px) {
  .weather-dashboard {
    padding: 12px;
  }

  .header-left h1 {
    font-size: 20px;
  }

  .today-body {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }

  .temp-high {
    font-size: 40px;
  }

  .temp-low {
    font-size: 28px;
  }
}
</style>