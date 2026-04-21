<template>
    <el-result title="404"  sub-title="非常抱歉,您访问的页面不存在!">
      <template #icon>
        <el-image 
          src="/src/assets/images/404.png"
        />
      </template>
      <template #extra>
        <el-button type="primary" @click="$event=>$router.push('/home')">返回首页</el-button>
      </template>
    </el-result>
  </template>

<script setup>

</script>

<style scoped>

</style>
