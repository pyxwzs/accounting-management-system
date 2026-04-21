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
                    <plain>净资产&nbsp;&nbsp;&nbsp;{{mainResult.price}}</plain>
                 </el-form-item>
            </el-form>
         <el-row style="margin-top:20px;"> 
            <el-col :span="24" >
                <EchartItem class="chart-item" :chartOption="pieChartOption01"></EchartItem>
            </el-col>
            <el-col :span="12">
                <EchartItem class="chart-item" :chartOption="pieChartOption02"></EchartItem>
            </el-col>
            <el-col :span="12">
                <EchartItem class="chart-item" :chartOption="pieChartOption03"></EchartItem>
            </el-col> 
            <el-col :span="12">
                <EchartItem class="chart-item" :chartOption="pieChartOption04"></EchartItem>
            </el-col> 
            <el-col :span="12">
                <EchartItem class="chart-item" :chartOption="pieChartOption05"></EchartItem>
            </el-col> 
      </el-row>
    </div>
</div>
<Footer></Footer>
</template>

<script setup>
import Header from '@/views/front/components/header.vue'
import Footer from '@/views/front/components/footer.vue'
import { ref, reactive, onMounted,watch,computed} from 'vue'
import instances from "@/utils/request";
import utils from '@/utils/utils'
import { useRoute ,useRouter} from 'vue-router'
import ToolUtils from '@/utils/ToolUtils';
import * as siteConfig  from "@/config/index";
import { useUserStore } from "@/store/userStore";
import * as echarts from 'echarts'
const $echarts = echarts;
import EchartItem from "@/common/components/echarts/echartItem.vue";
const userStore = useUserStore();
const searchFormRef=ref()
/**
 * 搜索表单数据实体
 */
const searchForm=reactive({})
const mainResult=reactive({
	 mapList01: [],
	 mapList02: [],
	 mapList03: [],
	 mapList04: [],
	 mapList05: [],
})
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
const loadData = async () => {
    if (searchForm.timeRange) {
        searchForm.day = searchForm.timeRange[0]+' - '+ searchForm.timeRange[1]
    } else {
        searchForm.day = undefined
    }
    //调用分页接口API
    searchForm.user_id=userStore.user.id
        await instances.post("/info/load/",searchForm).then(res => {
         Object.assign(mainResult, res.data);

    }).catch(err => { 
         console.error(err)
    })
}
const pieChartOption01 = computed(() =>({
        title: {
            text: '收入支出统计',
           
            left: 'center'
        },
        legend: {
			x: 'left',
			textStyle:{
				color:'var(--el-text-color-primary)'
			},
			orient: 'vertical'
		},
		tooltip: {
            trigger: 'item'
        },
		toolbox: {
			show: true,
			feature: {
				mark: {
					show: true
				},
				dataView: {
					show: true,
					readOnly: false
				},
				
				saveAsImage: {
					show: true
				}
			}
		},
         series: [
            {
            name: '金额',
            type: 'pie',
            radius: '50%',
           data: mainResult.mapList01,
            emphasis: {
                itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
            }
        ]
		
}));
const pieChartOption02 = computed(() =>({
        title: {
            text: '收入时间走势图',
           
            left: 'center'
        },
        legend: {
			x: 'left',
			textStyle:{
				color:'var(--el-text-color-primary)'
			},
			orient: 'vertical'
		},
		tooltip: {
            trigger: 'item'
        },
		toolbox: {
			show: true,
			feature: {
				mark: {
					show: true
				},
				dataView: {
					show: true,
					readOnly: false
				},
				
				saveAsImage: {
					show: true
				}
			}
		},
         xAxis: {
            type: 'category',
            data: mainResult.mapList02.incomeTimeList
        },
        yAxis: {
            type: 'value'
        },
          series: [
            {
                data: mainResult.mapList02.incomePriceList,
                type: 'line',
                smooth: true
            }
        ]
		
}));
const pieChartOption03 = computed(() =>({
         title: {
            text: '支出时间走势图',
           
            left: 'center'
        },
        legend: {
			x: 'left',
			textStyle:{
				color:'var(--el-text-color-primary)'
			},
			orient: 'vertical'
		},
		tooltip: {
            trigger: 'item'
        },
		toolbox: {
			show: true,
			feature: {
				mark: {
					show: true
				},
				dataView: {
					show: true,
					readOnly: false
				},
				
				saveAsImage: {
					show: true
				}
			}
		},
         xAxis: {
            type: 'category',
            data: mainResult.mapList03.expenditureTimeList
        },
        yAxis: {
            type: 'value'
        },
          series: [
            {
                data: mainResult.mapList03.expenditurePriceList,
                type: 'line',
                smooth: true
            }
        ]
		
		
}));
const pieChartOption04 = computed(() =>({
         title: {
            text: '支出分类统计图',
           
            left: 'center'
        },
        legend: {
			x: 'left',
			textStyle:{
				color:'var(--el-text-color-primary)'
			},
			orient: 'vertical'
		},
		tooltip: {
            trigger: 'item'
        },
		toolbox: {
			show: true,
			feature: {
				mark: {
					show: true
				},
				dataView: {
					show: true,
					readOnly: false
				},
				
				saveAsImage: {
					show: true
				}
			}
		},
         xAxis: {
            type: 'category',
            data: mainResult.mapList04.incomeTypeTimeList
        },
        yAxis: {
            type: 'value'
        },
          series: [
            {
                data: mainResult.mapList04.incomeTypePriceList,
                type: 'bar',
                smooth: true
            }
        ]
		
		
}));
const pieChartOption05 = computed(() =>({
         title: {
            text: '支出分类统计图',
           
            left: 'center'
        },
        legend: {
			x: 'left',
			textStyle:{
				color:'var(--el-text-color-primary)'
			},
			orient: 'vertical'
		},
		tooltip: {
            trigger: 'item'
        },
		toolbox: {
			show: true,
			feature: {
				mark: {
					show: true
				},
				dataView: {
					show: true,
					readOnly: false
				},
				
				saveAsImage: {
					show: true
				}
			}
		},
         xAxis: {
            type: 'category',
            data: mainResult.mapList05.expenditureTypeTimeList
        },
        yAxis: {
            type: 'value'
        },
          series: [
            {
                data: mainResult.mapList05.expenditureTypePriceList,
                type: 'bar',
                smooth: true
            }
        ]
		
		
}));
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
.chart-item{

height: 450px;
}
plain{
    font-weight: bolder;
}
</style>
