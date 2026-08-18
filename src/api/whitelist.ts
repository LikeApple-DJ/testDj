import http from './http'

export interface WhitelistEntry {
  id: number
  department: string
  allowedAction: string
  createdBy: string
  createdAt: string
}

export interface PermissionWhitelistEntry {
  id: number
  userId: string
  permission: string
  createdBy: string
  createdAt: string
}

export function listImportWhitelist() {
  return http.get<WhitelistEntry[]>('/whitelist/import')
}

export function createImportWhitelist(data: { department: string; allowedAction: string }) {
  return http.post<WhitelistEntry>('/whitelist/import', data)
}

export function deleteImportWhitelist(id: number) {
  return http.delete(`/whitelist/import/${id}`)
}

export function listPermissionWhitelist() {
  return http.get<PermissionWhitelistEntry[]>('/whitelist/permission')
}

export function createPermissionWhitelist(data: { userId: string; permission: string }) {
  return http.post<PermissionWhitelistEntry>('/whitelist/permission', data)
}

export function deletePermissionWhitelist(id: number) {
  return http.delete(`/whitelist/permission/${id}`)
}