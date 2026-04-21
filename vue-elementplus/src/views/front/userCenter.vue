<template>
<Header></Header>
<div class="inner-page-header">
    <div class="container">
        <h1 class="mb-8">个人中心</h1>
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><router-link  to="/front"><i class="fas fa-home"></i> 首页</router-link></li>
            <li class="breadcrumb-item active" aria-current="page">个人中心</li>
        </ol>
    </div>
</div>
<div class="single-services-page pt-80 pb-55">
    <div class="container">
        <div class="row">
            <UserCenterTab activeTabs="个人主页"></UserCenterTab>
            <div class="col-lg-9 mb-25">
                <div class="single-service-text">
                  
                   
                    
                </div>
            </div>
        </div>
    </div>
</div>
</template>

<script setup>
import Header from '@/views/front/components/header.vue'
import {ElMessageBox} from 'element-plus'
import { ref, reactive, onMounted} from 'vue'
import instances from "@/utils/request";
import utils from '@/utils/utils'
import {  Delete, Edit, Plus, Refresh, Search, Sort } from '@element-plus/icons-vue';
import UserCenterTab from './components/userCenterTab.vue'
import { useUserStore } from '@/store/userStore';
import { useRoute ,useRouter} from 'vue-router'
const route = useRoute();
const router = useRouter()
const userStore = useUserStore()

/**
 * 页面挂载后执行
 */
onMounted(() => {
  
})
</script>
<style src="@/assets/styles/front/bootstrap.min.css" scoped></style>
<style src="@/assets/styles/front/all.min.css" scoped></style>
<style src="@/assets/styles/front/owl.carousel.min.css" scoped></style>
<style src="@/assets/styles/front/owl.theme.default.min.css" scoped></style>
<style src="@/assets/styles/front/venobox.css" scoped></style>
<style src="@/assets/styles/front/custom.css" scoped></style>
<style src="@/assets/styles/front/responsive.css" scoped></style>
<style src="@/assets/styles/front/helper.css" scoped></style>
<style scoped>

</style>
