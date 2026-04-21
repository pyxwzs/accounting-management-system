<template>
    <Header></Header>
    <div class="slider">
        <div id="carouselsliderControls" class="carousel slide carousel-fade" data-bs-ride="carousel">
            <div class="carousel-inner">
                <el-carousel height="700px">
                    <el-carousel-item v-for="(item,index) in slideshows" :key="index">
                        <div class="carousel-item slider-one active" :style="{ backgroundImage: `url(${item.img})`, backgroundSize: 'cover', backgroundPosition: 'center' }">
                            <div class="carousel-caption">
                                <div class="container">
                                    <div class="row">
                                        <div class="col-lg-6">
                                            <div class="slider-caption-box">
                                                <h2>{{item.article.name}}</h2>
                                                <router-link :to="{ path: '/articleDetails/'+item.item_id }" class="btn-style-1" >查看更多</router-link></div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </el-carousel-item>
                </el-carousel>
            </div>
        </div>
    </div>

<div class="blog-area pt-80 pb-55">
    <div class="container">
        <!-- section title -->
        <div class="section-title mb-30 text-center">
            
            <h2>财经资讯</h2>
        </div>
        <!-- section title end -->
        <div class="row row-cols-lg-2 row-cols-md-2 row-cols-sm-1 row-cols-1">
            <div class="col mb-25" v-for="(item,index) in articles" :key="index">
                <!-- blog item 3 -->
                <div class="blog-item-3">
                    <div class="row align-items-center">
                        <div v-if="item.img" class="col-lg-5 mb-15">
                            <div class="blog-img">
                                <img :src="item.img" alt="">
                                <router-link :to="{ path: '/articleDetails/'+item.id }" class="btn-style-3 btn-sm">查看更多</router-link>
                            </div>
                        </div>
                        <div :class="item.img ? 'col-lg-7 mb-15' : 'col-12 mb-15'">
                            <div class="blog-content">
                                <h2 class="title">
                                    <router-link :to="{ path: '/articleDetails/'+item.id }" >{{item.name}}</router-link>
                                </h2>
                                <p class="content-preview">{{removeHtmlTagsSafely(item.content)}}</p>
                                <div class="meta">
                                    <a><strong>时间 :</strong>{{new Date(item.create_time).format('YY-MM-DD HH:mm')}}</a>
                                </div>
                                <router-link v-if="!item.img" :to="{ path: '/articleDetails/'+item.id }" class="btn-style-3 btn-sm mt-10 d-inline-block">查看更多</router-link>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- blog item 3 end -->
            </div>
          
        </div>
    </div>
</div>
<Footer></Footer>

</template>

<script setup>
import Header from '@/views/front/components/header.vue'
import Footer from '@/views/front/components/footer.vue'
import { ref, reactive, onMounted} from 'vue'
import instances from "@/utils/request";
import utils from '@/utils/utils'
import * as siteConfig  from "@/config/index";
import { useUserStore } from "@/store/userStore";
const userStore = useUserStore();
const slideshows=ref([])
const articles=ref([])
const blogs=ref([])
const removeHtmlTagsSafely= (html) => {
    if (!html) return '';
    const div = document.createElement('div');
    div.innerHTML = html;
    return div.textContent || div.innerText || '';
}
const loadBold=()=>{
	instances.post("/blog/blogs/",{'size':8,'current':1}).then((res) => {
		blogs.value=res.data.list
	}).catch(err => { 
		console.error(err)
	})
}
const loadArticle=()=>{
	instances.post("/article/articles/",{'size':8,'current':1}).then((res) => {
		articles.value=res.data.list
	}).catch(err => { 
		console.error(err)
	})
}
const loadSlideshow=()=>{
	instances.get("/slideshow/slideshows").then((res) => {
		slideshows.value=res.data
	}).catch(err => { 
		console.error(err)
	})
}

onMounted(() => {
	loadSlideshow()
	
	loadArticle()
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
h2{
    font-size: 20px !important;
}
.blog-img img{
    height: 100% !important;
    width:100% !important;
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
.blog-img img{
    height: 280px !important;
    width:100% !important;
}
.call-to-action-area{
    background-image: url(@/assets/images/call-to-action-bg.jpg);
    background-size: cover;
    background-position: center top;
    position: relative;
    z-index: 0;
    height: 300px !important; 
}
</style>
