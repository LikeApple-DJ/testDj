import http from './http'
import type { Employee, EmployeeCreateRequest, EmployeeUpdateRequest, PageResponse, ImportResult } from '@/types/employee'

export function listEmployees(params: {
  page?: number
  size?: number
  search?: string
  department?: string
}) {
  return http.get<PageResponse<Employee>>('/employees', { params })
}

export function getEmployee(id: number) {
  return http.get<Employee>(`/employees/${id}`)
}

export function createEmployee(data: EmployeeCreateRequest) {
  return http.post<Employee>('/employees', data)
}

export function updateEmployee(id: number, data: EmployeeUpdateRequest) {
  return http.put<Employee>(`/employees/${id}`, data)
}

export function deleteEmployee(id: number) {
  return http.delete(`/employees/${id}`)
}

export function importEmployees(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return http.post<ImportResult>('/employees/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}