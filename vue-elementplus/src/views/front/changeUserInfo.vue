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
                  
                    <el-form class="profile-form" style="max-width:400px" :model="formData" ref="formRef" :rules="formRules">
                                        <el-form-item label="昵称" prop="nick_name">
                                            <el-input class="input-style" v-model="formData.nick_name" placeholder="请输入" size="large"  clearable></el-input>
                                        </el-form-item>
                                        <el-form-item label="姓名" prop="real_name">
                                            <el-input class="input-style" v-model="formData.real_name" placeholder="请输入" size="large" clearable></el-input>
                                        </el-form-item>
                                        <el-form-item label="年龄" prop="age">
                                            <el-input class="input-style" v-model="formData.age" placeholder="请输入" size="large" clearable></el-input>
                                        </el-form-item>
                                        <el-form-item label="手机" prop="phone">
                                            <el-input  class="input-style" v-model="formData.phone" placeholder="请输入" size="large" clearable></el-input>
                                        </el-form-item>
                                        <el-form-item label="邮箱" prop="email">
                                            <el-input class="input-style" v-model="formData.email" placeholder="请输入" size="large" clearable></el-input>
                                        </el-form-item>
                                        <el-form-item label="性别" prop="sex">
                                            <el-radio-group v-model="formData.sex"  >
                                                <el-radio value="男">男</el-radio>
                                                <el-radio value="女">女</el-radio>
                                            </el-radio-group>
                                        </el-form-item>
                                        <el-form-item>
                                            <el-button size="large"  class="add-btn" type="primary" @click="submit()">保存</el-button>
                                        </el-form-item>
                                    </el-form>
                    
                </div>
            </div>
        </div>
    </div>
</div>
</template>

<script setup>
import Header from '@/views/front/components/header.vue'
import { ref, reactive, onMounted} from 'vue'
import instances from "@/utils/request";
import utils from '@/utils/utils'
import ToolUtils from '@/utils/ToolUtils';
import UserCenterTab from './components/userCenterTab.vue'
const userStore = useUserStore()
import { useUserStore } from '@/store/userStore';
const formData= reactive({
    nick_name: userStore.user.nick_name,
    real_name: userStore.user.real_name,
    phone: userStore.user.phone,
    email: userStore.user.email,
    sex: userStore.user.sex,
    age: userStore.user.age,
    id: userStore.user.id
})
/**
 * 表单引用
 */
const formRef = ref()
/**
 * 表单校验
 */
const formRules= reactive({
    nick_name: [{ required: true, message: "昵称称长度为1-20", min: 1, max: 20, trigger: 'blur'}],
    real_name:[{ required: true, message: "姓名不能为空", trigger: 'blur' }],
    sex: [{ required: true, message: "性别不能为空", trigger: 'blur' }],
    email:  [{
        required: true, validator(_rule, value, callback, _source, _options) {
            if (ToolUtils.validateEmail(value)) {
                callback()
            }
            callback(new Error("邮箱格式不正确，请输入正确的邮箱"))
        }, trigger: 'blur'
    }],
    phone:   [{
        required: true, validator(_rule, value, callback, _source, _options) {
            if (ToolUtils.validatePhoneNumber(value)) {
                callback()
            }
            callback(new Error("手机号格式不正确，请输入正确的手机号"))
        }, trigger: 'blur'
    }],
})
/**
 * 
 * @param formEl 确认修改按钮事件
 */
 const submit = () => {
    formRef.value.validate(async (validate) => {
        if (validate) {
            instances.put("/user/user",formData).then(res => {
                utils.showSucess("修改成功!")  
                userStore.user.nick_name = formData.nick_name
                userStore.user.real_name = formData.real_name
                userStore.user.sex = formData.sex
                userStore.user.phone = formData.phone
                userStore.user.email = formData.email
                userStore.user.age = formData.age
            }).catch(_err => { 
                console.log(_err)
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
