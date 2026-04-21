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
            <UserCenterTab activeTabs="修改密码"></UserCenterTab>
            <div class="col-lg-9 mb-25">
                <div class="single-service-text">
                    <el-form ref="updatePwdRef"  style="max-width:300px" :rules="updatePwdRules" :model="updatePwdForm" size="medium">
                        <el-form-item prop="oldPwd">
                            <el-input  placeholder="请输入原密码" v-model="updatePwdForm.oldPwd" show-password :prefix-icon="Lock"
                                                        size="large">
                            </el-input >
                        </el-form-item>
                        <el-form-item prop="newPwd">
                            <el-input  placeholder="请输入新密码" show-password :prefix-icon="Lock" v-model="updatePwdForm.newPwd"
                                                size="large"></el-input >
                        </el-form-item>
                        <el-form-item prop="rePwd">
                            <el-input placeholder="请输入确认密码" show-password :prefix-icon="Lock" v-model="updatePwdForm.rePwd"
                                size="large"></el-input >
                        </el-form-item>
                        <el-form-item>
                            <el-button   class="add-btn" type="primary"  @click="submit()">确定</el-button >
                        </el-form-item>           
                    </el-form>
                   
                </div>
            </div>
        </div>
    </div>
</div>
</template>

<script setup>
import ToolUtils from '@/utils/ToolUtils';
import Header from '@/views/front/components/header.vue'
import { ref, reactive, onMounted} from 'vue'
import instances from "@/utils/request";
import utils from '@/utils/utils'
import UserCenterTab from './components/userCenterTab.vue'
import { useUserStore } from '@/store/userStore';
const userStore = useUserStore()
/**
 * 更新密码请求DTO
 */
const updatePwdForm = reactive({
    oldPwd: "",
    newPwd: "",
    rePwd: ""
})
/**
 * 更新密码表单引用
 */
const updatePwdRef = ref()
const updatePwdRules = reactive({
    oldPwd: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur'}
    ],
    newPwd: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur'}
    ],
    rePwd: [{
        required: true, message: "确认密码不能为空", trigger: 'blur'
    },
    {
        required: true, validator(_rule, value, callback, _source, _options) {
            if (value !== updatePwdForm.newPwd) {
                callback(new Error("密码输入密码不一致"))
            }
            callback()
        }, trigger: 'blur'
    }]
})
/**
* 
* @param formEl 确认按钮事件
*/
const submit = () => {
    updatePwdRef.value.validate(async (validate) => {
        if (validate) {
            await instances.put("/user/updatePwd",updatePwdForm).then(res => {
                utils.showSucess("密码修改成功,请重新登录")   
                userStore.logout()
                window.location.reload()
            }).catch(_error => { 
                console.log(_error)
            })
        }
    })
}

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
.add-btn{
    color: #000;
    border: 0;
    border-radius: 4px;
    position: relative;
    font-size: 14px;
    text-align: center;
    background-color: #1de278;
}
:deep(.el-radio__input.is-checked .el-radio__inner){
    background-color: #1de278 ;
    border-color:#1de278
}
.single-service-text{
    border: 1px solid #dee2e6;
    padding: 20px;
    /* box-shadow:  0px 0px 12px rgba(0, 0, 0, .12); */
}
</style>
