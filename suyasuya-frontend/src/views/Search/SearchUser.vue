<script setup>
import { useRoute } from 'vue-router';


const route = useRoute()
</script>
<template>
    用户搜索
    <div>参数为：{{ route.params.keyword }}</div>
</template>
<style scoped></style>