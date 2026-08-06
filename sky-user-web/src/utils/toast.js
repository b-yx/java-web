// 极简 toast：动态创建 DOM，不依赖组件树
let el = null
let timer = null

export function toast(message, duration = 2000) {
  if (!el) {
    el = document.createElement('div')
    el.style.cssText =
      'position:fixed;left:50%;top:45%;transform:translate(-50%,-50%);' +
      'background:rgba(0,0,0,0.75);color:#fff;font-size:14px;line-height:1.5;' +
      'padding:12px 18px;border-radius:8px;z-index:99999;max-width:70%;' +
      'text-align:center;pointer-events:none;transition:opacity .2s;'
    document.body.appendChild(el)
  }
  el.textContent = message
  el.style.opacity = '1'
  clearTimeout(timer)
  timer = setTimeout(() => {
    el.style.opacity = '0'
  }, duration)
}