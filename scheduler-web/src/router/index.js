import { createRouter, createWebHistory } from 'vue-router';
import JobList from '../views/JobList.vue';
import JobLog from '../views/JobLog.vue';
import WeatherDashboard from '../views/WeatherDashboard.vue';

const routes = [
  { path: '/', redirect: '/weather' },
  { path: '/jobs', name: 'JobList', component: JobList },
  { path: '/logs', name: 'JobLog', component: JobLog },
  { path: '/weather', name: 'WeatherDashboard', component: WeatherDashboard },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;