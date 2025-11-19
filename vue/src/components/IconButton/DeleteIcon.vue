<template>
  <button class="delete-button" @click="handleClick">
    🗑️
  </button>
</template>

<script setup>
import { defineEmits } from 'vue';

const emit = defineEmits(['click']);

const handleClick = (event) => {
  event.stopPropagation();
  emit('click');
};
</script>

<style>
.delete-button {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  color: #ccc;
  padding: 0.25rem;
  transition: color 0.2s ease;
}
</style>