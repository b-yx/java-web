// 金额展示：本后端金额单位统一为 元
export function formatPrice(n) {
  const v = Number(n || 0)
  return v.toFixed(2)
}

export function maskPhone(phone) {
  if (!phone) return ''
  return String(phone).replace(/(\d{3})\d*(\d{4})/, '$1****$2')
}

// 订单状态文案
export function orderStatusText(status) {
  switch (Number(status)) {
    case 1:
      return '待付款'
    case 2:
      return '待接单'
    case 3:
      return '已接单'
    case 4:
      return '派送中'
    case 5:
      return '已完成'
    case 6:
      return '已取消'
    default:
      return '未知状态'
  }
}

export function orderStatusColor(status) {
  return Number(status) === 5 || Number(status) === 6 ? '#999999' : '#ffc200'
}

export function pad(n) {
  return n < 10 ? '0' + n : '' + n
}

export function formatDateTime(dateStr) {
  const d = dateStr ? new Date(dateStr) : new Date()
  return (
    d.getFullYear() +
    '-' +
    pad(d.getMonth() + 1) +
    '-' +
    pad(d.getDate()) +
    ' ' +
    pad(d.getHours()) +
    ':' +
    pad(d.getMinutes())
  )
}

// 一小时后送达时间 HH:mm
export function arrivalTime() {
  const d = new Date()
  d.setTime(d.getTime() + 3600000)
  return pad(d.getHours()) + ':' + pad(d.getMinutes())
}

// 计算订单商品件数与合计
export function sumOrder(list) {
  let count = 0
  let amount = 0
  ;(list || []).forEach((item) => {
    count += Number(item.number || 0)
    amount += Number(item.number || 0) * Number(item.amount || 0)
  })
  return { count, amount }
}