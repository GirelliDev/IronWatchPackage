import { startAdminTCP } from './admin.tcp.js'
import { startAdminMonitor } from './admin.monitor.js'

export function startAdminSystem() {
  startAdminTCP(9999)
  startAdminMonitor(1000)
}
