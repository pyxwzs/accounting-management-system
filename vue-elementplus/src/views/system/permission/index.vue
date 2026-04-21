<template>
    <el-card shadow="never">
        <template #header>
            <el-form :inline="true" :model="searchForm" ref="searchFormRef">
                <el-form-item prop="name" label="权限名称">
                    <el-input v-model="searchForm.name" placeholder="请输入权限名称/模糊查询" clearable>
                    </el-input>
                </el-form-item>
                <el-form-item  prop="mark" label="权限标识">
                     <el-input v-model="searchForm.mark" placeholder="请输入权限标识/模糊查询"  clearable>
                    </el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="success" :icon="Search" plain  @click="handleQuery" >搜索</el-button>
                    <el-button type="info" :icon="Refresh" plain @click="handleRest" >重置</el-button>
                </el-form-item>
            </el-form>
            <div>
                <el-button v-permission="'permission:insert'" plain  type="primary" :icon="Plus"  @click="openDialog()">新增</el-button>
            </div>
        </template>
     <el-table :data="dataList" border style="width: 100%"  stripe :header-cell-style="{fontSize: '13px !important', color:'#111  !important'}"  size="large" >
            <el-table-column prop="name" label="权限名称" align="center" />
            <el-table-column prop="mark" label="权限标识" align="center" />
            <el-table-column prop="type" label="类型" align="center"  >
                <template #default="scope">
                    <el-tag v-if="scope.row.type === '增'" type="success"  effect="dark">增</el-tag>
                    <el-tag v-else-if="scope.row.type === '删'" type="danger"  effect="dark">删</el-tag>
                    <el-tag v-else-if="scope.row.type === '改'" type="warning"  effect="dark">改</el-tag>
                    <el-tag v-else type="info"  effect="plain">查</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="create_time" label="创建日期" align="center" :show-overflow-tooltip="true" width="180" />
            <el-table-column fixed="right" align="center" label="操作" width="220">
                <template #default="scope">
                    <el-button   v-permission="'permission:edit'"  :icon="Edit" type="warning" @click="openDialog(scope.row.id)" plain size="small">编辑</el-button>
                    <el-button   v-permission="'permission:delete'"   :icon="Delete" type="danger" @click="handleDelete(scope.row.id)"  plain size="small">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    
    </el-card>
    <AddOrUpdate ref="addOrUpdatemodal" @success="addOrUpdateSuccess"></AddOrUpdate>
</template>

<script setup>
import { ref, reactive, onMounted} from 'vue'
import {  Delete, Edit, Plus, Refresh, Search, Sort } from '@element-plus/icons-vue';
import { ElMessageBox ,ElNotification } from "element-plus";
import instances from "@/utils/request";
import utils from '@/utils/utils'
import AddOrUpdate from './components/add-or-update.vue';
//搜索表单组件
const searchFormRef=ref()
//搜索表单数据实体
const searchForm=reactive({})
/**
 * 权限列表数据
 */
const dataList = ref([]);
/**
 * 新增/编辑表单引用
 */
const addOrUpdatemodal = ref(null)
/**
 * 加载权限数据
 */
const loadData = async () => {
    await instances.post("/permission/permissions",searchForm).then((res) => {
        dataList.value = res.data
    }).catch((error) => {
        console.error(error);
    });
}
/**
 * 打开新增/编辑对话框
 */
const openDialog = (id) => {
    addOrUpdatemodal.value.openDialog(id)
}
/**
 * 新增/更新回调事件
 */
const addOrUpdateSuccess = () => {
    loadData()
}
/**
 * 工具栏点击删除按钮
 */
const handleDelete = (id) => {
     ElMessageBox.confirm(`确认删除已选中的数据项?`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        //调用删除接口API
         await instances.delete(`/permission/permission/${id}`).then((res) => {
            utils.showSucess("删除成功!")  
            loadData()
        }).catch(err => { 
            console.error(err);
        })
    });
}
//点击查询按钮事件
const handleQuery = () => {
    loadData()
}
//重置分页查询form表单
const handleRest = () => {
    searchFormRef.value.resetFields()
    loadData()
}
onMounted(() => {
    loadData();
});
</script>

<style scoped>

</style>
