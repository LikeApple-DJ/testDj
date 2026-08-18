import http from './http'
import type { CostBudget } from '@/types/employee'

export function listCostBudgets(employeeId: number, year?: number) {
  return http.get<CostBudget[]>(`/employees/${employeeId}/costs`, { params: { year } })
}

export function createCostBudget(employeeId: number, data: { costType: string; amount: number; year: number; description: string }) {
  return http.post<CostBudget>(`/employees/${employeeId}/costs`, data)
}

export function deleteCostBudget(employeeId: number, costId: number) {
  return http.delete(`/employees/${employeeId}/costs/${costId}`)
}