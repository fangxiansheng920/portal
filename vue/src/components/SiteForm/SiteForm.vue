<template>
  <div>
    <!-- 标题：编辑&新增 -->
    <div class="form-title">
      {{ mode === 'edit' ? '编辑站点' : '添加新的站点' }}
      <el-icon @click="handleCancel" class="cancel-button"><CircleClose size="20"/></el-icon>
    </div>
    <el-scrollbar class="site-form-container">
      <!-- 核心表单区域 -->
      <el-form
          ref="siteForm"
          :model="formData"
          label-position="top"
          label-width="120px"
          class="site-form "
      >
        <!-- 站点名称 -->
        <el-form-item label="站点名称" required>
          <el-input
              required
              show-word-limit
              v-model="formData.name"
              placeholder="请输入站点名称"
              maxlength="30"
          />
        </el-form-item>
        <el-form-item label="访问方式" prop="accessType" required>
          <el-checkbox-group v-model="accessTypeList" @change="accessTypeChange" :min="1">
            <el-checkbox label="PC" value="PC" />
            <el-checkbox label="小程序" value="小程序" />
            <el-checkbox label="微信" value="微信" />
            <el-checkbox label="企微" value="企微" />
          </el-checkbox-group>
        </el-form-item>
        <!-- 网站URL -->
        <el-form-item  label="网站URL" required v-if="formData.accessType!==ACCESS_TYPE.MINI">
          <div>
            <el-input
                required
                v-model="item.url"
                placeholder="https://example.com"
                class="access-type-url-input "
                v-show="formData.accessType!==ACCESS_TYPE.MINI"
                v-for="(item, index) in accessTypeUrlList" :key="index"
                >
              <template  #prepend ><div class="access-type-url-input__prepend">{{item.accessType}}</div></template>
              <template  #append>
                <el-checkbox v-model="item.toQrCode"  name="type">
                  转二维码
                </el-checkbox>
              </template>
            </el-input>
          </div>


        </el-form-item>
        <!-- 微信二维码（条件显示） -->
        <el-form-item label="小程序二维码" v-if="formData.accessType.includes(ACCESS_TYPE.MINI) " required>
          <el-upload
              class="avatar-uploader"
              :action="UploadURL"
              :headers="{ Authorization: 'Bearer '+token }"
              :show-file-list="false"
              :on-success="handleQrUploadSuccess"
              :before-upload="beforeAvatarUpload"
              accept="image/png, image/jpeg, image/jpg"


          >
            <template #tip>
              <div class="el-upload__tip">图片必须是 JPG/PNG 格式，大小不能超过 1MB</div>
            </template>
            <img v-if="formData.qrUrl" :src="formData.qrUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <!-- 站点信息描述 -->
        <el-form-item label="站点描述" >
          <el-input
              type="textarea"
              :autosize="{ minRows: 4, maxRows: 14 }"
              v-model="formData.description"
              placeholder="描述一下站点用途/特点（最多254字）"
              maxlength="254"
              show-word-limit
          />
        </el-form-item>
        <!-- 站点图标 -->
        <el-form-item label="站点图标" required>
          <el-upload
              class="avatar-uploader"
              :action="UploadURL"
              :headers="{ Authorization: 'Bearer '+token }"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
              accept="image/png, image/jpeg, image/jpg"
          >
            <img v-if="formData.portalLogo" :src="formData.portalLogo" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <!-- 项目标签 -->
        <el-form-item label="项目标签" required>
          <el-select
              v-model="formData.tagIdSet"
              placeholder="请选择标签"
              filterable
              clearable
              class="tag-select"
              multiple
              multiple-limit="5"
          >
            <el-option
                v-for="tag in allTags"
                :key="tag.id"
                :label="tag.name"
                :value="tag.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="站点类型" required>
          <el-radio-group v-model="formData.publicFlag"  class="public_flg_radio">
            <el-radio :value="0" >私有
            </el-radio>
            <el-radio :value="1"  >公开
              <text class="tag-hint">任何登录用户均可访问该项目</text>
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="props.mode==='add'">
          <el-checkbox label="设为收藏" v-model="formData.collectFlag" />
        </el-form-item>


      </el-form>
    </el-scrollbar>
    <el-row class="submit-row">
      <el-button
          type="primary"
          @click="handleSubmit"
          :loading="loading"
      >
        确认
      </el-button>
      <el-button
          @click="handleCancel"
      >
        取消
      </el-button>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, computed, defineProps, defineEmits, ref, watch, onMounted } from 'vue';
import { ElMessage} from 'element-plus';
import {
  createSite,
  updateSite,
} from '@/api/siteManagement.js';
import { Plus ,CircleClose } from '@element-plus/icons-vue'
import { getAllTag } from '@/api/siteManagement.js'
import {ACCESS_TYPE} from "@/constants/constant";

const UploadURL = import.meta.env.VITE_API_BASE_API+ 'uploadWebsiteImage';
// 定义props和emits
const props = defineProps({
  mode: {
    type: String,
    required: true,
    validator: (val) => val === "" || ['add', 'edit'].includes(val) // 允许空字符串
  },
  initialData: {
    type: Object,
    default: () => ({})
  }
});
// 初始化表单数据
function editPreData(newData){
  if (props.mode === 'edit' && newData) {
    formData.name = newData.title || '';  // 注意这里是 title，而不是 name
    formData.description = newData.description || ''; //描述
    formData.tagIdSet = newData.tagIdSet || ''; //标签id集合
    formData.publicFlag = newData.publicFlag || 0; //公开标志
    formData.portalLogo = newData.portalLogo || ''; //站点图标
    formData.id = newData.id || ''; //站点id
    formData.portalQRcode = newData.portalQRcode || ''; //站点二维码
    //处理多种访问类型
    formData.pcUrl = newData.portalUrl || '';
    formData.qrUrl = newData.portalQRcode || '';
    formData.accessType = newData.accessType || ''; //访问类型
    if(formData.accessType === ACCESS_TYPE.MINI){
      //如果是微信小程序，则只显示微信二维码
      accessTypeList.value = [ACCESS_TYPE.MINI];
      accessTypeUrlList.value=[];
    }else if(formData.accessType === ACCESS_TYPE.PC && newData.portalUrl && !newData.portalUrl.includes("toQrCode")){
      //如果是PC端,之前的数据
      accessTypeList.value = [ACCESS_TYPE.PC];
      accessTypeUrlList.value=[{
        accessType:ACCESS_TYPE.PC,
        url:formData.pcUrl,
        toQrCode:false
      }];
    }
    else {
      // 关键修复：安全解析 portalUrl
      accessTypeList.value = newData.accessType ? newData.accessType.split(',') : [];

      // 1. 先判断 portalUrl 是否为非空字符串
      if (typeof newData.portalUrl === 'string' && newData.portalUrl.trim() !== '') {
        try {
          // 2. 尝试解析，捕获异常
          accessTypeUrlList.value = JSON.parse(newData.portalUrl);
          // 3. 确保解析结果是数组（避免后端返回非数组格式）
          if (!Array.isArray(accessTypeUrlList.value)) {
            accessTypeUrlList.value = [];
          }
        } catch (e) {
          console.error('解析 portalUrl 失败:', e);
          accessTypeUrlList.value = []; // 解析失败时重置为空数组
        }
      } else {
        // 当 portalUrl 为空或无效时，直接设为空数组
        accessTypeUrlList.value = [];
      }
    }
  }
}
watch(
  () => props.initialData,
  (newData) => {
    // 仅在编辑模式下更新（新增模式不需要）
    editPreData(newData);
  },
  { immediate: true } // 初始加载时也执行一次
);
const token = computed(() => {
  return localStorage.getItem('token');
});
const accessTypeList = ref([ACCESS_TYPE.PC]);
const accessTypeUrlList = ref([
    {
      accessType:ACCESS_TYPE.PC,
      url:'',
      toQrCode:false
    }
    ]);

const emit = defineEmits(['submit', 'cancel']);

// 表单数据
const formData = reactive({
  name: '',
  portalUrl: '',
  portalLogo: '',
  description: '',
  accessType: ACCESS_TYPE.PC,
  pcUrl: '',
  qrUrl: '',
  isQrFileChanged: false,
  tagIdSet: [],
  publicFlag: 0,
  id: '',
  collectFlag: true,
});

//所有标签数据
const allTags =ref([]);
const loadingTags = ref(false);
// 重置表单方法
const resetForm = () => {
  formData.name = '';
  formData.portalUrl = '';
  formData.portalLogo = '';
  formData.description = '';
  formData.accessType = ACCESS_TYPE.PC;
  formData.pcUrl = '';
  formData.qrUrl = '';
  formData.isQrFileChanged = false;
  formData.tagIdSet = '';
  formData.publicFlag = 0;
  formData.id = '';
  formData.portalQRcode='';
  formData.collectFlag = true;
  accessTypeList.value = [ACCESS_TYPE.PC];
  accessTypeUrlList.value=[
    {
      accessType:ACCESS_TYPE.PC,
      url:'',
      toQrCode:false
    }
  ];

};

// 获取所有标签
const fetchAllTags = async () => {
  loadingTags.value = true;
  try {
    const response = await getAllTag();
    if (response.code === 200 && response.status === true && response.data) {
      allTags.value = response.data || [];
      // console.log('获取标签成功:', allTags.value);
    } else {
      // ElMessage.error(response.message || '获取标签失败');
    }
  } catch (error) {
    console.error('获取标签错误:', error);
    // ElMessage.error('获取标签异常，请重试');
  } finally {
    loadingTags.value = false;
  }
};
const accessTypeChange =()=>{
  const originUrls= accessTypeUrlList.value;
  accessTypeUrlList.value=[];
  accessTypeList.value.forEach(item=>{
    var newItem=1;
    originUrls.forEach(originUrl=>{
      if(originUrl.accessType === item){
        accessTypeUrlList.value.push(originUrl);
        newItem=0;
      }
    })
    if(newItem === 1 && item !== ACCESS_TYPE.MINI)
    {
      accessTypeUrlList.value.push({
        accessType:item,
        url:'',
        toQrCode:false
      })
    }
  })
  formData.accessType = accessTypeList.value.join(',');
};
// 监听mode变化，处理表单初始化
watch(
  () => props.mode,
  (newMode) => {
    if (newMode === 'add') {
      // 新增模式：重置表单
      resetForm();
    } else if (newMode === 'edit' && props.initialData) {
      // 编辑模式：加载初始数据
      editPreData(props.initialData);
      // formData.name = props.initialData.title || '';
      // formData.description = props.initialData.description || '';
      // formData.accessType = props.initialData.accessType || ACCESS_TYPE.PC;
      // formData.portalUrl = props.initialData.portalUrl || '';
      // formData.pcUrl = props.initialData.portalUrl || '';
      // formData.qrUrl = props.initialData.portalQRcode || '';
      // formData.tagId = props.initialData.tagId ||'';
      // formData.publicFlag = props.initialData.publicFlag || 0;
      // formData.portalLogo = props.initialData.portalLogo || '';
      // formData.id = props.initialData.id || '';
    }
  },
  { immediate: true }
);


onMounted(()=>{
  fetchAllTags();
  // console.log('props.site',props.site);
})
// 加载状态
const loading = ref(false);

// 二维码相关计算属性
const hasQrCode = computed(() => {
  return formData.qrUrl || formData.qrFile;
});

// 图片上传成功处理
const handleQrUploadSuccess = (response) => {
  if (response.code === 200) {
    // 拼接完整站点图片URL
    const fullQrUrl = import.meta.env.VITE_API_BASE_API + 'website/' + response.message;
    formData.qrUrl = fullQrUrl; // 关键：使用完整URL
    ElMessage.success('图片上传预览成功');
    formData.isQrFileChanged = true;
  } else {
    ElMessage.error(response.message || '图片上传失败');
  }
};

function handleAvatarSuccess(res) {
  if (res.code === 200) {
    const fullLogoUrl = import.meta.env.VITE_API_BASE_API + 'website/' + res.message;
    formData.portalLogo = fullLogoUrl; // 关键：使用完整URL
    ElMessage.success('图片上传成功');
  } else {
    ElMessage.error(res.message || '图片上传失败');
  }
}

const beforeAvatarUpload = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png' && rawFile.type !== 'image/jpg') {
    ElMessage.error('图片必须是 JPG/PNG 格式!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 1) {
    ElMessage.error('图片不能超过 1MB!')
    return false
  }
  return true
}

// 提交表单
const handleSubmit = async () => {
  // 验证站点名称是否填写
  if (!formData.name) {
    ElMessage.warning('请填写站点名称！');
    return;
  }
  // 验证标签是否选择
  if (!formData.tagIdSet || formData.tagIdSet.length === 0) {
    ElMessage.warning('请选择项目标签！');
    return;
  }
  // 根据访问类型进行验证
   if(formData.accessType.includes(ACCESS_TYPE.MINI))
   {
     if (!hasQrCode.value) return ElMessage.warning('请上传微信二维码！');
   }
  // 校验 访问类型是否填写
  if(accessTypeUrlList.value.length > 0)
  {
    for(let i = 0;i<accessTypeUrlList.value.length;i++){
    const item = accessTypeUrlList.value[i];
    if(!item.url){
      return ElMessage.warning(`请填写访问类型${item.accessType}的网址！`);
    }
    if (!/^https?:\/\//.test(item.url)) {
        return ElMessage.warning({
          message: `${item.accessType}地址必须以http://或https://开头！`,
          duration: 700
        });

      }
    {
      if(!accessTypeUrlList.value[i].url) return ElMessage.warning('请填写完整访问类型网址！');
    }
  }
  }
  //图标验证
  if(!formData.portalLogo) return ElMessage.warning('请上传图标！');
  // 执行提交逻辑
  try {
    loading.value = true;

    // 准备API参数
    const apiParams = {
      title: formData.name,
      description: formData.description,
      accessType: formData.accessType,
      publicFlag: formData.publicFlag,
      status: formData.status ? 1 : 0,
      tagIdSet: formData.tagIdSet,
      portalLogo: formData.portalLogo,
      portalQRcode: formData.qrUrl,
      portalUrl: JSON.stringify(accessTypeUrlList.value),
      collectFlag: formData.collectFlag?1:0,
    };

    // 调用新增/修改API
    let apiRes;
    if (props.mode === 'add') {
      apiRes = await createSite(apiParams);
      // console.log('新增站点返回:', apiRes);

      if (apiRes.code === 201) {
        const newPortalId = apiRes.data;
        // console.log('新站点 ID:', newPortalId);
        resetForm();
        ElMessage.success({
      message:'新增标签成功！',
      duration:700
    })
        emit('submit', { ...apiParams, portalId: newPortalId, id: newPortalId });
      } else {
        throw new Error(apiRes.message || '新增失败');
      }
    } else {
      const siteId = props.initialData.id;
      // console.log('要修改的站点ID:', siteId);

      if (!siteId) {
        throw new Error('站点ID不存在，无法修改');
      }

      apiRes = await updateSite(apiParams, siteId);

      if (apiRes.code === 200) {
        ElMessage.success({
      message:'修改成功',
      duration:500//毫秒
    })
        emit('submit', { ...apiParams, portalId: siteId });
      } else {
        throw new Error(apiRes.message || '修改失败');
      }
    }
  } catch (error) {
    ElMessage.error(error.message || '操作失败');
  } finally {
    loading.value = false;
  }
};

// 取消操作
const handleCancel = () => {
  emit('cancel');
  // 取消时重置表单（如果是新增模式）
  if (props.mode === 'add') {
    resetForm();
  }
};


</script>

<style scoped>
.avatar-uploader .avatar {
  width: 88px;
  height: 88px;
  display: block;
}
/* 保留你原来的样式 */
.site-form-container {
  background: #fff;
  border-radius: 8px;
  height: 75vh;
  padding-right: 25px;

}



.form-title {

  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.site-form {
  width: 620px;
}
.tag-select{
  width: 100%;
}
.qr-upload {
  width: 200px;
}

.tag-item {
  margin-right: 8px;
  margin-bottom: 8px;
}

.tag-input {
  min-width: 100%;
}

.tag-hint {
  margin-top: 4px;
  font-size: 12px;
  color: #999;
}

.upload-button {
  width: 70%;
}
.access-type-url-input{
  margin-top: 10px;
  width: 600px;
  padding: 0;
}
.access-type-url-input__prepend{
 width: 20px;
}
</style>
<style>

.public_flg_radio{
  display: flex;
  flex-direction: column;
  justify-items: start !important;
  align-items: start !important;

}
.public_flg_radio .el-radio {
  margin: 0px 0px;
}
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}
.submit-row{
  display: flex;
  justify-content: flex-end;
}
.cancel-button{
  cursor: pointer;
}
.cancel-button:hover{
  color: var(--el-color-primary);
}
.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 88px;
  height: 88px;
  text-align: center;
}
</style>
