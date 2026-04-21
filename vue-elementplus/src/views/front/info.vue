<template>
<Header></Header>
<div class="inner-page-header">
    <div class="container">
        <h1 class="mb-8">收支记录</h1>
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><router-link  to="/front"><i class="fas fa-home"></i> 首页</router-link></li>
            <li class="breadcrumb-item active" aria-current="page">收支记录</li>
        </ol>
    </div>
</div>
<div class="services-page pt-80 pb-55">
    <div class="container">
       <el-form :inline="true" :model="searchForm" ref="searchFormRef">
                <el-form-item prop="name" label="记录名称">
                    <el-input v-model="searchForm.name" placeholder="请输入记录名称" clearable>
                    </el-input>
                </el-form-item>
                <el-form-item label="类型" prop="type">
                    <el-select v-model="searchForm.type" placeholder="请选择类型" clearable style="width: 120px;">
                        <el-option label="收入" value="收入" />
                        <el-option label="支出" value="支出" />
                    </el-select>
                </el-form-item>
                <el-form-item label="日期范围" prop="timeRange">
                    <el-date-picker v-model="searchForm.timeRange" type="daterange" range-separator="-"
                        start-placeholder="开始时间" end-placeholder="结束时间" format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD" />
                </el-form-item>
                <el-form-item>
                    <el-button type="success" :icon="Search" @click="handleQuery" plain>搜索</el-button>
                    <el-button type="info" :icon="Refresh" @click="handleRest" plain>重置</el-button>
                </el-form-item>
            </el-form>
            <div>
                <el-button  type="primary" :icon="Plus"  plain @click="openDialog()">新增</el-button>
                <el-button  type="danger" :icon="Delete" :disabled="!selectIds.length" plain
                    @click="handleMultipleDelete()">批量删除</el-button>
            </div>
       
        <el-table :data="pageTableData.list" style="width: 100%" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" />
            <el-table-column prop="name" label="名称"  />
            <el-table-column prop="price" label="金额"  />
            <el-table-column prop="type" label="类型" width="80">
                <template #default="scope">
                    <el-tag v-if="scope.row.type === '收入'" type="success">收入</el-tag>
                    <el-tag v-else type="warning">支出</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="classify" label="分类"  />
            <el-table-column prop="budget_name" label="预算"  />
            <el-table-column prop="content" label="备注"  />
            <el-table-column prop="day" label="日期" align="center" :show-overflow-tooltip="true" width="180" />
            <el-table-column prop="create_time" label="创建日期" align="center" :show-overflow-tooltip="true" width="180" />
            <el-table-column fixed="right" align="center" label="操作" width="300">
                <template #default="scope">
                    <el-button :icon="Edit" type="warning" @click="openDialog(scope.row.id)" plain size="small">编辑</el-button>
                    <el-button :icon="Delete" type="danger" @click="handleDelete(scope.row.id)" plain size="small">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <div class="pagination-container">
            <el-pagination v-model:current-page="searchForm.current" v-model:page-size="searchForm.size"
                :page-sizes="[10, 30, 50, 100]" :small=false :disabled=false :background=true
                layout="total, sizes, prev, pager, next, jumper" :total="pageTableData.total"
                @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>
    </div>
</div>
 <AddOrUpdate ref="addOrUpdatemodal" @success="addOrUpdateSuccess"></AddOrUpdate>
<Footer></Footer>
</template>

<script setup>
import Header from '@/views/front/components/header.vue'
import Footer from '@/views/front/components/footer.vue'
import { ref, reactive, onMounted,watch} from 'vue'
import {ElMessageBox ,ElNotification} from 'element-plus'
import instances from "@/utils/request";
import utils from '@/utils/utils'
import { useRoute ,useRouter} from 'vue-router'
import ToolUtils from '@/utils/ToolUtils';
import * as siteConfig  from "@/config/index";
import AddOrUpdate from '../system/info/components/add-or-update.vue';
import { useUserStore } from "@/store/userStore";
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
 * 新增/编辑组件实体引用
 */
const addOrUpdatemodal = ref(null)
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
 * 点击查询按钮事件
 */
const handleQuery = () => {
    loadData()
}
/**
 * 重置分页查询form表单
 */
const handleRest = () => {
    searchFormRef.value.resetFields()
    loadData()
}
/**
 * 
 * 分页左下角切换当前页条数事件
 */
const handleSizeChange = (vari) => {
    searchForm.current = 1
    loadData()
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
    if (searchForm.timeRange) {
        searchForm.day = searchForm.timeRange[0]+' - '+ searchForm.timeRange[1]
    } else {
        searchForm.day = undefined
    }
    //调用分页接口API
     searchForm.user_id=userStore.user.id
    await instances.post("/info/infos/",searchForm).then(res => {
        pageTableData.list = res.data.list
        pageTableData.total = res.data.total
    }).catch(err => { 
         console.error(err)
    })
}
/**
 * 
 * 新增/更新弹框事件
 */
const openDialog = (id) => {
    addOrUpdatemodal.value.openDialog(id)
}
/**
 * 新增/更新成功回调事件
 */
const addOrUpdateSuccess = () => {
    loadData()
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
        await instances.post("/info/delete/",ids).then((res) => {
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

.title-notice{
    color: #4285f4 ;
    font-size: 13px;
}
.services-page {
    min-height: 58%;
}
.pagination-container {
    padding: 10px 0px 0px;
}
:deep(.el-pager li.is-active){
  color: #dead6f;
}
:deep(.el-pager li:hover){
  color: #dead6f;
}
:deep(.el-button){
    font-size: 14px;
}

</style>
