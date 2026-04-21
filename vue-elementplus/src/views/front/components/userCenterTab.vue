<template>
   <div class="col-lg-3">
               
                <ul class="sidebar-services mb-25">
                    <li style="padding: 0;"  :class="{ 'active': activeTabs === '个人主页' }"><router-link to="/changeUserInfo">个人主页 <i class="fas fa-home"></i></router-link></li>
                    <!-- <li style="padding: 0;" :class="{ 'active': activeTabs === '个人信息' }"><router-link to="/changeUserInfo">个人信息 <i class="fas fa-user"></i></router-link></li> -->
                    <li style="padding: 0;" :class="{ 'active': activeTabs === '修改密码' }"><router-link to="/changePassword">修改密码 <i class="fas fa-lock"></i></router-link></li>
                    <li style="padding: 0;" :class="{ 'active': activeTabs === '消息列表' }"><router-link to="/message">消息列表 <i class="fas fa-message"></i></router-link></li>
                   
                </ul>
                <div class="sidebar-download-brouchure mb-25">
                       <UserAvatar></UserAvatar>
                </div>
                <div class="sidebar-about mb-25">
                    <p>用户名{{userStore.user.username}}</p>
                    <p>昵称：{{userStore.user.nick_name}}</p>
                    <p>姓名：{{userStore.user.real_name}}</p>
                    <p>手机：{{userStore.user.phone}}</p>
                    <p>邮箱：{{userStore.user.email}}</p>
                </div>
            </div>
</template>

<script setup>
import { ref, reactive, onMounted} from 'vue'
import * as siteConfig  from "@/config/index";
import instances from "@/utils/request";
import { useUserStore } from '@/store/userStore';
import UserAvatar from '../../personal/components/user-avatar.vue';
const userStore = useUserStore()
const activeTabs=ref('')
const props = defineProps({	
    activeTabs: {
		type: String,
		require: true,
	},	
})
const message=ref(false)
const loadMessage = () => {
    instances.get("/message/haveSystemMessage").then((res) => {
       message.value=res.data;
    }).catch(err => {
        console.error(err);
    })
}
onMounted(() => {
    loadMessage()
});

activeTabs.value=props.activeTabs
console.log(activeTabs.value)
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
.btn-style-1{
    margin-bottom: 10px;
}
.sidebar-about{
    text-align: left;
}
</style>
