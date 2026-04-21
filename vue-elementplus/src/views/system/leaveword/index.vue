<template>

    <el-card class="card" shadow="never">
        <template #header>
            <el-form :inline="true" :model="searchForm" ref="searchFormRef">
                <el-form-item prop="user_id" label="留言用户">
                    <el-select v-model="searchForm.user_id" filterable remote reserve-keyword placeholder="请输入用户名查找"
                        :remote-method="remoteMethod"
                        :loading="loading"
                        style="width: 240px" >
                            <el-option  v-for="(item,index) in userList" :key="index"  :label="item.username"  :value="item.id" />
                    </el-select>
                </el-form-item>
                 <el-form-item label="分类" prop="type">
                    <el-select v-model="searchForm.type" placeholder="请选择类型" clearable  style="width: 120px;">
                        <el-option label="功能问题" value="功能问题" />
                        <el-option label="体验与交互" value="体验与交互" />
                        <el-option label="内容问题" value="内容问题" />
                        <el-option label="性能与网络" value="性能与网络" />
                        <el-option label="其他反馈" value="其他反馈" />
                    </el-select>
                </el-form-item>
                <el-form-item label="创建时间" prop="timeRange">
                    <el-date-picker v-model="searchForm.timeRange" type="datetimerange" size="large" range-separator="-"
                        start-placeholder="开始时间" end-placeholder="结束时间" format="YYYY-MM-DD HH:mm:ss"
                        value-format="YYYY-MM-DD HH:mm:ss" />
                </el-form-item>
                <el-form-item>
                    <el-button plain type="success" :icon="Search"  @click="handleQuery" >搜索</el-button>
                    <el-button plain type="info" :icon="Refresh"  @click="handleRest" >重置</el-button>
                </el-form-item>
            </el-form>
            <div>
                <el-button  plain type="danger" :icon="Delete"  :disabled="!selectIds.length" 
                    @click="handleMultipleDelete()">批量删除</el-button>
            </div>
        </template>
         <p class="info-text">
             <el-text class="mx-1" type="primary" size="small">双击单元行查看回复信息</el-text>
        </p>
        <el-table :data="pageTableData.list" border style="width: 100%" @selection-change="handleSelectionChange" @cell-dblclick="handleCellDblclick" :header-cell-style="{fontSize: '13px !important' ,color:'#111  !important'}"  stripe  size="large">
              
            <el-table-column type="selection" width="55" />
             <el-table-column prop="user"  width="55">
                <template #default="scope">
                    <el-image class="user-img" :src="scope.row.user.img_url" />
                </template>
            </el-table-column>
             <el-table-column prop="user" label="留言用户">
                <template #default="scope">
                   <el-text>{{scope.row.user.username}}</el-text>
                    (<el-text>{{scope.row.user.nick_name}}</el-text>)
                </template>
            </el-table-column>
            <el-table-column prop="type" label="分类"  /> 
            <el-table-column prop="content" label="内容"  >
                <template #default="scope">
                    <p v-html="scope.row.content"></p>
                </template>
            </el-table-column>
            <el-table-column prop="create_time" label="创建日期" align="center" :show-overflow-tooltip="true" width="180" />
            <el-table-column fixed="right" align="center" label="操作" width="300">
                <template #default="scope">  
                    <el-button :icon="ChatDotSquare"     type="success" @click="openDialog(scope.row.id,scope.row.user_id)" plain size="small" >回复</el-button>
                    <el-button :icon="Delete"   type="danger" @click="handleDelete(scope.row.id)"  plain size="small" >删除</el-button>
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
    <AddOrUpdate ref="addOrUpdatemodal" @success="addOrUpdateSuccess"></AddOrUpdate>
    <Reply ref="replymodal"></Reply>
   
</template>

<script setup>
import {ElMessageBox ,ElNotification} from 'element-plus'
import { ref, reactive, onMounted} from 'vue'
import {  Delete, Edit, Plus, Refresh, Search, Sort ,ChatDotSquare} from '@element-plus/icons-vue';
import instances from "@/utils/request";
import utils from '@/utils/utils'
import AddOrUpdate from './components/add-or-update.vue';
import Reply from '../reply/index.vue';



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
const replymodal=ref(null)
//双击单元行事件
const handleCellDblclick= (row) => {
   replymodal.value.listDialog(row.id)
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
        searchForm.create_time = searchForm.timeRange[0]+' - '+ searchForm.timeRange[1]
    } else {
        searchForm.create_time = undefined
    }
    //调用分页接口API
    await instances.post("/leaveword/leavewords/",searchForm).then(res => {
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
const openDialog = (id,send_id) => {
    addOrUpdatemodal.value.openDialog(id,send_id)
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
        await instances.post("/leaveword/delete/",ids).then((res) => {
        utils.showSucess("删除成功!")   
        loadData()
        }).catch(_err => { 
            console.error(_err)
        })
    });
}
const userList= ref([])
const remoteMethod = (query) => {
    if (query) {
        loading.value = true;
        instances.post("/user/userList/", { username: query })
            .then(res => {
                userList.value = res.data.filter((item) => {
                    return item.username.toLowerCase().includes(query.toLowerCase());
                });
            })
            .catch(err => {
                console.error("用户列表获取失败:", err);
                userList.value = [];
            })
            .finally(() => {
                loading.value = false;
            });
    } else {
        userList.value = [];
    }
};
onMounted(() => {
      loadData()
})
</script>

<style scoped>
.pagination-container {
    padding: 10px 0px 0px;
}
.user-img{
    width: 40px;
    height: 40px;
    border-radius: 50%;
}
</style>
