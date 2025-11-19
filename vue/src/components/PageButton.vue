<template>
  <div class="pagination">
    <button 
      v-if="pageCurrent > 1"
      @click="changePage(pageCurrent - 1)"
      :disabled="pageCurrent === 1"
    >
      上一页
    </button>
    <span>页码：{{ pageCurrent }} / {{ totalPages }}</span>
    <button
      v-if="pageCurrent < totalPages"
      @click="changePage(pageCurrent + 1)"
      :disabled="pageCurrent === totalPages"
    >
      下一页
    </button>
  </div>
</template>

<script>
export default {
  name: 'PageButton',
  props: {
    pageCurrent: {
      type: Number,
      required: true
    },
    totalPages: {
      type: Number,
      required: true
    }
  },
  methods: {
    changePage(page) {
      this.$emit('update:pageCurrent', page);
    }
  }
}
</script>

<style scoped>
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}
.pagination button {
  padding: 5px 12px;
  border: 1px solid #ddd;
  background: #f5f5f5;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}
.pagination button:hover {
  background: #e9e9e9;
}
.pagination button:disabled {
  background: #ddd;
  cursor: not-allowed;
}
</style>