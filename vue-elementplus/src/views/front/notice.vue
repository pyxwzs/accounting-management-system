<template>
<Header></Header>
<div class="inner-page-header">
    <div class="container">
        <h1 class="mb-8">系统公告</h1>
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><router-link  to="/front"><i class="fas fa-home"></i> 首页</router-link></li>
            <li class="breadcrumb-item active" aria-current="page">系统公告</li>
        </ol>
    </div>
</div>
<div class="services-page pt-80 pb-55">
    <div class="container">
        <div class="row row-cols-lg-3 row-cols-md-2 row-cols-sm-1 row-cols-1">
            <div class="col mb-25" v-for="(item,index) in pageTableData.list" :key="index">
                <!-- icon box 1 -->
                <div class="icon-box-1">
                    <div class="icon"><i class="fas fa-tags"></i></div>
                    <h3 class="title mb-20"><router-link  :to="{ path: '/notice/'+item.id }">{{item.title}}</router-link ></h3>
                    <p>{{item.create_time}}</p>
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
//调用后端api查询数据展示
const loadData = async () => {
    //调用分页接口API
    await instances.post("/notice/notices/",searchForm).then(res => {
        pageTableData.list = res.data.list
        pageTableData.total = res.data.total
    }).catch(err => { 
         console.error(err)
    })
}
//页面挂载后执行
onMounted(() => {
    loadData()
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

.title-notice{
    color: #4285f4 ;
    font-size: 13px;
}
.services-page {
    min-height: 58%;
}
</style>
