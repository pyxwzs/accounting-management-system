<template>
<Header></Header>
<div class="inner-page-header">
    <div class="container">
        <h1 class="mb-8">我的预算</h1>
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><router-link  to="/front"><i class="fas fa-home"></i> 首页</router-link></li>
            <li class="breadcrumb-item active" aria-current="page">我的预算</li>
        </ol>
    </div>
</div>
<div class="services-page pt-80 pb-55">
    <div class="container">
       <el-form :inline="true" :model="searchForm" ref="searchFormRef">
               <el-form-item prop="name" label="预算名称">
                    <el-input v-model="searchForm.name" placeholder="请输入记录名称" clearable>
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

        <div class="budget-hints" v-if="pageTableData.list.length">
            <el-alert v-for="row in pageTableData.list.filter((r) => r.alert_message)" :key="'w-' + row.id"
                :title="row.name + '：' + row.alert_message" type="warning" show-icon class="mb-2" />
            <el-alert v-for="row in pageTableData.list.filter((r) => r.next_period_suggestion)" :key="'n-' + row.id"
                :title="row.name + ' — ' + row.next_period_suggestion" type="info" show-icon class="mb-2" />
        </div>

        <el-row v-if="pageTableData.list.length" :gutter="16" class="budget-pie-row">
            <el-col v-for="row in pageTableData.list" :key="'pie-' + row.id" :xs="24" :sm="12" :md="8">
                <div class="chart-card">
                    <EchartItem class="chart-inner" :chartOption="buildBudgetPieOption(row)" />
                </div>
            </el-col>
        </el-row>

        <el-table :data="pageTableData.list" style="width: 100%" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" />
            <el-table-column prop="name" label="预算名称"  />
            <el-table-column label="统计周期" width="110" align="center">
                <template #default="scope">{{ cycleLabel(scope.row.cycle_type) }}</template>
            </el-table-column>
            <el-table-column prop="price" label="预算金额"  />
            <el-table-column label="预警线" width="90" align="center">
                <template #default="scope">{{ warnPercent(scope.row.warn_ratio) }}</template>
            </el-table-column>
        
            <el-table-column prop="residue_price" label="剩余金额"  />
             <el-table-column  label="已使用金额" >
                <template #default="scope">
                    <el-tag v-if="scope.row.use_price>scope.row.price"  type="danger">{{scope.row.use_price}}</el-tag>
                    <el-tag v-else type="success">{{scope.row.use_price}}</el-tag>
                </template>
            </el-table-column>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Edit } from '@element-plus/icons-vue'
import instances from "@/utils/request";
import utils from '@/utils/utils'
import AddOrUpdate from '../system/budget/components/add-or-update.vue';
import EchartItem from "@/common/components/echarts/echartItem.vue";
import { useUserStore } from "@/store/userStore";
const userStore = useUserStore();

const cycleLabel = (c) => {
    const m = { none: '不按周期', month: '按月', quarter: '按季', year: '按年' }
    return m[c] || c || '按月'
}
const warnPercent = (r) => {
    if (r == null || r === '') return '85%'
    return Math.round(Number(r) * 100) + '%'
}
const buildBudgetPieOption = (row) => {
    const price = Number(row.price) || 0
    const use = Number(row.use_price) || 0
    const used = Math.min(use, price)
    const rest = Math.max(0, price - use)
    const over = Math.max(0, use - price)
    const data = [
        { value: +used.toFixed(2), name: '已使用', itemStyle: { color: '#5470c6' } },
        { value: +rest.toFixed(2), name: '剩余额度', itemStyle: { color: '#91cc75' } }
    ]
    if (over > 0) {
        data.push({ value: +over.toFixed(2), name: '超支', itemStyle: { color: '#ee6666' } })
    }
    return {
        title: { text: row.name || '预算占用', left: 'center', textStyle: { fontSize: 14 } },
        tooltip: { trigger: 'item', formatter: '{b}: {c} 元 ({d}%)' },
        series: [{ type: 'pie', radius: '58%', data }]
    }
}
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
    await instances.post("/budget/budgets/",searchForm).then(res => {
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
        await instances.post("/budget/delete/",ids).then((res) => {
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
.budget-hints {
    margin: 16px 0;
}
.mb-2 {
    margin-bottom: 8px;
}
.budget-pie-row {
    margin-bottom: 24px;
}
.chart-card {
    background: #fafafa;
    border-radius: 8px;
    padding: 8px;
    margin-bottom: 16px;
    min-height: 280px;
}
.chart-inner {
    width: 100%;
    min-height: 260px;
}

</style>
