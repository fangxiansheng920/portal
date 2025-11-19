<template>
  <span class="time-display">
    创建于：{{ formattedTime }}
  </span>
</template>

<script setup>
import { computed ,defineProps} from 'vue'

const props = defineProps({
  time: {
    type: String,
    required: true
  }
})

const formattedTime = computed(() => {
  return new Date(props.time).toLocaleDateString()
})
</script>

<style scoped>
.time-display {
  font-size: 12px;
  color: #999;
}
</style>