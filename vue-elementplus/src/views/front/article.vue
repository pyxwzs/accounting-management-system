<template>
<Header></Header>
<div class="inner-page-header">
    <div class="container">
        <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-4">
            <h1 class="mb-0">财经资讯</h1>
            <el-button v-if="userStore.token" type="primary" size="small" :loading="syncing" @click="syncRss">
                同步最新资讯
            </el-button>
        </div>
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><router-link  to="/front"><i class="fas fa-home"></i> 首页</router-link></li>
            <li class="breadcrumb-item active" aria-current="page">财经资讯</li>
        </ol>
    </div>
</div>
<div class="services-page pt-80 pb-55">
    <div class="container">
        <div class="row row-cols-lg-3 row-cols-md-2 row-cols-sm-1 row-cols-1">
            <div class="col mb-25" v-for="(item,index) in pageTableData.list" :key="index">
               
                <div class="image-box2">
                    <div class="number">{{new Date(item.create_time).format('YY-MM-DD')}}</div>
                    <h4 class="title mb-10"><router-link :to="{ path: '/articleDetails/'+item.id }">{{item.name}}</router-link></h4>
                    <p class="content-preview">{{removeHtmlTagsSafely(item.content)}}</p>
                    <div class="article-card-actions mt-10">
                        <a v-if="item.source_url" :href="item.source_url" target="_blank" rel="noopener noreferrer" class="read-original">阅读原文</a>
                        <span v-if="userStore.token" class="fav-wrap" @click.stop="toggleFavorite(item)" title="收藏">
                            <i class="fas fa-star" :class="{ 'is-fav': item.favorited }"></i>
                        </span>
                    </div>
                    <div v-if="item.img" class="img"><img :src="item.img" alt=""></div>
                </div>
            
            </div>
           
        </div>
        <div class="pagination-container pagination text-center">
				<el-pagination v-model:current-page="searchForm.current" background v-model:page-size="searchForm.size"
                                    layout=" prev, pager, next" :total="pageTableData.total"
                                        @current-change="handleCurrentChange"  />
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
import { useUserStore } from '@/store/userStore'
import { useRoute } from 'vue-router'

const route = useRoute();
const userStore = useUserStore();
const syncing = ref(false);
//分页列表响应数据
const pageTableData = reactive({
    list: [],
    total: 0
})
//搜索表单数据实体
const searchForm=reactive({
    size: 12,
    current: 1
}) 
/**
 * 手动切换当前第几页事件
 */
const handleCurrentChange = (vari) => {
    searchForm.current = vari
    loadData()
}
const removeHtmlTagsSafely= (html) => {
    if (!html) return '';
    const div = document.createElement('div');
    div.innerHTML = html;
    return div.textContent || div.innerText || '';
}
/**
 * 调用后端api查询数据展示
 */
const syncRss = async () => {
    syncing.value = true;
    try {
        const res = await instances.post("/article/sync-rss");
        utils.showSucess(`已同步 ${res.data.imported} 条资讯`);
        await loadData();
    } catch (e) {
        console.error(e);
    } finally {
        syncing.value = false;
    }
};
const toggleFavorite = async (item) => {
    if (!userStore.token) return;
    try {
        const res = await instances.post("/article/favorite/toggle", { article_id: item.id });
        item.favorited = res.data.favorited;
    } catch (e) {
        console.error(e);
    }
};
const loadData = async () => {
    //调用分页接口API
    searchForm.type=route.params.type
    await instances.post("/article/articles/",searchForm).then(res => {
        pageTableData.list = res.data.list
        pageTableData.total = res.data.total
    }).catch(err => { 
         console.error(err)
    })
}
onMounted(() => {
   loadData();
})
watch(
  () => route.path,
  (newPath) => {
   loadData();
  }
);
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
.image-box2 .title{
    /* 显示三行文本 */
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
}
.image-box2 img{
    height: 100% !important;
    width:100% !important;
}
.image-box2 .number{
    font-size: 36px;
}
.services-page {
    min-height: 58%;
}
.article-card-actions {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 14px;
}
.read-original {
    color: #1de278;
    text-decoration: none;
}
.read-original:hover {
    text-decoration: underline;
}
.fav-wrap {
    cursor: pointer;
    color: #ccc;
}
.fav-wrap .fa-star.is-fav {
    color: #ffc107;
}
</style>
