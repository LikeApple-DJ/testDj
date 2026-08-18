export interface Employee {
  id: number
  name: string
  employeeNo: string
  department: string
  position: string
  phone: string
  email: string
  hireDate: string
  salary: number | null
  bankAccount: string
  education: string
  skills: string
  contractEndDate: string
  address: string
  emergencyContact: string
  emergencyPhone: string
  createdAt: string
  updatedAt: string
}

export interface EmployeeCreateRequest {
  name: string
  employeeNo: string
  department: string
  position: string
  phone: string
  email: string
  hireDate: string
  salary?: number
  bankAccount?: string
  education?: string
  skills?: string
  contractEndDate?: string
  address?: string
  emergencyContact?: string
  emergencyPhone?: string
}

export type EmployeeUpdateRequest = EmployeeCreateRequest

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  currentPage: number
  pageSize: number
}

export interface ImportResult {
  totalRows: number
  successRows: number
  failedRows: number
  errors: ImportError[]
}

export interface ImportError {
  row: number
  column: string
  message: string
}

export interface CostBudget {
  id: number
  employeeId: number
  costType: string
  amount: number
  year: number
  description: string
  createdAt: string
  updatedAt: string
}