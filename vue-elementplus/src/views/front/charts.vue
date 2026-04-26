<template>
<Header></Header>
<div class="inner-page-header">
    <div class="container">
        <h1 class="mb-8">收支记录</h1>
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><router-link to="/front"><i class="fas fa-home"></i> 首页</router-link></li>
            <li class="breadcrumb-item active" aria-current="page">收支记录</li>
        </ol>
    </div>
</div>
<div class="services-page pt-80 pb-55">
    <div class="container">
        <el-form :inline="true" :model="searchForm" ref="searchFormRef">
            <el-form-item label="日期范围" prop="timeRange">
                <el-date-picker v-model="searchForm.timeRange" type="daterange" range-separator="-"
                    start-placeholder="开始时间" end-placeholder="结束时间" format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD" />
            </el-form-item>
            <el-form-item>
                <el-button type="success" :icon="Search" @click="handleQuery" plain>搜索</el-button>
                <el-button type="info" :icon="Refresh" @click="handleRest" plain>重置</el-button>
            </el-form-item>
            <el-form-item>
                <span class="net-asset">净资产&nbsp;&nbsp;&nbsp;{{ mainResult.price }}</span>
            </el-form-item>
        </el-form>

        <!-- 四宫格：饼图、合并折线、合并柱状、词云（均匀分布） -->
        <el-row :gutter="16" class="charts-grid">
            <el-col :xs="24" :md="12" class="grid-cell">
                <div class="chart-panel">
                    <EchartItem class="chart-item" :chartOption="pieChartOption01" />
                </div>
            </el-col>
            <el-col :xs="24" :md="12" class="grid-cell">
                <div class="chart-panel">
                    <EchartItem class="chart-item" :chartOption="combinedLineOption" />
                </div>
            </el-col>
            <el-col :xs="24" :md="12" class="grid-cell">
                <div class="chart-panel">
                    <EchartItem class="chart-item" :chartOption="combinedBarOption" />
                </div>
            </el-col>
            <el-col :xs="24" :md="12" class="grid-cell">
                <div class="chart-panel chart-panel-wordcloud">
                    <div class="wordcloud-toolbar">
                        <span class="toolbar-label">词云</span>
                        <el-radio-group v-model="wordCloudMode" size="small">
                            <el-radio-button label="income">收入词云</el-radio-button>
                            <el-radio-button label="expense">支出词云</el-radio-button>
                        </el-radio-group>
                    </div>
                    <EchartItem class="chart-item chart-item-wordcloud" :chartOption="wordCloudOption" />
                </div>
            </el-col>
        </el-row>
    </div>
</div>
<Footer></Footer>
</template>

<script setup>
import Header from '@/views/front/components/header.vue'
import Footer from '@/views/front/components/footer.vue'
import { ref, reactive, onMounted, computed } from 'vue'
import instances from "@/utils/request"
import { useUserStore } from "@/store/userStore"
import { Search, Refresh } from '@element-plus/icons-vue'
import EchartItem from "@/common/components/echarts/echartItem.vue"

const userStore = useUserStore()
const searchFormRef = ref()
const searchForm = reactive({})
const wordCloudMode = ref('income')

const mainResult = reactive({
    mapList01: [],
    mapList02: { incomeTimeList: [], incomePriceList: [] },
    mapList03: { expenditureTimeList: [], expenditurePriceList: [] },
    mapList04: { incomeTypeTimeList: [], incomeTypePriceList: [] },
    mapList05: { expenditureTypeTimeList: [], expenditureTypePriceList: [] },
    price: 0
})

/** 合并收入/支出按日数据，用于双折线 */
function mergeTimeSeries(incomeTimes, incomePrices, expenseTimes, expensePrices) {
    const map = new Map()
    const put = (d, key, val) => {
        if (!d) return
        if (!map.has(d)) map.set(d, { income: 0, expense: 0 })
        map.get(d)[key] = (parseFloat(val) || 0)
    }
    for (let i = 0; i < (incomeTimes?.length || 0); i++) {
        put(incomeTimes[i], 'income', incomePrices[i])
    }
    for (let i = 0; i < (expenseTimes?.length || 0); i++) {
        put(expenseTimes[i], 'expense', expensePrices[i])
    }
    const dates = [...map.keys()].sort()
    const inc = dates.map(d => map.get(d).income)
    const exp = dates.map(d => map.get(d).expense)
    return { dates, inc, exp }
}

/**
 * 当年 1～12 月：从按日数据汇总每月收入、支出；差额 = 收入 − 支出（仅用于标注，不画柱）
 */
function buildCurrentYearMonthlySeries(incomeTimes, incomePrices, expenseTimes, expensePrices) {
    const year = new Date().getFullYear()
    const xLabels = Array.from({ length: 12 }, (_, i) => `${i + 1}月`)
    const incomeM = new Array(12).fill(0)
    const expenseM = new Array(12).fill(0)

    const add = (timeList, priceList, arr) => {
        for (let i = 0; i < (timeList?.length || 0); i++) {
            const d = timeList[i]
            if (!d || String(d).length < 7) continue
            const y = parseInt(String(d).substring(0, 4), 10)
            const m = parseInt(String(d).substring(5, 7), 10) - 1
            if (y !== year || m < 0 || m > 11) continue
            const v = parseFloat(priceList[i]) || 0
            arr[m] += v
        }
    }
    add(incomeTimes, incomePrices, incomeM)
    add(expenseTimes, expensePrices, expenseM)

    for (let i = 0; i < 12; i++) {
        incomeM[i] = +incomeM[i].toFixed(2)
        expenseM[i] = +expenseM[i].toFixed(2)
    }
    const diffM = xLabels.map((_, i) => Number((incomeM[i] - expenseM[i]).toFixed(2)))
    return { year, xLabels, incomeM, expenseM, diffM }
}

const handleQuery = () => { loadData() }
const handleRest = () => {
    searchFormRef.value?.resetFields()
    loadData()
}

const loadData = async () => {
    if (searchForm.timeRange) {
        searchForm.day = searchForm.timeRange[0] + ' - ' + searchForm.timeRange[1]
    } else {
        searchForm.day = undefined
    }
    searchForm.user_id = userStore.user.id
    await instances.post("/info/load/", searchForm).then(res => {
        Object.assign(mainResult, res.data)
        if (!mainResult.mapList02) mainResult.mapList02 = { incomeTimeList: [], incomePriceList: [] }
        if (!mainResult.mapList03) mainResult.mapList03 = { expenditureTimeList: [], expenditurePriceList: [] }
        if (!mainResult.mapList04) mainResult.mapList04 = { incomeTypeTimeList: [], incomeTypePriceList: [] }
        if (!mainResult.mapList05) mainResult.mapList05 = { expenditureTypeTimeList: [], expenditureTypePriceList: [] }
    }).catch(err => {
        console.error(err)
    })
}

const pieChartOption01 = computed(() => ({
    title: {
        text: '收入支出占比',
        left: 'center'
    },
    legend: {
        x: 'left',
        textStyle: { color: 'var(--el-text-color-primary)' },
        orient: 'vertical'
    },
    tooltip: { trigger: 'item' },
    toolbox: {
        show: true,
        feature: {
            mark: { show: true },
            dataView: { show: true, readOnly: false },
            saveAsImage: { show: true }
        }
    },
    series: [{
        name: '金额',
        type: 'pie',
        radius: '50%',
        data: mainResult.mapList01 || [],
        emphasis: {
            itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
        }
    }]
}))

const combinedLineOption = computed(() => {
    const m2 = mainResult.mapList02 || {}
    const m3 = mainResult.mapList03 || {}
    const { dates, inc, exp } = mergeTimeSeries(
        m2.incomeTimeList,
        m2.incomePriceList,
        m3.expenditureTimeList,
        m3.expenditurePriceList
    )
    return {
        title: { text: '收入与支出时间走势', left: 'center' },
        tooltip: { trigger: 'axis' },
        legend: { data: ['收入', '支出'], bottom: 4 },
        grid: { left: '3%', right: '4%', bottom: '12%', top: '14%', containLabel: true },
        toolbox: {
            show: true,
            feature: { saveAsImage: { show: true } }
        },
        xAxis: { type: 'category', boundaryGap: false, data: dates },
        yAxis: { type: 'value' },
        series: [
            { name: '收入', type: 'line', smooth: true, data: inc, itemStyle: { color: '#91cc75' } },
            { name: '支出', type: 'line', smooth: true, data: exp, itemStyle: { color: '#ee6666' } }
        ]
    }
})

const combinedBarOption = computed(() => {
    const m2 = mainResult.mapList02 || {}
    const m3 = mainResult.mapList03 || {}
    const { year, xLabels, incomeM, expenseM, diffM } = buildCurrentYearMonthlySeries(
        m2.incomeTimeList,
        m2.incomePriceList,
        m3.expenditureTimeList,
        m3.expenditurePriceList
    )

    const labelY = xLabels.map((_, i) => {
        const top = Math.max(incomeM[i], expenseM[i], 0)
        return top > 0 ? +(top * 1.08).toFixed(2) : 0
    })

    const diffLabelText = (d) => {
        if (d > 0) return `差额 +${d}`
        if (d < 0) return `差额 ${d}`
        return '差额 0'
    }

    return {
        title: { text: `${year}年月收入与月支出（1–12月）`, left: 'center' },
        tooltip: {
            trigger: 'axis',
            axisPointer: { type: 'shadow' },
            formatter: (params) => {
                if (!params?.length) return ''
                const idx = params[0].dataIndex
                const monthName = params[0].axisValue
                const bars = params.filter((p) => p.seriesType === 'bar')
                let html = `${year}年${monthName}<br/>`
                for (const p of bars) {
                    html += `${p.marker}${p.seriesName}：${p.value} 元<br/>`
                }
                const d = diffM[idx]
                html += `收支差额：${d > 0 ? '+' : ''}${d} 元`
                if (d < 0) html += '（支出大于收入）'
                return html
            }
        },
        legend: { data: ['月收入', '月支出'], bottom: 4 },
        grid: { left: '3%', right: '4%', bottom: '14%', top: '18%', containLabel: true },
        toolbox: {
            show: true,
            feature: { saveAsImage: { show: true } }
        },
        xAxis: {
            type: 'category',
            data: xLabels,
            axisTick: { alignWithLabel: true }
        },
        yAxis: {
            type: 'value',
            name: '金额（元）',
            axisLine: { show: true },
            splitLine: { lineStyle: { type: 'dashed' } }
        },
        series: [
            {
                name: '月收入',
                type: 'bar',
                data: incomeM,
                itemStyle: { color: '#5470c6' },
                barGap: '20%'
            },
            {
                name: '月支出',
                type: 'bar',
                data: expenseM,
                itemStyle: { color: '#fac858' }
            },
            {
                name: '差额标注',
                type: 'scatter',
                symbolSize: 0.001,
                itemStyle: { opacity: 0 },
                tooltip: { show: false },
                silent: true,
                z: 20,
                label: { show: true, position: 'top', distance: 6, fontSize: 11 },
                data: xLabels.map((lab, i) => {
                    const d = diffM[i] ?? 0
                    return {
                        value: [lab, labelY[i]],
                        label: {
                            formatter: diffLabelText(d),
                            color: d >= 0 ? '#2f855a' : '#c53030'
                        }
                    }
                })
            }
        ]
    }
})

const wordCloudOption = computed(() => {
    const isIncome = wordCloudMode.value === 'income'
    const m4 = mainResult.mapList04 || {}
    const m5 = mainResult.mapList05 || {}
    const names = isIncome ? (m4.incomeTypeTimeList || []) : (m5.expenditureTypeTimeList || [])
    const vals = isIncome ? (m4.incomeTypePriceList || []) : (m5.expenditureTypePriceList || [])
    const data = names
        .map((name, i) => ({
            name: (name && String(name).trim()) ? name : '未分类',
            value: parseFloat(vals[i]) || 0
        }))
        .filter(d => d.value > 0)

    const titleText = isIncome ? '收入词云（按分类名称）' : '支出词云（按分类名称）'

    if (!data.length) {
        return {
            title: { text: titleText, left: 'center', top: 8, textStyle: { fontSize: 14 } },
            graphic: {
                type: 'text',
                left: 'center',
                top: 'middle',
                style: { text: '暂无数据', fill: '#909399', fontSize: 16 }
            }
        }
    }

    return {
        title: { text: titleText, left: 'center', top: 8, textStyle: { fontSize: 14 } },
        tooltip: { show: true, formatter: (p) => (p.data ? `${p.data.name}: ${p.data.value} 元` : '') },
        series: [{
            type: 'wordCloud',
            shape: 'circle',
            left: 'center',
            top: 'center',
            width: '95%',
            height: '90%',
            sizeRange: [14, 52],
            rotationRange: [-45, 45],
            gridSize: 10,
            textStyle: {
                fontWeight: 500,
                color: isIncome ? '#2f855a' : '#c53030'
            },
            data
        }]
    }
})

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
.net-asset {
    font-weight: 600;
    color: var(--el-text-color-primary);
}
.services-page {
    min-height: 58%;
}
.charts-grid {
    margin-top: 20px;
}
.grid-cell {
    margin-bottom: 16px;
}
.chart-panel {
    height: 420px;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 8px;
    padding: 8px;
    background: var(--el-bg-color);
    box-sizing: border-box;
}
.chart-panel-wordcloud {
    display: flex;
    flex-direction: column;
    padding-top: 4px;
}
.wordcloud-toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 8px;
    padding: 4px 8px 0;
    flex-shrink: 0;
}
.toolbar-label {
    font-weight: 600;
    font-size: 14px;
    color: var(--el-text-color-primary);
}
.chart-item {
    width: 100%;
    height: 100%;
    min-height: 360px;
}
.chart-item-wordcloud {
    flex: 1;
    min-height: 300px;
}
</style>
