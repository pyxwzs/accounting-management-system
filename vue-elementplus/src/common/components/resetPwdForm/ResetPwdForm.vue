<template>
	<el-form ref="resetpwdFormRef" :model="resetpwdForm" :rules="rules" label-width="0" class="resetpwd-Form"
		:size="formSize" status-icon>
		<el-form-item prop="newPwd">
			<el-input type="password" prefix-icon="Lock" v-model="resetpwdForm.newPwd" placeholder="请输入新密码"
				size="large" />
		</el-form-item>
		<el-form-item prop="password1">
			<el-input type="password" prefix-icon="Lock" v-model="resetpwdForm.password1" placeholder="请输入确认密码"
				size="large" />
		</el-form-item>
		<el-form-item>
			<div class="flex resetpwd-line">
				<div class="flex-item">
					<el-button type="info"  color="#556b8d" size="large" @click="onPreStep" class="resetpwd-btn">上一步</el-button>
				</div>
				<div style="width:20px;height:20px"></div>
				<div class="flex-item">
					<el-button type="primary" color="#556b8d" size="large" @click="onSubmit" class="resetpwd-btn">下一步</el-button>
				</div>
			</div>
		</el-form-item>
	</el-form>
</template>

<script setup>
import {ref,reactive,onMounted,onUnmounted} from "vue";
//引入工具方法
import utils from "@/utils/utils.js";
import instances from "@/utils/request";

//外部的参数，需要传递上一步获取的账号验证token信息
const option = defineProps({
	sysmtetoken: {
		type: String,
		required: true
	}
});

//自定义事件
const emits = defineEmits(['pre', 'next']);

//登录表单的实例
const resetpwdFormRef = ref(null);
//登录表单的数据
const resetpwdForm = reactive({
	newPwd: '',
	password1: ''
});
//检测两次密码是否一致
const validatePass = (rule, value, callback) => {
	if (value != resetpwdForm.newPwd) {
		callback('两次密码不一致')
	} else {
		callback()
	}
}
//表单校验规则
const rules = reactive({
    newPwd: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur'}
    ],
    password1: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur'},
        {validator: validatePass,trigger: 'blur'}
    ],
})
//提交
const onSubmit = () => {
		resetpwdFormRef.value.validate((valid, fileds) => {
			if (valid) {
				instances.put("/user/resetPwd",{username:option.sysmtetoken,newPwd:resetpwdForm.newPwd}).then((res) => {
		 			emits('next');
    			}).catch(err => {
        			console.error(err);
    			})
				
			}
		});
	}

	//返回上一步
	const onPreStep = () => {
		emits('pre');
	}
</script>

<style scoped="scoped">
	.resetpwd-line {
		width: 100%;
	}

	.resetpwd-btn {
		width: 100%;
	}
</style>
