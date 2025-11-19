<template>
    <div class="container ">

      <el-button
          v-for="(item, index) in items"
          :key="index"
          class="item"
          @click="setActive(index)"
          @mouseenter="hover(index)"
          @mouseleave="leave()"
          :class="{ active: activeIndex === index }"
      >
        {{ item.name }}
      </el-button>
      <!--浮标-->
      <div class="marker " :style="{ left: markerLeft + 'px', width: markerWidth + 'px', transform: `translateZ(0)` }"></div>
    </div>
</template>

<script>
export default {
  props: {
    items: {
      type: Array,
      required: true,
    },
    activeIndex: {
      type: Number,
      default: 0, // 默认激活第一个按钮
    }
  },
  data() {
    return {
      hoverIndex: null, // 当前鼠标悬停的索引
      markerLeft: 0, // 浮标的位置
      markerWidth: 0, // 浮标的宽度
    };
  },
  methods: {
    setActive(index) {
      // 触发自定义事件 'active-changed'，并将激活的索引作为参数传递
      this.$emit('active-changed', index);
    },
    hover(index) {
      this.hoverIndex = index;
      this.updateMarker(index); // 更新浮标位置
    },
    leave() {
      if (this.hoverIndex !== null) {
        this.hoverIndex = null;
        this.updateMarker(this.activeIndex); // 恢复到激活态的浮标位置
      }
    },
    updateMarker(index) {
      // 关键优化：使用offsetLeft而非getBoundingClientRect，避免滚动/定位影响
      const targetButton = this.$el.querySelectorAll('.item')[index];
      if (!targetButton) return;

      // 1. 计算浮标相对于容器的左偏移（不受页面滚动影响）
      this.markerLeft = targetButton.offsetLeft;
      // 2. 浮标宽度与按钮一致（包含按钮的padding和border）
      this.markerWidth = targetButton.offsetWidth;
    },
  },
  mounted() {
    // 优化初始化：确保DOM渲染完成后再计算（比requestAnimationFrame更可靠）
    this.$nextTick(() => {
      this.updateMarker(this.activeIndex);
    });
  },
  watch: {
    activeIndex: {
      immediate: true, // 初始化时执行
      handler(newVal) {
        this.$nextTick(() => {
          this.updateMarker(newVal);
        });
      }
    },
    '$el.clientWidth'() {
      this.$nextTick(() => {
        this.updateMarker(this.activeIndex); // 使用props的activeIndex
      });
    },
  },
};
</script>

<style scoped>
.container {
  max-width: 100%;
  position: relative;
  display: inline-flex;
  gap: 10px;
  overflow: auto;
  padding: 3px;
  font-size: 14px;
  border-radius: 20px;
  transition: all 0.3s ease;
  background-color: #F7F7F7 !important;
  margin-top: 5px;
  margin-bottom: 5px;

  /* 添加滚动条样式 */
  scrollbar-width: thin; /* Firefox 支持 */
  scrollbar-color: rgba(39, 126, 240, 0.3) transparent; /* Firefox 支持 */
}

.container:hover {
  scrollbar-width: thin; /* Firefox 支持 */
}

/* WebKit 浏览器滚动条样式 */
.container::-webkit-scrollbar {
  height: 2px; /* 横向滚动条高度，实现"更小"效果 */
  cursor: pointer;
}

.container::-webkit-scrollbar-track {
  background: transparent; /* 轨道透明，与模块融合 */
  border-radius: 10px;
}

.container::-webkit-scrollbar-thumb {
  background-color: rgba(39, 126, 240, 0.3); /* 半透明蓝色，与主题色呼应 */
  border-radius: 10px; /* 圆角设计，与整体风格统一 */
  border: 2px solid transparent; /* 增加透明边框，视觉上减小尺寸 */
}

/* 鼠标悬停时略微增加不透明度 */
.container::-webkit-scrollbar-thumb:hover {
  background-color: var(--el-color-primary);
  cursor: pointer;
  height: 4px;
}

:deep(.el-button) {
  border-radius: 20px;
  border: none;
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s ease, color 0.3s ease;
  background-color: transparent;
  z-index: 1; /* 确保内容在浮标之上 */
}

.item.active {
  background-color: transparent;
  color: #ffffff;
}

.item:hover {
  color: white;
  background-color: #277ef0;
}

.marker {
  position: absolute;
  top: 0; /* 顶部对齐 */
  bottom: 0; /* 底部对齐 */
  margin-top: 3px;
  margin-bottom: 2px;
  border-radius: 20px;
  background-color: #277ef0;
  transition: left 0.3s ease, width 0.3s ease;
  z-index: 0; /* 确保浮标在内容之下 */
}
</style>
