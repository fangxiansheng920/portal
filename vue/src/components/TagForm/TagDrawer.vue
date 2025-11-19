<template>
    <div v-show="props.visible" class="drawer">
        <TagForm
        :mode="props.mode"
        :initial-data="props.tagData"
        @submit="handleSubmit"
        @cancel="closeTagDrawer"
        />
    </div>
</template>

<script setup>
import { defineProps, defineEmits,watch} from 'vue';
import TagForm from '@/components/TagForm/TagForm.vue';

const props = defineProps({
    visible:Boolean,
    mode:String,
    tagData:Object
})


const emit = defineEmits(['close','submit']);

watch(
    ()=>props.tagData,
    ()=>{
        // console.log('tagTada changed',newData);
    },
    {immediate:true}
)

function handleSubmit(data){
    emit('submit',data);
    closeTagDrawer();
}
// console.log('抽屉组件收到的props',props.tagData);

function closeTagDrawer(){
    emit('close');
    // console.log('表单已关闭');
}

</script>


<style>
.drawer {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: white;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

</style>