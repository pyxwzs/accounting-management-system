<template>
	<div class="success-box">
		<div class="success-icon">
			<el-icon>
				<SuccessFilled />
			</el-icon>
		</div>
		<div>
			<router-link to="/login" class="info-btn">密码重置成功，点击返回登录。</router-link>
		</div>
	</div>
</template>

<script>
	import {
		ref,
		reactive,
		onMounted,
		onUnmounted
	} from "vue";
	//引入工具方法
</script>

<style scoped="scoped">
	.success-box {
		text-align: center;
	}

	.success-icon {
		font-size: 100px;
		color: #409EFF;
		margin-top: 20px;
	}

	.info-btn {
		margin-top: 20px;
		font-size: 14px;
		color: #409EFF;
		text-decoration: none;
	}
</style>
