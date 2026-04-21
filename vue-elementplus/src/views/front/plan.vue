<template>
<Header></Header>
<div class="inner-page-header">
    <div class="container">
        <h1 class="mb-8">存钱计划</h1>
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><router-link  to="/front"><i class="fas fa-home"></i> 首页</router-link></li>
            <li class="breadcrumb-item active" aria-current="page">存钱计划</li>
        </ol>
    </div>
</div>
<div class="services-page pt-80 pb-55">
    <div class="container">
       <el-form :inline="true" :model="searchForm" ref="searchFormRef">
                <el-form-item prop="name" label="计划名称">
                    <el-input v-model="searchForm.name" placeholder="请输入计划名称" clearable>
                    </el-input>
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

            <el-alert type="info" show-icon class="plan-tip" title="进度与预测说明" 
                description="存钱进度 = 已存入 / 目标；时间进度 = 已过天数 / 计划总天数。完成预测依据您近30天「收支记录」中的收入与支出，用日均结余与剩余目标、剩余天数估算是否有望按期存满。" />
       
        <el-table :data="pageTableData.list" style="width: 100%" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" />
            
            <el-table-column prop="name" label="计划名称"  />
            <el-table-column prop="price" label="目标金额"  />
            <el-table-column prop="hasPrice" label="存入金额"  />
            <el-table-column label="存钱进度" width="170" align="center">
                <template #default="scope">
                    <el-progress :percentage="pct(scope.row.progress_percent)" :stroke-width="12" />
                </template>
            </el-table-column>
            <el-table-column label="时间进度" width="170" align="center">
                <template #default="scope">
                    <el-progress v-if="scope.row.time_progress_percent != null" :percentage="pct(scope.row.time_progress_percent)" status="warning" :stroke-width="12" />
                    <span v-else>—</span>
                </template>
            </el-table-column>
            <el-table-column prop="type" label="币种"  />
            <el-table-column prop="start_time" label="起始日期" align="center" :show-overflow-tooltip="true" width="120" />
            <el-table-column prop="end_time" label="截止日期" align="center" :show-overflow-tooltip="true" width="120" />
            <el-table-column label="完成预测" min-width="300">
                <template #default="scope">
                    <div class="forecast-line">
                        <el-tag v-if="scope.row.forecast_on_track === true" type="success" size="small">有望按期</el-tag>
                        <el-tag v-else-if="scope.row.forecast_on_track === false" type="danger" size="small">存在风险</el-tag>
                        <el-tag v-else type="info" size="small">参考</el-tag>
                        <span v-if="scope.row.recent_net_daily != null" class="forecast-meta">日均结余 {{ num(scope.row.recent_net_daily) }} 元</span>
                        <span v-if="scope.row.required_daily_save != null" class="forecast-meta">需日均 {{ num(scope.row.required_daily_save) }} 元</span>
                    </div>
                    <div class="forecast-msg">{{ scope.row.forecast_message || '—' }}</div>
                </template>
            </el-table-column>
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
import { ref, reactive, onMounted } from 'vue'
import instances from "@/utils/request";
import { ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Edit } from '@element-plus/icons-vue'
import utils from '@/utils/utils'
import AddOrUpdate from '../system/plan/components/add-or-update.vue';
import { useUserStore } from "@/store/userStore";
const userStore = useUserStore();

const pct = (v) => Math.min(100, Math.round(Number(v) || 0))
const num = (v) => (Number(v) || 0).toFixed(2)
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
        searchForm.expenses_time = searchForm.timeRange[0]+' - '+ searchForm.timeRange[1]
    } else {
        searchForm.expenses_time = undefined
    }
    //调用分页接口API
     searchForm.user_id=userStore.user.id
    await instances.post("/plan/plans/",searchForm).then(res => {
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
        await instances.post("/plan/delete/",ids).then((res) => {
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
.plan-tip {
    margin: 16px 0 20px;
}
.forecast-line {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px;
    margin-bottom: 6px;
}
.forecast-meta {
    font-size: 12px;
    color: #909399;
}
.forecast-msg {
    font-size: 12px;
    color: #606266;
    line-height: 1.5;
}

</style>
