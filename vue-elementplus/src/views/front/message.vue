<template>
<Header></Header>
<div class="inner-page-header">
    <div class="container">
        <h1 class="mb-8">个人中心</h1>
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><router-link  to="/front"><i class="fas fa-home"></i> 首页</router-link></li>
            <li class="breadcrumb-item active" aria-current="page">个人中心</li>
        </ol>
    </div>
</div>
<div class="single-services-page pt-80 pb-55">
    <div class="container">
        <div class="row">
            <UserCenterTab activeTabs="消息列表"></UserCenterTab>
            <div class="col-lg-9 mb-25">
                <div class="single-service-text">
                    <div style="margin-bottom: 10px">
                        <el-button  style="font-size:14px" type="danger" :icon="Delete" size="small"  :disabled="!selectIds.length" 
                            @click="handleMultipleDelete()">
                        </el-button>
                    </div>
                    <el-table :data="pageTableData.list" style="width: 100%" @selection-change="handleSelectionChange" stripe :header-cell-style="{fontSize: '13px !important' ,color:'#111  !important'}"  size="large">
                        <el-table-column type="selection" width="55" />                   
                        <el-table-column prop="content" >
                            <template #default="scope">
                                <p v-html="scope.row.content"></p>
                            </template>
                        </el-table-column>
                        <el-table-column prop="create_time"  align="center" :show-overflow-tooltip="true" width="180" />
                        <el-table-column fixed="right" align="center"  width="100">
                            <template #default="scope">
                                <div class="flex">
                                    <div class="flex-item"> 
                                        <el-button :icon="Delete" @click="handleDelete(scope.row.id)"  circle size="small"></el-button>
                                    </div>
                                </div>                  
                            </template>
                        </el-table-column>
                    </el-table>
                    <div class="pagination-container">
                        <el-pagination v-model:current-page="searchForm.current" v-model:page-size="searchForm.size" background 
                            layout=" prev, pager, next" :total="pageTableData.total"
                            @current-change="handleCurrentChange"/>
                     </div>
                </div>
            </div>
        </div>
    </div>
</div>

</template>

<script setup>
import {ElMessageBox} from 'element-plus'
import Header from '@/views/front/components/header.vue'
import { ref, reactive, onMounted} from 'vue'
import {  Delete, Edit, Plus, Refresh, Search, Sort } from '@element-plus/icons-vue';
import instances from "@/utils/request";
import utils from '@/utils/utils'
import UserCenterTab from './components/userCenterTab.vue'
import { useUserStore } from '@/store/userStore';
const userStore = useUserStore();
/**
 * 搜索表单组件
 */
const searchFormRef=ref()
/**
 * 搜索表单数据实体
 */
const searchForm=reactive({
    size: 20,
    current: 1
})
/**
 * 分页列表响应数据
 */
const pageTableData = reactive({
    list: [],
    total: 0
})

/**
 * 列表选中数据id集合
 */
const selectIds = ref([])
/**
 * 列表选中监听事件
 * @param row 
 */
const handleSelectionChange = (row) => {
    selectIds.value = row.map(item => {
        return item.id
    })
}



/**
 * 手动切换当前第几页事件
 */
const handleCurrentChange = (vari) => {
    searchForm.current = vari
    loadData()
}
/**
 * 调用后端api查询数据展示
 */
const loadData = async () => {
    searchForm.type="0"
    searchForm.user_id=userStore.user.id
    //调用分页接口API
    await instances.post("/message/messages/",searchForm).then(res => {
        pageTableData.list = res.data.list
        pageTableData.total = res.data.total
        console.log(res.data.list)
    }).catch(err => { 
         console.error(err)
    })
}


/**
 * 
 * 列表工具栏删除
 */
const handleDelete = (id) => {
    openDeleteDialog([id])
}
/**
 * 批量删除
 */
const handleMultipleDelete = () => {
    openDeleteDialog(selectIds.value)
}
/**
 * 删除函数
 * @param ids 用户id
 */
const openDeleteDialog = (ids) => {
    ElMessageBox.confirm(`确认删除已选中的数据项?`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        //调用删除接口API
        await instances.post("/message/delete/",ids).then((res) => {
        utils.showSucess("删除成功!")   
        loadData()
        }).catch(_err => { 
            console.error(_err)
        })
    });
}
/**
 * 页面挂载后执行
 */
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
.single-service-text{
    border: 1px solid #dee2e6;
    padding: 20px;
    /* box-shadow:  0px 0px 12px rgba(0, 0, 0, .12); */
}
:deep(.el-pager li.is-active){
  background-color: #1de278!important;
}
:deep(.el-pager li:hover){
  background-color: #1de278!important;
}
.pagination-container {
    padding: 10px 0px 0px;
}
</style>
