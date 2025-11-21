<template>
  <el-dialog
      v-model="dialogVisible"
      title="站点备注"
      :width="320"
      @close="handleClose"
  >
    <div
        class="demo-rich-conent"
        style="display: flex; gap: 16px; flex-direction: column"
    >
      <el-avatar
          :size="60"
          :src="getFullPortalLogoUrl(site.portalLogo)"
          style="margin-bottom: 8px"
      />
      <div>
        <p
            class="demo-rich-content__name"
            style="margin: 0; font-weight: 500"
        >
          {{site.title }}
        </p>
        <p
            class="demo-rich-content__mention"
            style="margin: 0; font-size: 14px; color: var(--el-color-info)"
        >
          {{site.description }}
        </p>
      </div>

<!--      <p class="demo-rich-content__desc" style="margin: 0">-->
<!--        {{site.description }}-->
<!--      </p>-->
    </div>
    <el-form
        ref="formRef"
        style="max-width: 800px"
        label-width="auto"
        class="demo-rich-content__form"
        label-position="top"
    >
      <el-form-item
          label="站点备注(仅自己可见)"
          prop="remark"
      >
        <el-input
            style="width: 600px"
            v-model="noteContent"
            type="textarea"
            :autosize="{ minRows: 5, maxRows: 10 }"
            autocomplete="off"
            maxlength="255"
            show-word-limit
            placeholder="可备注账号密码等信息，仅自己可见..."
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch,defineProps,defineEmits } from 'vue';

const getFullPortalLogoUrl = (portalLogo) => {
  return portalLogo.startsWith('http') ? portalLogo : import.meta.env.VITE_API_BASE_API + 'website/' + portalLogo;
};
import {getCollectRemark, modifyCollectRemark} from "@/api/siteData";
import {ElMessage} from "element-plus";

const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  },
  site: {
    type: Object,
    required: true
  },

});

const emit = defineEmits(['close']);

const dialogVisible = ref(props.visible);
const noteContent = ref("");
const getUserNote = () => {
  getCollectRemark({portalId: props.site.id}).then(res => {
    if(res.code === 200) {
      noteContent.value = res.data;
    }else {
      noteContent.value = '';
      ElMessage.error(res.message);
    }
  })
};
// 监听visible prop变化
watch([() => props.visible, () => props.site], ([newValA, newValB]) => {
   dialogVisible.value = newValA;
   if(newValA===true && newValB.id!==undefined)
   {
     if(newValB.collectRemark) {
       noteContent.value=newValB.collectRemark;
     }else {
       getUserNote();
     }
   }




});

// 监听dialogVisible变化，同步到父组件
watch(dialogVisible, (newVal) => {
 if (newVal===false) {
   emit('close');
 }
});

const handleSave = () => {
  modifyCollectRemark({portalId: props.site.id, remark: noteContent.value}).then(res => {
    if(res.code === 200) {
      ElMessage.success('保存成功');
      emit('close');
    }else {
      ElMessage.error(res.message);
    }
  });
  dialogVisible.value = false;
};

const handleClose = () => {
  dialogVisible.value = false;
  emit('close');
};
</script>

<style scoped>
.note-textarea {
  width: 100%;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
.demo-rich-content__form {
  margin-top: 30px;
}
</style>
