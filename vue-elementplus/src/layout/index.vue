<template>
<section id="main-wrapper" class="theme-default">
    <header id="header">
        <!--logo start-->
        <div class="brand">
            <a href="" class="logo">
                <i class="icon-layers"></i>&nbsp;
                <span>{{siteConfig.siteName}}</span></a>
        </div>
        <!--logo end-->
        <ul class="nav navbar-nav navbar-left">
           
            
        </ul>
        <ul class="nav navbar-nav navbar-right">
           
        </ul>
    </header>
    <!--sidebar left start-->
    <aside class="sidebar sidebar-left">
        <div class="sidebar-profile">
            <div class="avatar">
                <img class="img-circle profile-image" :src="userStore.user.img_url" alt="profile">
                <i class="on border-dark animated bounceIn"></i>
            </div>
            <div class="profile-body dropdown">
                <a href="javascript:void(0);" @click="iqShow = !iqShow" class="dropdown-toggle" data-toggle="dropdown" aria-expanded="false"><h4>
                    {{ userStore.user.username}} <span class="caret"></span></h4>
                </a>
                <small class="title">{{ userStore.user.role_name}}</small>
                <ul class="dropdown-menu animated fadeInRight" :class="{'show':iqShow}" role="menu">
                    <li>
                        <a href="javascript:void(0);" @click="$router.push('/user/personal')">
                                <span class="icon"><i class="fa fa-user"></i>
                                </span>个人资料</a>
                    </li>
                    <li>
                        <a href="javascript:void(0);" @click="$router.push('/front')">
                                <span class="icon"><i class="fa  fa-life-ring"></i>
                                </span>前台</a>
                    </li>
                    
                    <li class="divider"></li>
                    <li>
                        <a  @click="logout">
                                <span class="icon"><i class="fa fa-sign-out"></i>
                                </span>登出</a>
                    </li>
                </ul>
            </div>
        </div>
        <nav>
            <h5 class="sidebar-header">导航栏</h5>
            <ul class="nav nav-pills nav-stacked">
               <el-menu class="menu-container" :collapse-transition="false" :collapse="settingStore.isCollapse" :default-active="activeIndex">
                    <MenuItem
                        v-for="menu in menus"
                        :item="menu"
                        :key="menu.id" 
                    ></MenuItem>
                </el-menu>
            </ul>
        </nav>

    </aside>
  
   
    <section class="main-content-wrapper">
        <Tab></Tab>
        <Main></Main>
    </section>
</section>
</template>

<script setup>
import Main from './components/main/index.vue'
import Tab from './components/tabs/index.vue'
import { onMounted, reactive,computed, ref ,onBeforeUnmount} from "vue";
import Logo from '@/common/components/logo/index.vue'
import MenuItem from "./components/menu-item.vue";
import instances from "@/utils/request";
import * as siteConfig  from "../config/index";
import { ElMessageBox ,ElNotification } from "element-plus";
import {useSettingStore} from '@/store/settingStore'
import { useUserStore } from "@/store/userStore";
import { useRouter } from 'vue-router'
const router = useRouter();
import { useRoute } from 'vue-router'
const route=useRoute()
const userStore = useUserStore();
const settingStore = useSettingStore()
const iqShow=ref(false)
const activeIndex =computed(() => {
    const {meta}=route
    return meta.activeIndex
})
//菜单数据
const menus = reactive([]);
//加载菜单数据方法
const loadMenu = () => {
    instances.post("/menu/menus").then((res) => {
        menus.push(...res.data);
    }).catch((error) => {
        console.error(error);
    });
};
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
    router.push('/login').then(() => {
      // 如果需要强制刷新，可以在这里加上
      window.location.reload();
    });
  });
};
// 全局点击事件处理函数
const handleClickOutside = (event) => {
  const navbarList = document.querySelector('.dropdown-toggle');
  if (!navbarList.contains(event.target)) {
    iqShow.value = false;
  }
};


// 移除全局点击事件监听器
onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});
onMounted(() => {
     document.addEventListener('click', handleClickOutside);
    loadMenu();
});
</script>
<style src="@/assets/styles/bootstrap.min.css" scoped></style>
<style src="@/assets/styles/font-awesome.min.css" scoped></style>
<style src="@/assets/styles/simple-line-icons.css" scoped></style>
<style src="@/assets/styles/animate.css" scoped></style>
<style src="@/assets/styles/main.css" scoped></style>
<style scoped>
.sidebar-fold {
    width: var(--sidebar-width-collapsed) !important;
   
}
.sidebar-margin-left{
    margin-left:var(--sidebar-width-collapsed) !important; 
    width: calc(100% - 65px);
}
.el-menu {
    background-color:var(--ct-menu-bg);
    border-right: none;
    box-sizing: border-box;
    list-style: none;
    margin: 0;
    padding-left: 0;
    position: relative;
   
}
:deep(.el-menu-item){
    color: var(--ct-menu-item-color);
    font-size: 13px!important;
   
}
:deep(.el-sub-menu__title){
    color: var(--ct-menu-item-color);
}
:deep(.el-sub-menu__title:hover) {
    color:var(--ct-menu-item-hover-color);
    background-color:#fff;
    border-radius: 5px;
   
}

.el-menu :deep(.is-active){
    color:var(--ct-menu-item-active-color)!important;
    
}
.el-menu-item :deep(.is-active){
    color:#fff;
    background-color:#fff !important;
    
}
.dropdown-menu-end{
    right: 0;
}
.main-content-wrapper{
    padding: 70px 20px;
    height: 100vh;
  
}
</style>
