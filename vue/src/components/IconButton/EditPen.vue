<template>
    <span class="pen-icon"  @click="handleClick">🖊</span>
</template>



<script setup>
import { defineEmits } from 'vue'

const emit = defineEmits(['click'])

const handleClick = (event) =>{
    event.stopPropagation(); 
    emit('click');
}

</script>
<style scoped>

</style>