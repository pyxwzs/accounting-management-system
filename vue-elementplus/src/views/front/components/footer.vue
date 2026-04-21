<template>
<footer class="footer-dark pt-60">
    <div class="container">
        <div class="footer-contact-info-mainbox mb-35">   
        </div>
        <div class="row">
            <div class="col-lg-12">
                <ul class="footer-link list-inline">
                    <li class="list-inline-item"><router-link to="/front">首页</router-link></li>
                    <li class="list-inline-item"><router-link to="/article">新闻动态</router-link></li>
                    <li class="list-inline-item"><router-link to="/blog">论坛</router-link></li>
                    <li class="list-inline-item"><router-link to="/inbox">收件箱</router-link></li>
  
                </ul>
            </div>
        </div>
    </div>
    <div class="footer-copyright">©  All Rights Reserved.
     <a >{{siteConfig.siteName}}</a>
    </div>
</footer>
</template>

<script setup>
import {ElMessageBox} from 'element-plus'
import { useUserStore } from "@/store/userStore";
import * as siteConfig  from "@/config/index";
import { Search } from '@element-plus/icons-vue'
const userStore = useUserStore();
import { useRoute ,useRouter} from 'vue-router';
const route = useRoute();
const router = useRouter();
import { onMounted, reactive,computed, ref ,onBeforeUnmount} from "vue";
import instances from "@/utils/request";
import utils from '@/utils/utils'

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
.footer-dark .footer-copyright{
    color: #fff;
}
</style>
