<!-- <template>
  <div id="app">
    <router-view />
  </div>
</template>

<style>
html,
body,
#app {
  width: 100%;
  height: 100%;
}
</style> -->

<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>
 
<template>
  <!-- <h1>App根组件</h1>
  <RouterLink to="/">Login</RouterLink> 
  <hr>
  <RouterLink to="/home">Home</RouterLink> -->
 <!-- 路由展示区 -->
  <!-- <RouterLink to="/login">Home</RouterLink> -->
  <RouterView />
</template>
 
<style scoped>
 
</style>