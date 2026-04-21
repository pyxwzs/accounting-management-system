<template>
    <el-card shadow="never">
        <template #header>
            <el-form :inline="true" :model="searchForm" ref="searchFormRef">
                <el-form-item prop="name" label="操作人">
                    <el-input v-model="searchForm.name" placeholder="请输入操作人" clearable>
                    </el-input>
                </el-form-item>
               
                <el-form-item label="操作类型" prop="operation">
                     <el-input v-model="searchForm.operation" placeholder="请输入操作类型/模糊查询" clearable>
                    </el-input>
                </el-form-item>
               
                <el-form-item label="状态" prop="operation_result">
                    <el-select v-model="searchForm.operation_result" placeholder="请选择状态"  clearable style="width: 120px;">
                        <el-option label="成功" value="0" />
                        <el-option label="失败" value="1" />
                    </el-select>
                </el-form-item>
                <el-form-item label="创建时间" prop="timeRange">
                    <el-date-picker v-model="searchForm.timeRange"  type="datetimerange" range-separator="--"
                        start-placeholder="开始时间" end-placeholder="结束时间" format="YYYY-MM-DD HH:mm:ss"
                        value-format="YYYY-MM-DD HH:mm:ss" />
                </el-form-item>
                <el-form-item>
                    <el-button type="success"  :icon="Search" plain @click="handleQuery" >搜索</el-button>
                    <el-button type="info"  :icon="Refresh" plain @click="handleRest" >重置</el-button>
                </el-form-item>
            </el-form>
            <div>
                <el-button v-permission="'log:delete'" type="danger" plain :icon="Delete" :disabled="!selectIds.length" 
                    @click="handleMultipleDelete()">批量删除</el-button>
            </div>
        </template>

       <el-table border :data="pageTableData.list" style="width: 100%" stripe :header-cell-style="{fontSize: '13px !important',color:'#111  !important'}"  size="large" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" />
            <el-table-column prop="operation" label="操作说明" align="center" />
            <el-table-column prop="name" label="操作人员" align="center" width="150" />
            <el-table-column prop="operation_result" label="操作状态" align="center"  width="150">
                <template #default="scope">
                    <el-tag v-if="scope.row.operation_result === '0'" effect="plain" type="success">成功</el-tag>
                    <el-tag v-else type="warning" effect="plain">失败</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="operation_fail_reason" label="失败原因" align="center"  />
            <el-table-column prop="create_time" label="操作日期" align="center" :show-overflow-tooltip="true" width="180" />
            <el-table-column fixed="right" align="center" label="操作" width="220">
                <template #default="scope">
                    <el-button    v-permission="'log:delete'"   :icon="Delete" type="danger" @click="handleDelete(scope.row.id)"  size="small">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <div class="pagination-container">
            <el-pagination v-model:current-page="searchForm.current" v-model:page-size="searchForm.size"
                :page-sizes="[10, 30, 50, 100]" :small=false :disabled=false :background=true
                layout="total, sizes, prev, pager, next, jumper" :total="pageTableData.total"
                @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>
    </el-card>
</template>

<script setup>
import { ref, reactive, onMounted} from 'vue'
import {  Delete, Edit, Plus, Refresh, Search, Sort } from '@element-plus/icons-vue';
import { ElMessageBox ,ElNotification } from "element-plus";
import instances from "@/utils/request";
import utils from '@/utils/utils'
//搜索表单组件
const searchFormRef=ref()
//搜索表单数据实体
const searchForm=reactive({
    size: 20,
    current: 1
})
/**
 * 列表选中数据id集合
 */
const selectIds = ref([])
//分页列表响应数据
const pageTableData = reactive({
    list: [],
    total: 0
})
//点击查询按钮事件
const handleQuery = () => {
    loadData()
}
/**
 * 调用后端api查询数据展示
 */
const loadData = async () => {
    if (searchForm.timeRange) {
        searchForm.endTime = searchForm.timeRange[1]
        searchForm.startTime = searchForm.timeRange[0]
    } else {
        searchForm.endTime = undefined
        searchForm.startTime = undefined
    }
     
    //调用分页接口API
    await instances.post("/log/logs/",searchForm).then(res => {
        pageTableData.list = res.data.list
        pageTableData.total = res.data.total
    }).catch(err => { 
         console.error(err)
    })
}
//重置分页查询form表单
const handleRest = () => {
    searchFormRef.value.resetFields()
    loadData()
}
/**
 * 
 * @param _vari 分页左下角切换当前页条数事件
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
 * 列表选中监听事件
 * @param row 
 */
const handleSelectionChange = (row) => {
    selectIds.value = row.map(item => {
        return item.id
    })
}
/**
 * 批量删除
 */
const handleMultipleDelete = () => {
    openDeleteDialog(selectIds.value)
}
/**
 * 
 * @param id 列表工具栏删除
 */
const handleDelete = (id) => {
    openDeleteDialog([id])
}
/**
 * 删除操作日志
 * @param ids 操作日志id
 */
const openDeleteDialog = (ids) => {
    ElMessageBox.confirm(`确认删除已选中的数据项?`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        //调用删除接口API
        await instances.post("/log/delete/",ids).then((res) => {
        utils.showSucess("删除成功!")   
        loadData()
        }).catch(_err => { })
    });
}

/**
 * 页面挂载后执行
 */
onMounted(() => {
    loadData()
})

</script>

<style scoped>

.pagination-container {
    padding: 10px 0px 0px;
}
</style>
