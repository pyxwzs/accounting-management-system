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
                            登录
                        </h3>
                    </div>
                    <div class="panel-body">
                        <p> 输入您的账号和密码以访问管理系统界面。</p>
                        <el-form class="form-horizontal" ref="loginFormRef" :model="loginForm" :rules="loginRules">
                            <div class="form-group">
                                <div class="col-md-12">
                                    <el-form-item prop="username">
                                        <input type="text" class="form-control" v-model="loginForm.username" id="email" autocomplete="off"  placeholder="账号">
                                     </el-form-item>
                                    <i class="fa fa-user"></i>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-md-12">
                                    <el-form-item prop="password">
                                        <input type="password" class="form-control" v-model="loginForm.password" id="password" autocomplete="off" placeholder="密码">
                                     </el-form-item>
                                    <i class="fa fa-lock"></i>
                                    <!-- <router-link  to="/resetPwd" class="help-block">忘记密码?</router-link> -->
                                </div>
                            </div>
                            <div class="form-group">
                                
                                    <div class="col-md-6">
                                        <el-form-item prop="imgCode">
                                            <input class="form-control" placeholder="请输入图片验证码" v-model="loginForm.imgCode" />
                                        </el-form-item>
                                    </div>
                                    <div class="col-md-6">
                                        <canvas @click="getImgcode()" ref="canvasRef"></canvas>
                                    </div>
                                
                            </div>
                            <div class="form-group">
                                <div class="col-md-12">
                                    <a @click="submit()" class="btn btn-primary btn-block">登录</a>
                                    <hr>
                                    <!-- <router-link to="/register" class="btn btn-default btn-block">没有账号? 注册</router-link> -->
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
import { ref,onMounted ,reactive,nextTick} from 'vue'
import { User ,Lock,PictureRounded} from '@element-plus/icons-vue';
import  instances  from '@/utils/request'
import utils from '@/utils/utils'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/userStore';
import * as siteConfig  from "../../config/index";
const router = useRouter();
const userStore = useUserStore();
//验证码画布实例
const canvasRef=ref()
//验证码实例
const show_num = ref([]);
//登录表单的实例
const loginFormRef = ref(null);
//登录表单的数据
const loginForm = reactive({
    username:'',
    password:'',
    imgCode:''
})
//检测验证码是否正确
const validateImgCode = (rule, value, callback) => {
	if (value.toLowerCase() != show_num.value.join("")) {
		callback('验证码不正确')
	} else {
		callback()
	}
}
//登录表单校验规则
const loginRules = reactive({
    username: [
        { required: true, message: '请输入账号', trigger: 'blur' },
        { min: 5, max: 10, message: '长度在 5 到 10 个字符', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur'}
    ],
    imgCode: [
        { required: true, message: '请输入验证码', trigger: 'blur' },
        { min: 4, max: 4, message: '验证码为四个字符', trigger: 'blur'},
        {validator: validateImgCode,trigger: 'blur'}
    ]

})
//提交登录
const submit = () => {
    loginFormRef.value.validate((valid) => {
        if (valid) {
            //TODO 调用接口登录账号
            instances.get("/user/login",{params:loginForm}).then((res)=>{ 
                utils.showSucess("登录成功!")
                userStore.setUser(res.data.user)
                userStore.setToken(res.data.token)
                router.push('/')
            }).catch((error)=>{
                console.log(error);
            })  
        }else{
            console.log('error submit!!');
        }
    });
   
}
//得到随机的颜色值
const randomColor=()=> {
    let r = Math.floor(Math.random() * 256);
    let g = Math.floor(Math.random() * 256);
    let b = Math.floor(Math.random() * 256);
    return "rgb(" + r + "," + g + "," + b + ")"; 
}
//获取验证码方法
const getImgcode=()=>{
    nextTick();
    if (!canvasRef.value) {
        console.warn('Canvas element not found');
        return;
    }
    const context = canvasRef.value.getContext('2d');
    const canvas_width = 200;
    const canvas_height = 32;
    canvasRef.value.width=canvas_width;
    canvasRef.value.height=canvas_height;
    const sCode = "a,b,c,d,e,f,g,h,i,j,k,m,n,p,q,r,s,t,u,v,w,x,y,z,A,B,C,E,F,G,H,J,K,L,M,N,P,Q,R,S,T,W,X,Y,Z,1,2,3,4,5,6,7,8,9,0";
    const aCode = sCode.split(",");
    const aLength = aCode.length;//获取到数组的长度
    for (let i = 0; i < 4; i++) { //这里的for循环可以控制验证码位数（如果想显示6位数，4改成6即可）
        let j = Math.floor(Math.random() * aLength);//获取到随机的索引值
        // var deg = Math.random() * 30 * Math.PI / 180;//产生0~30之间的随机弧度
        let deg = Math.random() - 0.5; //产生一个随机弧度
        let txt = aCode[j];//得到随机的一个内容
        show_num.value[i] = txt.toLowerCase();
        let x = 15 + i * 40;//文字在canvas上的x坐标
        let y = 20 + Math.random() * 10;//文字在canvas上的y坐标
        context.font = "bold 23px 微软雅黑";
        context.translate(x, y);
        context.rotate(deg);
        context.fillStyle = randomColor();
        context.fillText(txt, 0, 0);
        context.rotate(-deg);
        context.translate(-x, -y);               
    }
    for (let i = 0; i <= 5; i++) { //验证码上显示线条
        context.strokeStyle = randomColor();
        context.beginPath();
        context.moveTo(Math.random() * canvas_width, Math.random() * canvas_height);
        context.lineTo(Math.random() * canvas_width, Math.random() * canvas_height);
        context.stroke();
    }
    for (let i = 0; i <= 30; i++) { //验证码上显示小点
        context.strokeStyle = randomColor();
        context.beginPath();
        let x = Math.random() * canvas_width;
        let y = Math.random() * canvas_height;
        context.moveTo(x, y);
        context.lineTo(x + 1, y + 1);
        context.stroke();
    }
}
onMounted(() => {
    getImgcode();
})
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
