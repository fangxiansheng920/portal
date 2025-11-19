<template>
    <div class="site-info" >
      <div class="site-details">
        <h3 class="site-title">站点名称： {{ site.title }}</h3>
            <p>站点logo：{{ site.portalLogo }}</p>
            <p>站点描述：{{site.description }}</p>
            <p>创建者: {{site.createBy }}</p>
            <div v-if="site.accessType==='PC'">
                <p>访问方式：{{ site.accessType }}</p>
                <p>站点链接: <a>{{ site.portalUrl }}</a></p>
            </div>
            <div v-else-if="site.accessType === '企业微信'">
                <p>访问方式：{{ site.accessType }}</p>
                <p>站点二维码: <img alt="站点二维码" /></p>
            <p>项目标签：{{site.tag }}</p>
            <p>创建时间: {{site.createTime }}</p>
            <p>更新时间：{{ site.updateTime }}</p>
      </div>
      </div>
    </div>
</template>


<script setup>
import {defineProps} from 'vue'

const props = defineProps({
  site:{
    type:Object,
    required: true
  }
})

console.log('SiteInfo props:', props)

</script>


<style scoped>
.site-info {
  padding: 20px;
}

.site-details {
  line-height: 1.8;
}

.site-title {
  font-size: 24px;
  margin-bottom: 15px;
  color: #333;
}

.qrcode-image {
  max-width: 200px;
  max-height: 200px;
  border: 1px solid #eee;
  margin-top: 5px;
}

.button-container {
  margin-top: 20px;
  text-align: right;
}
</style>