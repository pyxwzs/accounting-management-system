<template>
<Header></Header>
<div class="inner-page-header">
    <div class="container">
        <h1 class="mb-8">我的预算</h1>
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><router-link to="/front"><i class="fas fa-home"></i> 首页</router-link></li>
            <li class="breadcrumb-item active" aria-current="page">我的预算</li>
        </ol>
    </div>
</div>
<div class="services-page pt-80 pb-55">
    <div class="container">
        <el-form :inline="true" :model="searchForm" ref="searchFormRef">
            <el-form-item prop="name" label="预算名称">
                <el-input v-model="searchForm.name" placeholder="请输入记录名称" clearable />
            </el-form-item>
            <el-form-item>
                <el-button type="success" :icon="Search" @click="handleQuery" plain>搜索</el-button>
                <el-button type="info" :icon="Refresh" @click="handleRest" plain>重置</el-button>
            </el-form-item>
        </el-form>
        <div class="toolbar-row">
            <el-button type="primary" :icon="Plus" plain @click="openDialog()">新增</el-button>
            <el-button type="danger" :icon="Delete" :disabled="!selectIds.length" plain
                @click="handleMultipleDelete()">批量删除</el-button>
        </div>

        <div class="budget-hints" v-if="pageTableData.list.length">
            <el-alert v-for="row in pageTableData.list.filter((r) => r.alert_message)" :key="'w-' + row.id"
                :title="row.name + '：' + row.alert_message" type="warning" show-icon class="mb-2" />
            <el-alert v-for="row in pageTableData.list.filter((r) => r.next_period_suggestion)" :key="'n-' + row.id"
                :title="row.name + ' — ' + row.next_period_suggestion" type="info" show-icon class="mb-2" />
        </div>

        <el-empty v-if="!pageTableData.list.length" description="暂无预算，点击新增创建" />

        <el-row v-else :gutter="16" class="budget-card-grid">
            <el-col v-for="row in pageTableData.list" :key="row.id" :xs="24" :sm="12" :lg="8">
                <el-card class="budget-card" shadow="hover">
                    <div class="budget-card-top">
                        <el-checkbox
                            :model-value="selectIds.includes(row.id)"
                            @change="(checked) => toggleRowSelect(row.id, checked)"
                        />
                        <span class="budget-name">{{ row.name || '未命名预算' }}</span>
                        <el-tag size="small" type="info">{{ cycleLabel(row.cycle_type) }}</el-tag>
                    </div>
                    <div class="budget-card-chart-wrap">
                        <EchartItem class="budget-mini-pie" :chartOption="buildBudgetPieOption(row)" />
                    </div>
                    <ul class="budget-stats">
                        <li><span>预算金额</span><strong>{{ row.price }}</strong></li>
                        <li><span>预警线</span><strong>{{ warnPercent(row.warn_ratio) }}</strong></li>
                        <li><span>剩余</span><strong>{{ row.residue_price }}</strong></li>
                        <li>
                            <span>已用</span>
                            <el-tag v-if="Number(row.use_price) > Number(row.price)" type="danger" size="small">{{ row.use_price }}</el-tag>
                            <el-tag v-else type="success" size="small">{{ row.use_price }}</el-tag>
                        </li>
                        <li class="full"><span>创建时间</span><em>{{ row.create_time }}</em></li>
                    </ul>
                    <div class="budget-card-actions">
                        <el-button :icon="Edit" type="warning" @click="openDialog(row.id)" plain size="small">编辑</el-button>
                        <el-button :icon="Delete" type="danger" @click="handleDelete(row.id)" plain size="small">删除</el-button>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <div class="pagination-container" v-if="pageTableData.total">
            <el-pagination v-model:current-page="searchForm.current" v-model:page-size="searchForm.size"
                :page-sizes="[10, 30, 50, 100]" :background="true"
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
import instances from "@/utils/request"
import utils from '@/utils/utils'
import AddOrUpdate from '../system/budget/components/add-or-update.vue'
import EchartItem from "@/common/components/echarts/echartItem.vue"
import { useUserStore } from "@/store/userStore"

const userStore = useUserStore()

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
        title: { text: '占用分布', left: 'center', top: 4, textStyle: { fontSize: 13 } },
        tooltip: { trigger: 'item', formatter: '{b}: {c} 元 ({d}%)' },
        series: [{ type: 'pie', radius: ['38%', '62%'], center: ['50%', '52%'], data }]
    }
}

const searchFormRef = ref()
const searchForm = reactive({
    size: 20,
    current: 1
})
const pageTableData = reactive({
    list: [],
    total: 0
})
const addOrUpdatemodal = ref(null)
const selectIds = ref([])

const toggleRowSelect = (id, checked) => {
    if (checked) {
        if (!selectIds.value.includes(id)) selectIds.value = [...selectIds.value, id]
    } else {
        selectIds.value = selectIds.value.filter(x => x !== id)
    }
}

const handleQuery = () => { loadData() }
const handleRest = () => {
    searchFormRef.value.resetFields()
    loadData()
}
const handleSizeChange = () => {
    searchForm.current = 1
    loadData()
}
const handleCurrentChange = (vari) => {
    searchForm.current = vari
    loadData()
}

const loadData = async () => {
    if (searchForm.timeRange) {
        searchForm.day = searchForm.timeRange[0] + ' - ' + searchForm.timeRange[1]
    } else {
        searchForm.day = undefined
    }
    searchForm.user_id = userStore.user.id
    await instances.post("/budget/budgets/", searchForm).then(res => {
        pageTableData.list = res.data.list
        pageTableData.total = res.data.total
        selectIds.value = selectIds.value.filter(id => pageTableData.list.some(r => r.id === id))
    }).catch(err => {
        console.error(err)
    })
}

const openDialog = (id) => {
    addOrUpdatemodal.value.openDialog(id)
}
const addOrUpdateSuccess = () => {
    loadData()
}
const handleDelete = (id) => {
    openDeleteDialog([id])
}
const handleMultipleDelete = () => {
    openDeleteDialog(selectIds.value)
}
const openDeleteDialog = (ids) => {
    ElMessageBox.confirm(`确认删除已选中的数据项?`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        await instances.post("/budget/delete/", ids).then(() => {
            utils.showSucess("删除成功!")
            loadData()
        }).catch(_err => {
            console.error(_err)
        })
    })
}

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
.services-page {
    min-height: 58%;
}
.toolbar-row {
    margin-bottom: 12px;
}
.budget-hints {
    margin: 16px 0;
}
.mb-2 {
    margin-bottom: 8px;
}
.budget-card-grid {
    margin-top: 8px;
}
.budget-card {
    margin-bottom: 20px;
    border-radius: 10px;
}
.budget-card-top {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 8px;
    flex-wrap: wrap;
}
.budget-name {
    font-weight: 600;
    font-size: 16px;
    flex: 1;
    min-width: 0;
}
.budget-card-chart-wrap {
    height: 220px;
    margin: 0 -4px 8px;
}
.budget-mini-pie {
    width: 100%;
    height: 210px;
}
.budget-stats {
    list-style: none;
    padding: 0;
    margin: 0 0 12px;
    font-size: 13px;
    color: #606266;
}
.budget-stats li {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 4px 0;
    border-bottom: 1px dashed var(--el-border-color-lighter);
}
.budget-stats li.full {
    flex-wrap: wrap;
}
.budget-stats em {
    font-style: normal;
    font-size: 12px;
    color: #909399;
}
.budget-card-actions {
    display: flex;
    gap: 8px;
    justify-content: flex-end;
}
.pagination-container {
    padding: 16px 0 0;
}
:deep(.el-pager li.is-active) {
    color: #dead6f;
}
:deep(.el-pager li:hover) {
    color: #dead6f;
}
</style>
