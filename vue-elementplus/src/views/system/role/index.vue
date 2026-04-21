<template>
  <el-card shadow="never">
        <template #header>
            <div>
                <el-button v-permission="'role:insert'" plain type="primary" :icon="Plus"  @click="openDialog()">新增</el-button>
            </div>
        </template>
        <el-table :data="dataList" style="width: 100%" border :header-cell-style="{fontSize: '13px !important' ,color:'#111  !important'}"  stripe  size="large">
            <el-table-column prop="id" label="ID序号" width="220"  />
            <el-table-column prop="name" label="角色名称"  width="180" />
            <el-table-column prop="description" label="描述"  />
            <el-table-column prop="controlsPermissionList" label="操作权限" show-overflow-tooltip min-width="300" >
                <template #default="scope">
                    <el-tag style="margin-right: 5px;"  effect="plain" v-for="(item,index) in scope.row.controlsPermissionList" :key="index" type="primary">{{item}}</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="menuPermissionList" label="菜单权限" show-overflow-tooltip  min-width="300">
                <template #default="scope">
                    <el-tag style="margin-right: 5px;"  effect="plain" v-for="(item,index) in scope.row.menuPermissionList" :key="index" type="info">{{item}}</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="create_name" label="创建人" align="center" width="280" />
            <el-table-column fixed="right" align="center" label="操作" width="200">
                <template #default="scope">
                    <el-button   v-permission="'role:edit'"  :icon="Edit" type="warning" plain @click="openDialog(scope.row.id)"  size="small">编辑</el-button>
                    <el-button  v-permission="'role:delete'"    :icon="Delete" type="danger" plain @click="handleDelete(scope.row.id)"  size="small">删除</el-button>

                </template>
            </el-table-column>
        </el-table>
    </el-card>
    <AddOrUpdate ref="addOrUpdatemodal" @success="addOrUpdateSuccess"></AddOrUpdate>

</template>

<script setup>
import instances from "@/utils/request";
import utils from '@/utils/utils'
import { Search, Refresh, Edit, Delete, Plus } from '@element-plus/icons-vue'
import { ElMessageBox ,ElNotification } from "element-plus";
import { onMounted, ref ,reactive,nextTick} from "vue";
import AddOrUpdate from './components/add-or-update.vue';
/**
 * 角色列表数据
 */
const dataList = ref([]);
/**
 * 新增/编辑表单引用
 */
const addOrUpdatemodal = ref(null)
/**
 * 加载角色数据
 */
const loadData = async () => {
    await instances.post("/role/roles").then((res) => {
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
 * 工具栏打开编辑按钮
 */
const handleEdit = (id) => {
    addOrUpdatemodal.value.openDialog(id)
}
/**
 * 新增/更新回调事件
 */
const addOrUpdateSuccess = () => {
    loadData()
}
/**
 * 
 * @param id 列表工具栏删除
 */
const handleDelete = (id) => {
     ElMessageBox.confirm(`确认删除已选中的数据项?`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        //调用删除接口API
         await instances.delete(`/role/role/${id}`).then((res) => {
            utils.showSucess("删除成功!")  
            loadData()
        }).catch(err => { 
            console.error(err);
        })
    });
}
onMounted(() => {
    loadData();
});
</script>

<style scoped>

</style>
