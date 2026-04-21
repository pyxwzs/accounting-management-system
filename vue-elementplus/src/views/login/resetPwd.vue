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
                            找回密码
                        </h3>
                    </div>
                    <div class="panel-body">
                        <p> 输入您的电子邮件地址，我们会向您发送一封电子邮件，其中包含找回密码所需的验证码信息。</p>
                        <div class="form-horizontal" >
                            <EmailCodeForm></EmailCodeForm>
                            <div class="form-group">
                                <div class="col-md-12">
                                    
                                    <router-link to="/login" class="btn btn-default btn-block">返回到登录</router-link>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

</section> 
</div>
</template>

<script setup>
import {ref,onMounted} from "vue";
import * as siteConfig  from "../../config/index";
import EmailCodeForm from '@/common/components/resetPwdForm/emailCodeForm.vue'
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
