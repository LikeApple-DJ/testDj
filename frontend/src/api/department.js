import api from './index'

export function getDepartmentTree() {
  return api.get('/departments/tree')
}

export function moveDepartment(id, newParentId) {
  return api.put(`/departments/${id}/move`, { newParentId })
}

export function deleteDepartment(id) {
  return api.delete(`/departments/${id}`)
}
