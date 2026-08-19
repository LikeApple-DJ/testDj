import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response) {
      const { status, data } = error.response
      if (status === 409) {
        return Promise.reject(new Error(data.msg || '数据冲突，请刷新重试'))
      }
      return Promise.reject(new Error(data.msg || '请求失败'))
    }
    return Promise.reject(error)
  }
)

export default api
