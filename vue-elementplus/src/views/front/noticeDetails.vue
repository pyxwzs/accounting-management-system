<template>
<Header></Header>
<div class="inner-page-header">
    <div class="container">
        <h1 class="mb-8">公告详情</h1>
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><router-link  to="/front"><i class="fas fa-home"></i> 首页</router-link></li>
            <li class="breadcrumb-item"><router-link to="/inbox?tab=notice">收件箱</router-link></li>
            <li class="breadcrumb-item active" aria-current="page">公告详情</li>
        </ol>
    </div>
</div>
<div class="blog-post-page pt-80 pb-55">
    <div class="container">
        <div class="row">
            <div class="col-lg-8 offset-lg-2">
                <!-- blog post content -->
                <div class="blog-post-content">
                    
                    <h2 class="mb-15">{{notice.title}}</h2>
                    <p v-html="notice.content"></p>
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
import { ref, reactive, onMounted,watch} from 'vue'
import instances from "@/utils/request";
import utils from '@/utils/utils'
import { useRoute ,useRouter} from 'vue-router'
import ToolUtils from '@/utils/ToolUtils';
import * as siteConfig  from "@/config/index";
const route = useRoute();
/**
 * 公共详情对象
 */
const notice=ref({})
/**
 * 调用后端api查询数据展示
 */
const loadData = async () => {
    //调用接口API
    await instances.get(`/notice/notice/${route.params.id}`).then((res) => {
        notice.value = res.data;
    }).catch(err => { 
         console.error(err)
    })
}
 onMounted(() => {
    loadData();
});
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
.blog-post-page {
    min-height: 58%;
}
</style>
