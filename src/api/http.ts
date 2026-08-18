import axios from 'axios'

const http = axios.create({
  baseURL: '/api/v1',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 全局响应拦截器仅做日志记录，不自动弹窗
// 各组件自行处理错误提示
http.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('[HTTP Error]', error.config?.url, error.response?.status, error.message)
    return Promise.reject(error)
  }
)

export default http
