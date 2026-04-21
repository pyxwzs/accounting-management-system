<template>
    <el-form  class="register-form" ref="registerFormRef" :model="registerForm" :rules="registerRules">
        <h1 class="register-title">注册</h1>
        <el-form-item prop="username">
            <el-input placeholder="请输入账号" v-model="registerForm.username" :prefix-icon="User"  size="large"></el-input>
        </el-form-item>
        <el-form-item prop="password">
            <el-input placeholder="请输入密码" v-model="registerForm.password" show-password :prefix-icon="Lock"  size="large"></el-input>
        </el-form-item>
        <el-form-item prop="password1">
            <el-input placeholder="再次输入密码" v-model="registerForm.password1" show-password :prefix-icon="Lock"  size="large"></el-input>
        </el-form-item>
        <el-form-item prop="email">
            <el-input placeholder="请输入邮箱" v-model="registerForm.email" :prefix-icon="Message"  size="large"></el-input>
        </el-form-item>
        <el-form-item>
            <el-button type="primary" class="register-btn" size="large" @click="submit()">注册</el-button>
        </el-form-item>
    </el-form >
</template>

<script setup>
import { User ,Lock,Message} from '@element-plus/icons-vue';
import { reactive,ref} from 'vue';
import  instances  from '@/utils/request'
import utils from '@/utils/utils'
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
                emit('registerSuccess')
            }).catch((error)=>{
                console.log(error);
            })  
        } else{
            console.log('error submit!!');
        }
    });
}
</script>

<style scoped>
.register-form {
    grid-row: 1;
    grid-column: 1;
    opacity: 0;
    transition: 1s ease-in-out;
    /* 上下 | 左右 */
    padding: 1% 25%;
    z-index: 0;
}

.register-form.sign-up-model {
    opacity: 1;
    transition: 1s ease-in-out;
    transition-delay: 0.5s;
    z-index: 1;
}

.register-title{
    text-align: center;
    color: #444;
}
.register-btn{
    width: 100%;
    font-size: 18px;
}
</style>
