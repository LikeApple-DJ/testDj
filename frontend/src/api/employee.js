import api from './index'

export function checkField(field, value) {
  return api.get('/employees/check', { params: { field, value } })
}

export function createEmployee(data) {
  return api.post('/employees', data)
}

export function getEmployeeList(params) {
  return api.get('/employees', { params })
}

export function transferEmployee(id, data) {
  return api.post(`/employees/${id}/transfer`, data)
}

export function resignEmployee(id, data) {
  return api.put(`/employees/${id}/resign`, data)
}
