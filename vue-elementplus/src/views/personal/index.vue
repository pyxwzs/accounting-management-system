<template>
   <div class="row">
                    <div class="col-md-12">
                        <section class="panel">
                            <div class="panel-body profile-wrapper">
                                <div class="col-md-3">
                                    <div class="profile-pic text-center">
                                        <UserAvatar></UserAvatar>
                                    </div>
                                </div>
                                <div class="col-md-9">
                                    <div class="profile-info">
                                        <h1> {{userStore.user.username}}</h1>
                                        <span class="text-muted">{{userStore.user.role_name}}</span>
                                        <p>
                                           欢迎来到我们的管理系统，您的高效工作之旅从此刻开始！
                                        </p>
                                        <div class="connect">
                                            <button type="button" class="btn btn-success btn-trans" @click="openUpdateUserInfo"><span class="fa fa-user"></span> 个人信息</button>
                                            <button type="button" class="btn btn-primary btn-trans"  @click="openUpdatePwd"><span class="fa fa-lock"></span> 登录密码</button>
                                            <UpdateUserInfo ref="updateUserInfoModal" @success="updateUserInfoSuccess"></UpdateUserInfo>
                                            <UpdatePwd ref="updatePwdModal" @success="updatePwdSuccess"></UpdatePwd>
                                        </div>
                                        <div class="social">
                                            <ul>
                                                <p class="fs-13"><strong>姓名: </strong> &nbsp;&nbsp;&nbsp; {{userStore.user.real_name}} </p>
                                                <p class="fs-13"><strong>昵称: </strong> &nbsp;&nbsp;&nbsp; {{userStore.user.nick_name}} </p>
                                                <p class="fs-13"><strong>性别: </strong> &nbsp;&nbsp;&nbsp; {{userStore.user.sex}}  </p>
                                                <p class="fs-13"><strong>手机: </strong> &nbsp;&nbsp;&nbsp; {{userStore.user.phone}}  </p>
                                                <p class="fs-13"><strong>邮箱: </strong> &nbsp;&nbsp;&nbsp; {{userStore.user.email}} </p>

                                            </ul>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </section>
                    </div>
                  
    </div>
</template>
<script setup>
import * as siteConfig  from "../../config/index";
import { useUserStore } from '@/store/userStore';
import ToolUtils from '@/utils/ToolUtils';
import { useRoute, useRouter } from 'vue-router';
import { computed, onMounted, reactive ,ref} from 'vue';
import instances from "@/utils/request";
import UserAvatar from './components/user-avatar.vue';
import UpdatePwd from './components/update-pwd.vue'
import UpdateUserInfo from './components/update-userInfo.vue'
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
/**
* 密码更新组件引用
*/
const updatePwdModal = ref(null)
const updatePwdSuccess = () => {
    userStore.logout()
    window.location.reload()
}
/**
 * 更新密码
 */
 const openUpdatePwd=()=>{
    updatePwdModal.value.openUpdatePwdDialog()
}



/**
* 个人信息更新组件引用
*/
const updateUserInfoModal = ref(null)
const updateUserInfoSuccess = () => {
    
}
/**
 * 更新个人信息
 */
 const openUpdateUserInfo=()=>{
    updateUserInfoModal.value.openUpdateUserInfoDialog()
}

const loadData = async () => {
    await instances.get("/user/user").then((res) => {
        userStore.setUser(res.data)
    }).catch(err => { 
        console.log(err);
    })
}
onMounted(() => {
    loadData()
})
</script>
<style src="@/assets/styles/bootstrap.min.css" scoped></style>
<style src="@/assets/styles/font-awesome.min.css" scoped></style>
<style src="@/assets/styles/simple-line-icons.css" scoped></style>
<style src="@/assets/styles/animate.css" scoped></style>
<style src="@/assets/styles/main.css" scoped></style>
<style scoped>

.containers {
  padding: 0 100px;
}
.logo-me{
    font: bold 19px Arial;
}

</style>
