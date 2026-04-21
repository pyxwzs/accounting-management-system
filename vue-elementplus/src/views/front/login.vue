<template>


<Header></Header>
<div class="inner-page-header">
    <div class="container">
        <h1 class="mb-8">登录</h1>
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><router-link  to="/front"><i class="fas fa-home"></i> 首页</router-link></li>
            <li class="breadcrumb-item active" aria-current="page">登录</li>
        </ol>
    </div>
</div>

<div class="login-page pt-80 pb-80">
    <div class="container">
        <div class="row">
            <div class="col-lg-6 offset-lg-3">
              
                <div class="form-signin">
                    <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules">
                        <h5 class="mb-3">登录你的账号.</h5>
                        <div class="form-floating mb-2">
                            <el-form-item prop="username">
                                <input type="text" class="form-control"  placeholder="账号" v-model="loginForm.username">
                            </el-form-item>
                        </div>
                        <div class="form-floating mb-3">
                            <el-form-item prop="password">
                                <input type="password" class="form-control" placeholder="密码" v-model="loginForm.password">
                             </el-form-item>
                        </div>
                         <div class="form-floating mb-3">
                            <el-form-item prop="imgCode">
                                <div class="flex-item">
					                <input  type="text" class="form-control"  placeholder="请输入图片验证码" v-model="loginForm.imgCode" size="large" />
				                </div>
                                <div class="flex-item">
                                    <canvas @click="getImgcode()" ref="canvasRef"></canvas>
                                </div>
                            </el-form-item>   
                        </div>
                        <div class="d-flex justify-content-between">
                            <div class="mb-3">
                                <div class="form-check"></div>
                            </div>
                            <!-- <small class="forgot-pass text-muted mb-0"><router-link to="/resetPwd">忘记密码?</router-link></small> -->
                        </div>
                        <button class="w-100 btn-style-1" type="button" @click="submit()" >登录</button>
                        <div class="col-12 border-0 mb-0 pb-0 text-center mt-3 no-account">
                            <small class="text-muted me-2">没有账号?</small>
                            <router-link to="/frontRegister" class="text-dark">注册</router-link></div>
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
import { ref,onMounted ,reactive} from 'vue'
import { User ,Lock,PictureRounded} from '@element-plus/icons-vue';
import  instances  from '@/utils/request'
import utils from '@/utils/utils'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/userStore';
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
            instances.get("/user/loginFront",{params:loginForm}).then((res)=>{ 
                utils.showSucess("登录成功!")
                userStore.setUser(res.data.user)
                userStore.setToken(res.data.token)
                router.push('/front')
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
