<template>
<div class="body">
    <section class="container animated fadeInUp">
    <div class="row">
        <div class="col-md-6 col-md-offset-3">
            <div id="login-wrapper">
                <header>
                    <div class="brand">
                        <a  class="logo">
                            <i class="icon-layers"></i>&nbsp;
                            <span>{{siteConfig.siteName}}</span>
                        </a>
                    </div>
                </header>
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title">
                            注册
                        </h3>
                    </div>
                    <div class="panel-body">
                        <p> 输入您的账号和密码和其他信息注册一个新的账号。</p>
                        <el-form class="form-horizontal" ref="registerFormRef" :model="registerForm" :rules="registerRules">
                            <div class="form-group">
                                <div class="col-md-12">
                                    <el-form-item prop="username">
                                        <input type="text" class="form-control" id="email" v-model="registerForm.username" autocomplete="off"  placeholder="账号">
                                     </el-form-item>
                                    <i class="fa fa-user"></i>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-md-12">
                                    <el-form-item prop="password">
                                        <input type="password" class="form-control" id="password" v-model="registerForm.password" autocomplete="off" placeholder="密码">
                                     </el-form-item>
                                    <i class="fa fa-lock"></i>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-md-12">
                                    <el-form-item prop="password1">
                                        <input type="password" class="form-control" id="password" v-model="registerForm.password1" autocomplete="off" placeholder="确认密码">
                                     </el-form-item>
                                    <i class="fa fa-lock"></i>
                                </div>
                            </div>
                             <div class="form-group">
                                <div class="col-md-12">
                                    <el-form-item prop="email">
                                        <input type="text" class="form-control" id="email" v-model="registerForm.email" autocomplete="off" placeholder="邮箱 ">
                                     </el-form-item>
                                    <i class="fa fa-envelope"></i>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-md-12">
                                    <a @click="submit()" class="btn btn-primary btn-block">注册</a>
                                    <hr>
                                    <router-link to="/login" class="btn btn-default btn-block">已经有账号了? 登录</router-link>
                                </div>
                            </div>
                        </el-form>
                    </div>
                </div>
            </div>
        </div>
    </div>

</section> 
</div>
</template>

<script setup>
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
                router.push('/login')
            }).catch((error)=>{
                console.log(error);
            })  
        } else{
            console.log('error submit!!');
        }
    });
}
</script>
<style src="@/assets/styles/bootstrap.min.css" scoped></style>
<style src="@/assets/styles/font-awesome.min.css" scoped></style>
<style src="@/assets/styles/simple-line-icons.css" scoped></style>
<style src="@/assets/styles/animate.css" scoped></style>
<style src="@/assets/styles/main.css" scoped></style>
<style scoped>
.body{
    height: 100vh;
    background-image: url(@/assets/images/img-3.jpg);
    background-size: cover;
 
}
</style>
