<template>
<header>
    <div class="header-upper">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-lg-8 col-md-6 col-sm-5"></div>
                <div class="col-lg-4 col-md-6 col-sm-7">
                    <div class="header-top-right d-flex justify-content-end">
                        <ul class="list-unstyled list-inline header-top-menu">
                             <li class="list-inline-item" v-if="userStore.user" >
                                <div class="dropdown">
                                    <a class="dropdown-toggle" @click="iqShow = !iqShow" data-bs-toggle="dropdown"><i
                                        class="fas fa-user"></i> {{userStore.user.username}}
                                    </a>
                                    <ul class="dropdown-menu dropdown-menu-end" :class="{'show':iqShow}">
                                        <li style="padding: 0;">
                                            <p>{{userStore.user.role_name}}</p>
                                        </li>
                                        <li><a  @click="$router.push('/changeUserInfo')">个人中心</a></li>
                                        <li v-if="userStore.user.roleId!=='1879449283212673025'"><a @click="$router.push('/home')">后台</a></li>
                                        <li><a  @click="logout">退出登录</a></li>
                                    </ul>
                                </div>
                            </li>
                            <li v-if="!userStore.user" class="list-inline-item login"><a @click="$router.push('/frontLogin')">登录</a></li>
                            <li v-if="!userStore.user" class="list-inline-item signup"><a @click="$router.push('/frontRegister')">注册</a></li>
                        </ul> 
                    </div>
                </div>
            </div>
        </div>
    </div>
  
    <div class="header-lover">
        <div class="container">
            <nav class="navbar navbar-expand-lg">
                <router-link to="/front" class="navbar-brand logo-title">{{siteConfig.siteName}}</router-link>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbars01"
                        aria-controls="navbars01" aria-expanded="false" aria-label="Toggle navigation"><span></span>
                    <span></span> <span></span></button>
                <div class="collapse navbar-collapse" id="navbars01">
                    <ul class="navbar-nav ms-auto">
                        <li class="nav-item dropdown">
                            <router-link to="/front" class="nav-link">首页</router-link>
                        </li>
                         <li class="nav-item dropdown">
                            <router-link to="/info" class="nav-link">收支记录</router-link>
                        </li>
                         <li class="nav-item dropdown">
                            <router-link to="/charts" class="nav-link">收支统计</router-link>
                        </li>
                         <li class="nav-item dropdown">
                            <router-link to="/budget" class="nav-link">我的预算</router-link>
                        </li>
                         <li class="nav-item dropdown">
                            <router-link to="/expenses" class="nav-link">报销管理</router-link>
                        </li>
                        <li class="nav-item dropdown">
                            <router-link to="/plan" class="nav-link">存钱计划</router-link>
                        </li>
                        <li class="nav-item dropdown">
                            <router-link to="/article" class="nav-link">财经资讯</router-link>
                        </li>
                       
                        <li class="nav-item dropdown">
                            <router-link to="/inbox" class="nav-link">收件箱</router-link>
                        </li>
                      
                        <!-- <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle">新闻动态</a>
                            <ul class="dropdown-menu" aria-labelledby="navbarDropdown03">
                                <li><router-link  class="dropdown-item" :to="{ path: '/article/1' }">Services 1</router-link ></li>
                            </ul>
                        </li> -->
                    </ul>
                </div>
            </nav>
        </div>
    </div>

</header>
</template>

<script setup>
import {ElMessageBox} from 'element-plus'
import { useUserStore } from "@/store/userStore";
import * as siteConfig  from "@/config/index";
import { Search } from '@element-plus/icons-vue'
const userStore = useUserStore();
import { useRoute ,useRouter} from 'vue-router';
const route = useRoute();
const router = useRouter();
import { onMounted, reactive,computed, ref ,onBeforeUnmount} from "vue";
import instances from "@/utils/request";
import utils from '@/utils/utils'
const iqShow=ref(false)




/**
 * 登出
 */
const logout = () => {
  ElMessageBox.confirm("确定要退出系统吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
     //清空登录状态，跳转到登录页
     userStore.logout();
     window.location.reload();
  });
};

// 全局点击事件处理函数（未登录时没有用户下拉，querySelector 可能为 null）
const handleClickOutside = (event) => {
  const dropdown = document.querySelector('.header-top-right .dropdown');
  if (!dropdown) {
    iqShow.value = false;
    return;
  }
  if (!dropdown.contains(event.target)) {
    iqShow.value = false;
  }
};


// 移除全局点击事件监听器
onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});
onMounted(() => {
     document.addEventListener('click', handleClickOutside);
   
});
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
.logo-title{
	font-weight: bold;
	color: #1de278;
	font-size: 25px;
   
}
.dropdown-menu-end{
    inset: 27px 0px auto auto !important;
}

</style>
