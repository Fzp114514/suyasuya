<script setup>
import { RouterView } from 'vue-router'
import { ElConfigProvider } from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import Loading from '@/components/Loading.vue'

</script>

<template>
  <Loading />
  <el-config-provider :locale="zhCn">
    <router-view />
  </el-config-provider>
</template>