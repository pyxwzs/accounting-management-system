<template>
<Header></Header>
<div class="inner-page-header">
    <div class="container">
        <h1 class="mb-8">注册</h1>
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><router-link  to="/front"><i class="fas fa-home"></i> 首页</router-link></li>
            <li class="breadcrumb-item active" aria-current="page">注册</li>
        </ol>
    </div>
</div>

<div class="login-page pt-80 pb-80">
    <div class="container">
        <div class="row">
            <div class="col-lg-6 offset-lg-3">
              
                <div class="form-signin">
                    <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules">
                        <h5 class="mb-3">注册新的账号.</h5>
                        <div class="form-floating mb-2">
                            <el-form-item prop="username">
                                <input type="text" class="form-control"  placeholder="账号" v-model="registerForm.username">
                            </el-form-item>
                        </div>
                        <div class="form-floating mb-3">
                            <el-form-item prop="password">
                                <input type="password" class="form-control" placeholder="密码" v-model="registerForm.password">
                             </el-form-item>
                        </div>
                        <div class="form-floating mb-3">
                            <el-form-item prop="password1">
                                <input type="password" class="form-control" placeholder="确认密码" v-model="registerForm.password1">
                             </el-form-item>
                        </div>
                        <div class="form-floating mb-2">
                            <el-form-item prop="email">
                                <input type="text" class="form-control"  placeholder="邮箱" v-model="registerForm.email">
                            </el-form-item>
                        </div>
                        <div class="d-flex justify-content-between">
                            <div class="mb-3">
                                <div class="form-check"></div>
                            </div>
                            <!-- <small class="forgot-pass text-muted mb-0"><router-link to="/resetPwd">忘记密码?</router-link></small> -->
                        </div>
                        <button class="w-100 btn-style-1" type="button" @click="submit()" >注册</button>
                        <div class="col-12 border-0 mb-0 pb-0 text-center mt-3 no-account">
                            <small class="text-muted me-2">已经有账号了?</small>
                            <router-link to="/frontLogin" class="text-dark">去登录</router-link></div>
                    </el-form>
                </div>
              
            </div>
        </div>
    </div>
</div>
<Footer></Footer>

</template>

<script setup>
import Header from '@/views/front/components/header.vue'
import Footer from '@/views/front/components/footer.vue'
import * as siteConfig  from "../../config/index";
import { User ,Lock,Message} from '@element-plus/icons-vue';
import { reactive,ref} from 'vue';
import  instances  from '@/utils/request'
import utils from '@/utils/utils'
import { useRouter } from 'vue-router'
const router = useRouter();
//注册表单的实例
const registerFormRef = ref(null);
//注册表单的数据
const registerForm = reactive({
    username:'',
    password:'',
    password1:'',
    email:''
})

const emit=defineEmits(['registerSuccess'])
//检测两次密码是否一致
const validatePass = (rule, value, callback) => {
		if (value != registerForm.password) {
			callback('两次密码不一致')
		} else {
			callback()
		}
}
//注册表单校验规则
const registerRules = reactive({
    username: [
        { required: true, message: '请输入账号', trigger: 'blur' },
        { min: 5, max: 10, message: '长度在 5 到 10 个字符', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur'}
    ],
    password1: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur'},
        {validator: validatePass,trigger: 'blur'}
    ],
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
    ]

})

//提交注册
const submit = () => {
    registerFormRef.value.validate((valid) => {
        if (valid) {
            instances.post("/user/register",registerForm).then((res)=>{ 
                utils.showSucess("注册成功!")   
                router.push('/frontLogin')
            }).catch((error)=>{
                console.log(error);
            })  
        } else{
            console.log('error submit!!');
        }
    });
}
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
.form-control{
    font-size: 13px;
    line-height: 2.5;
}
.footer-dark .footer-copyright{
    color: #fff;
}
.login-page {
    min-height: 58%;
}
</style>
