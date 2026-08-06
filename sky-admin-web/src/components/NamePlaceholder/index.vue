<template>
  <!-- 有图片且加载成功时展示图片；无图片或加载失败时展示名称首字占位 -->
  <img v-if="url && !loadError"
       :src="url"
       class="np-img"
       @error="loadError = true" />
  <div v-else
       class="np-img np-placeholder">
    <span>{{ firstChar }}</span>
  </div>
</template>

<script lang="ts">
import { Vue, Component, Prop, Watch } from 'vue-property-decorator'

/**
 * 图片展示组件：有图显示图片（加载失败也回退），无图显示名称首字占位块
 * 用途：菜品/套餐图片为非必填项，未上传或图片地址失效时用首字代替
 */
@Component({
  name: 'NamePlaceholder'
})
export default class extends Vue {
  @Prop({ default: '' }) url: string
  @Prop({ default: '' }) name: string

  private loadError = false

  @Watch('url')
  onUrlChange() {
    // 图片地址变化时重置加载状态
    this.loadError = false
  }

  get firstChar() {
    return this.name ? this.name.charAt(0) : '食'
  }
}
</script>

<style lang="scss" scoped>
.np-img {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 40px;
  border-radius: 4px;
}

.np-placeholder {
  background: #ffe9a8;

  span {
    font-size: 16px;
    font-weight: bold;
    color: #d19e0d;
  }
}
</style>
