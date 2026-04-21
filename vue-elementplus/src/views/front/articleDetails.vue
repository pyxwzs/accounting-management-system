<template>
<Header></Header>
<div class="inner-page-header">
    <div class="container">
        <h1 class="mb-8">财经资讯</h1>
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><router-link  to="/front"><i class="fas fa-home"></i> 首页</router-link></li>
            <li class="breadcrumb-item active" aria-current="page">财经资讯</li>
        </ol>
    </div>
</div>
<div class="blog-post-page pt-80 pb-55">
    <div class="container">
        <div class="row">
            <div class="col-lg-8 offset-lg-2">
                <!-- blog post content -->
                <div class="blog-post-content">
                    <div v-if="article.img" class="blog-post-img mb-20">
                        <img :src="article.img" alt="">
                    </div>
                    <div class="blog-post-meta mb-20 d-flex flex-wrap align-items-center gap-3">
                            <a><strong>时间 :</strong>{{new Date(article.create_time).format('YY-MM-DD HH:mm')}}</a>
                            <a v-if="article.source_url" :href="article.source_url" target="_blank" rel="noopener noreferrer" class="read-original-link">阅读原文</a>
                            <span v-if="userStore.token" class="fav-detail" @click="toggleFavorite" title="收藏">
                                <i class="fas fa-star" :class="{ 'is-fav': article.favorited }"></i>
                            </span>
                    </div>
                    <h2 class="mb-15">{{article.name}}</h2>
                    <p v-html="article.content"></p>
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
import { ref, onMounted } from 'vue'
import instances from "@/utils/request";
import { useUserStore } from '@/store/userStore'
import { useRoute } from 'vue-router'
const route = useRoute();
const userStore = useUserStore();
const toggleFavorite = async () => {
    if (!userStore.token || !article.value.id) return;
    try {
        const res = await instances.post("/article/favorite/toggle", { article_id: article.value.id });
        article.value.favorited = res.data.favorited;
    } catch (e) {
        console.error(e);
    }
};
/**
 * 公共详情对象
 */
const article=ref({})
/**
 * 调用后端api查询数据展示
 */
const loadData = async () => {
    //调用接口API
    await instances.get(`/article/article/${route.params.id}`).then((res) => {
        article.value = res.data;
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
:deep(.el-pager li.is-active){
  background-color: #1de278!important;
}
:deep(.el-pager li:hover){
  background-color: #1de278!important;
}
.pagination-container {
    padding: 10px 0px 0px;
}
.content-preview{
    /* 显示三行文本 */
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
    
    /* 确保文本不换行，但会被 CSS 截断 */
    word-break: break-all;
    line-height: 1.5; /* 调整行高以适应设计 */
    max-height: 4.5em; /* 3行 * 1.5行高 = 4.5em */
}
.blog-post-img img{
    height:300px !important;
    width:100% !important;
    object-fit: cover;  /* 裁剪多余部分 */
    object-position: center; /* 裁剪时保持中心位置 */
}
.blog-page {
    min-height: 58%;
}
.blog-post-meta{
    color: #fff;
}
.read-original-link {
    color: #1de278 !important;
    text-decoration: none;
}
.read-original-link:hover {
    text-decoration: underline;
}
.fav-detail {
    cursor: pointer;
    color: #ccc;
}
.fav-detail .fa-star.is-fav {
    color: #ffc107;
}
</style>
