<template>
    <el-card> 
        <template #header>
            <div>
                <el-button v-permission="'menu:insert'" plain  :icon="Plus"  type='primary' @click="handleAddMenu(false)">新增</el-button>
                <el-button :icon="Sort"  type='info' plain @click="handleToggle">展开/择叠</el-button>
            </div>
        </template>
        
        <el-table border  stripe :header-cell-style="{fontSize: '13px !important',  color:'#111  !important'}"  size="large" v-show="refreshTag" :key="tableKey" :data="menus" row-key="id" show-overflow-tooltip :default-expand-all="expendTag">
            <el-table-column prop="name" label="菜单名称" align="center" min-width="180"></el-table-column>
            <el-table-column prop="icon" label="图标" align="center" width="120">
                <template #default="scope">
                    <el-icon>
                        <component :is="scope.row.icon"></component>
                    </el-icon>
                </template>
            </el-table-column>
            <el-table-column prop="url" label="路由地址" align="center" width="220"></el-table-column>
            <el-table-column prop="component" label="组件路径" align="center" width="220"></el-table-column>
            <el-table-column prop="num" label="排序" align="center" width="80"></el-table-column>
            <el-table-column prop="create_time" label="创建时间" align="center" width="220" />
            <el-table-column fixed="right" label="操作"  align="center" min-width="120">
                <template #default="scope">
              
                    <el-button   v-permission="'menu:edit'"  :icon="Edit" type="warning" @click="handleEditMenu(scope.row.id)" plain size="small">编辑</el-button>
                    <el-button   v-permission="'menu:delete'"   :icon="Delete" type="danger" @click="handleDeleteMenu(scope.row.id)"  plain size="small">删除</el-button>

                </template>
            </el-table-column>
        </el-table>
        <AddOrUpdate ref="menuDialogModel" @success="handleSuccess"></AddOrUpdate>
    </el-card>
    
</template>

<script setup>
import { onMounted, ref ,reactive,nextTick} from "vue";
import {  Delete, Edit, Plus, Refresh, Search, Sort } from '@element-plus/icons-vue';
import { ElMessageBox ,ElNotification } from "element-plus";
import instances from "@/utils/request";
import utils from '@/utils/utils'
import svgSelect from "@/common/components/svg-select/index.vue";
import AddOrUpdate from './components/add-or-update.vue'
const menuDialogModel=ref(null)
const menus = ref([]);
/**
 * 加载菜单数据
 */
const loadData = async () => {
    await instances.post("/menu/loadMenus").then((res) => {
        menus.value = res.data
    }).catch((error) => {
        console.error(error);
    });
}
/**
 * 编辑菜单按钮事件
 */
const handleEditMenu=(menuId)=>{
    menuDialogModel.value.openMenuDialog(menuId)
}
/**
 * 新增菜单按钮事件
 */
const handleAddMenu = () => {
    menuDialogModel.value.openMenuDialog()
}
/**
 * 新增/编辑菜单成功
 */
const handleSuccess=()=>{
    loadData()
}
/**
 * 
 * @param id 删除弹窗警告
 */
 const handleDeleteMenu = (menuId) => {
    ElMessageBox.confirm(`确认删除已选中的数据项?`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
     //确认后调用接口删除菜单
        await instances.delete(`/menu/menu/${menuId}`).then((res) => {
            utils.showSucess("删除成功!")   
            loadData()
        }).catch(err=>{
            console.error(err);
        })
    });
}
/**
 * table 展开/择叠tag
 */
const expendTag=ref(true)

/**
 * table 刷新标识
 */
const refreshTag=ref(true)
/**
 * table key
 */
const tableKey=ref(Math.random())
/**
 * 菜单列表展开/择叠事件
 */
const handleToggle=()=>{
    expendTag.value=!expendTag.value
    refreshTag.value=false
    nextTick(()=>{
        refreshTag.value=true
        tableKey.value=Math.random()
    })
}
onMounted(() => {
    loadData();
});
</script>

<style scoped>
.el-icon:hover{
    color:var(--theme-hover-color);
}
</style>
