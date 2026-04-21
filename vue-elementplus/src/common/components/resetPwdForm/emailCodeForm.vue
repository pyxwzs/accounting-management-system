<template>
		<div>
			<el-steps class="step" :active="stepActive" align-center>
			<el-step title="验证账号" description="" />
			<el-step title="重置密码" description="" />
			<el-step title="重置成功" description="" />
		</el-steps>
		<div class="form-box">
			<div v-if="stepActive == 1" class="form">
				<el-form ref="resetpwdFormRef" :model="resetpwdForm" :rules="rules" label-width="0" class="resetpwd-Form"
					 status-icon>
					<el-form-item prop="username">
						<el-input prefix-icon="UserFilled" v-model="resetpwdForm.username" placeholder="请输入用户名"
							size="large" />
					</el-form-item>
					<el-form-item prop="emailcode">
						<div class="flex resetpwd-line">
							<div class="flex-item">
								<el-input prefix-icon="Picture" v-model="resetpwdForm.emailcode" placeholder="请输入邮箱验证码"
									size="large" />
							</div>
							<div class="code-btn">
								<el-button type="primary" color="#556b8d" size="large" @click="getEmailCode" :disabled="curTime>0">
									{{emailCodeBtnText}}
								</el-button>
							</div>
						</div>
					</el-form-item>
					<el-form-item prop="imgCode">
						<div class="flex resetpwd-line">
							<div class="flex-item">
								<el-input prefix-icon="PictureRounded" v-model="resetpwdForm.imgCode" placeholder="请输入图片验证码"
									size="large" />
							</div>
							<div class="code-btn">
								<canvas @click="getImgcode()" ref="canvasRef"></canvas>
							</div>
						</div>
					</el-form-item>
					<el-form-item>
						<el-button type="primary" color="#556b8d" size="large" @click="onSubmit" class="resetpwd-btn">下一步</el-button>
					</el-form-item>
				</el-form>
			</div>
			<ResetPwdForm :sysmtetoken="sysmtetoken" v-else-if="stepActive == 2" @pre="toCordFrom" @next="toSuccess">
			</ResetPwdForm>
			<ResetSuccess v-else></ResetSuccess>
		</div>
	</div>
</template>

<script setup>
import {ref,reactive,onMounted,onUnmounted,onUpdated} from "vue";	
import utils from "@/utils/utils.js";
import ResetPwdForm from './ResetPwdForm.vue'
import ResetSuccess from "./ResetSuccess.vue";
import instances from "@/utils/request";
//当前的进度控制
const stepActive = ref(1);	
//验证码画布实例
const canvasRef=ref()
//登录表单的实例
const resetpwdFormRef = ref(null);
//验证码实例
const show_num = ref([]);
//登录表单的数据
const resetpwdForm = reactive({
	username: '',
	emailcode: '',
	imgCode: ''
});
//检测验证码是否正确
const validateImgCode = (rule, value, callback) => {
	if (value.toLowerCase() != show_num.value.join("")) {
		callback('验证码不正确')
	} else {
		callback()
	}
}
//表单的验证规则
const rules = reactive({
	username: [
        { required: true, message: '请输入账号', trigger: 'blur' },
        { min: 5, max: 10, message: '长度在 5 到 10 个字符', trigger: 'blur' }
    ],
	emailcode: [{
		required: true,
		message: '请输入邮箱验证码',
		trigger: 'blur'
	}],
	imgCode: [
        { required: true, message: '请输入验证码', trigger: 'blur' },
        { min: 4, max: 4, message: '验证码为四个字符', trigger: 'blur'},
        {validator: validateImgCode,trigger: 'blur'}
    ]
});
const emailCodeBtnText = ref("获取验证码");
const curTime = ref(0); //获取验证码的间隔时间
let timer = null; //定时器

//验证完成后后台返回的标识
const sysmtetoken = ref('');


//获取短信验证码
const getEmailCode = (async) => {
	if (!resetpwdForm.username) {
		utils.showError("请输入用户名");
		return;
	}
	if (!resetpwdForm.imgCode) {
		utils.showError("请输入图片验证码");
		return;
	}
	if (resetpwdForm.imgCode !== show_num.value.join("")) {
		utils.showError("图片验证码不正确");
		return;
	}
	//TODO 从后台获取邮箱验证码
	instances.get("/code/sendCode",{params:{username:resetpwdForm.username}}).then((res) => {
       	curTime.value = 60;
      	const startTime = Date.now();
      	localStorage.setItem('startTime', startTime);
      	localStorage.setItem('curTime', curTime.value);
      	startTimer(); 
		 utils.showSucess("发送成功，五分钟内有效")  
		 sysmtetoken.value=resetpwdForm.username
    }).catch(err => {
        console.error(err);
    })
	
}
const startTimer = () => {
  timer = setInterval(() => {
    const startTime = localStorage.getItem('startTime');
    const elapsedTime = (Date.now() - startTime) / 1000;
    curTime.value = 60 - Math.floor(elapsedTime);
    emailCodeBtnText.value = curTime.value + "秒后重新获取";
    if (curTime.value <= 0) {
      emailCodeBtnText.value = "获取验证码";
      clearInterval(timer);
      timer = null;
      localStorage.removeItem('startTime');
      localStorage.removeItem('curTime');
    }
  }, 1000);
};
//提交下一步
const onSubmit = () => {
	resetpwdFormRef.value.validate((valid) => {
		if (valid) {
			instances.get("/code/verifyCode",{params:{code:resetpwdForm.emailcode}}).then((res) => {
		 		stepActive.value = 2;
    		}).catch(err => {
        		console.error(err);
    		})
		}else{
			console.log('error submit!!');
            return false;
		}
	});
}

//返回上一步
const toCordFrom = () => {
	stepActive.value = 1;
}
//返回上一步
const toSuccess = () => {
	stepActive.value = 3;
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
    const canvas_width = 150;
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
        let x = 15 + i * 30;//文字在canvas上的x坐标
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
	const storedCurTime = localStorage.getItem('curTime');
  	const storedStartTime = localStorage.getItem('startTime');
  	if (storedCurTime && storedStartTime) {
    	const elapsedTime = (Date.now() - storedStartTime) / 1000;
    	curTime.value = 60 - Math.floor(elapsedTime);
    	if (curTime.value > 0) {
      		emailCodeBtnText.value = curTime.value + "秒后重新获取";
      		startTimer();
    	} else {
      		emailCodeBtnText.value = "获取验证码";
      		localStorage.removeItem('startTime');
      		localStorage.removeItem('curTime');
    }
  }
})
onUpdated(() => {
	if(canvasRef.value){
		getImgcode();
	}
	resetpwdForm.imgCode = '';
})
onUnmounted(() => {
	clearInterval(timer);
  	timer = null;
});
</script>

<style scoped>
	.step {
		max-width: 700px;
		margin: 0 auto;
	}

	.form-box {
		max-width: 500px;
		margin: 0 auto;
		margin-top: 30px;
	}

	.resetpwd-btn {
		width: 100%;
	}

	.resetpwd-line {
		width: 100%;
	}

	.code-btn {
		width: 150px;
		margin-left: 10px;
	}

	.code-btn:deep(.el-button),
	.code-btn:deep(img) {
		width: 100px;
	}

	.code-btn:deep(img) {
		height: 40px;
		cursor: pointer;
	}
</style>
