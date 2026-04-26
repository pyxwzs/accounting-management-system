<template>
<Header></Header>
<div class="inner-page-header">
    <div class="container">
        <h1 class="mb-8">存钱计划</h1>
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><router-link to="/front"><i class="fas fa-home"></i> 首页</router-link></li>
            <li class="breadcrumb-item active" aria-current="page">存钱计划</li>
        </ol>
    </div>
</div>
<div class="services-page pt-80 pb-55">
    <div class="container">
        <el-form :inline="true" :model="searchForm" ref="searchFormRef">
            <el-form-item prop="name" label="计划名称">
                <el-input v-model="searchForm.name" placeholder="请输入计划名称" clearable />
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

        <el-alert type="info" show-icon class="plan-tip" title="进度与预测说明"
            description="存钱进度 = 已存入 / 目标；时间进度 = 已过天数 / 计划总天数。完成预测依据您近30天「收支记录」中的收入与支出，用日均结余与剩余目标、剩余天数估算是否有望按期存满。" />

        <el-empty v-if="!pageTableData.list.length" description="暂无存钱计划" />

        <el-row v-else :gutter="16" class="plan-card-grid">
            <el-col v-for="row in pageTableData.list" :key="row.id" :xs="24" :sm="12" :lg="8">
                <el-card class="plan-card" shadow="hover">
                    <div class="plan-card-head">
                        <el-checkbox
                            :model-value="selectIds.includes(row.id)"
                            @change="(checked) => toggleRowSelect(row.id, checked)"
                        />
                        <span class="plan-title">{{ row.name || '未命名计划' }}</span>
                        <el-tag size="small">{{ row.type || 'CNY' }}</el-tag>
                    </div>
                    <div class="plan-amounts">
                        <div><span>目标</span><strong>{{ row.price }}</strong></div>
                        <div><span>已存</span><strong>{{ row.hasPrice }}</strong></div>
                    </div>
                    <div class="plan-progress-block">
                        <div class="plan-progress-label">存钱进度</div>
                        <el-progress :percentage="pct(row.progress_percent)" :stroke-width="14" />
                    </div>
                    <div class="plan-progress-block">
                        <div class="plan-progress-label">时间进度</div>
                        <el-progress v-if="row.time_progress_percent != null" :percentage="pct(row.time_progress_percent)"
                            status="warning" :stroke-width="14" />
                        <span v-else class="muted">—</span>
                    </div>
                    <div class="plan-dates">
                        <span>{{ row.start_time || '—' }}</span>
                        <span class="sep">→</span>
                        <span>{{ row.end_time || '—' }}</span>
                    </div>
                    <div class="plan-forecast">
                        <div class="forecast-line">
                            <el-tag v-if="row.forecast_on_track === true" type="success" size="small">有望按期</el-tag>
                            <el-tag v-else-if="row.forecast_on_track === false" type="danger" size="small">存在风险</el-tag>
                            <el-tag v-else type="info" size="small">参考</el-tag>
                            <span v-if="row.recent_net_daily != null" class="forecast-meta">日均结余 {{ num(row.recent_net_daily) }}</span>
                            <span v-if="row.required_daily_save != null" class="forecast-meta">需日均 {{ num(row.required_daily_save) }}</span>
                        </div>
                        <p class="forecast-msg">{{ row.forecast_message || '—' }}</p>
                    </div>
                    <div class="plan-card-actions">
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
import instances from "@/utils/request"
import { ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Edit } from '@element-plus/icons-vue'
import utils from '@/utils/utils'
import AddOrUpdate from '../system/plan/components/add-or-update.vue'
import { useUserStore } from "@/store/userStore"

const userStore = useUserStore()
const pct = (v) => Math.min(100, Math.round(Number(v) || 0))
const num = (v) => (Number(v) || 0).toFixed(2)

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
        searchForm.expenses_time = searchForm.timeRange[0] + ' - ' + searchForm.timeRange[1]
    } else {
        searchForm.expenses_time = undefined
    }
    searchForm.user_id = userStore.user.id
    await instances.post("/plan/plans/", searchForm).then(res => {
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
        await instances.post("/plan/delete/", ids).then(() => {
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
.plan-tip {
    margin: 0 0 20px;
}
.plan-card-grid {
    margin-top: 4px;
}
.plan-card {
    margin-bottom: 20px;
    border-radius: 10px;
    min-height: 320px;
}
.plan-card-head {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 12px;
    flex-wrap: wrap;
}
.plan-title {
    font-weight: 600;
    font-size: 16px;
    flex: 1;
    min-width: 0;
}
.plan-amounts {
    display: flex;
    gap: 24px;
    margin-bottom: 12px;
    font-size: 13px;
    color: #606266;
}
.plan-amounts strong {
    display: block;
    font-size: 18px;
    color: #303133;
    margin-top: 4px;
}
.plan-progress-block {
    margin-bottom: 12px;
}
.plan-progress-label {
    font-size: 12px;
    color: #909399;
    margin-bottom: 4px;
}
.plan-dates {
    font-size: 12px;
    color: #606266;
    margin-bottom: 12px;
    display: flex;
    align-items: center;
    gap: 8px;
    flex-wrap: wrap;
}
.plan-dates .sep {
    color: #c0c4cc;
}
.muted {
    color: #c0c4cc;
    font-size: 13px;
}
.plan-forecast {
    padding-top: 8px;
    border-top: 1px dashed var(--el-border-color-lighter);
    margin-bottom: 12px;
}
.forecast-line {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
}
.forecast-meta {
    font-size: 12px;
    color: #909399;
}
.forecast-msg {
    font-size: 12px;
    color: #606266;
    line-height: 1.55;
    margin: 0;
}
.plan-card-actions {
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
