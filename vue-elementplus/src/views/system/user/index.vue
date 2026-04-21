<template>
     <el-card shadow="never">
           <template #header>
            <el-form inline="true" :model="searchForm" ref="searchFormRef">
                <el-form-item prop="username" label="账号">
                    <el-input v-model="searchForm.username"  placeholder="请输入账号" clearable>
                    </el-input>
                </el-form-item>
                <el-form-item prop="phone" label="手机号">
                    <el-input v-model="searchForm.phone"  placeholder="请输入手机号" clearable>
                    </el-input>
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-select v-model="searchForm.status"  placeholder="请选择状态" clearable style="width: 120px;">
                        <el-option label="启用" value="1" />
                        <el-option label="禁用" value="0" />
                    </el-select>
                 </el-form-item>
                <el-form-item label="创建时间" prop="timeRange">
                    <el-date-picker v-model="searchForm.timeRange"  type="datetimerange" range-separator="-"
                        start-placeholder="开始时间" end-placeholder="结束时间" format="YYYY-MM-DD HH:mm:ss"
                        value-format="YYYY-MM-DD HH:mm:ss" />
                </el-form-item>
                <el-form-item>
                    <el-button type="success" :icon="Search" plain  @click="handleQuery" >搜索</el-button>
                    <el-button type="info" :icon="Refresh" plain  @click="handleRest" >重置</el-button>
                </el-form-item>
            </el-form>
            <div>
                <el-button v-permission="'user:insert'" type="primary" plain :icon="Plus"    @click="openDialog()">新增</el-button>
                <el-button v-permission="'user:delete'" type="danger" plain :icon="Delete"  :disabled="!selectIds.length" 
                    @click="handleMultipleDelete()">批量删除</el-button>
                     &nbsp; <SmartExport 
                        :data="pageTableData.list" 
                        :columns="exportColumns"
                        fileName="用户管理"
                        title="用户管理"
                    />
            </div>
        </template>
        <el-table :data="pageTableData.list" style="width: 100%" border  :header-cell-style="{fontSize: '13px !important',  color:'#111  !important'}" stripe  size="large" @selection-change="handleSelectionChange">
               <el-table-column type="expand">
                <template #default="scope">
                    <el-form class="menu-detail-container">
                        <el-row>
                            <el-col :span="6">
                                <el-form-item label="ID序号:">
                                    <span>{{ scope.row.id }}</span>
                                </el-form-item>
                            </el-col>
                            <el-col :span="6">
                                <el-form-item>
                                    <el-tooltip>
                                        <template #content> 复制</template>
                                        <el-icon>
                                            <CopyDocument @click="copyValue(scope.row.id)" />
                                        </el-icon>
                                    </el-tooltip>
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="6">
                                <el-form-item label="用户名:">
                                    <span>{{ scope.row.username }}</span>
                                </el-form-item>
                            </el-col>
                            <el-col :span="6">
                                <el-form-item label="姓名/昵称:">
                                    <span>{{ scope.row.real_name }} / {{ scope.row.nick_name }}</span>
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="6">
                                <el-form-item label="邮箱:">
                                    <span>{{ scope.row.email }}</span>
                                </el-form-item>
                            </el-col>
                            <el-col :span="6">
                                <el-form-item label="手机号:">
                                    <span>{{ scope.row.phone }}</span>
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="6">
                                <el-form-item label="登录密码">
                                    <el-tooltip>
                                        <template #content>
                                            {{ scope.row.password }}
                                        </template>
                                        <el-text type="primary" truncated>{{ scope.row.password }}</el-text>
                                    </el-tooltip>

                                </el-form-item>
                            </el-col>
                            <el-col :span="6">
                                <el-form-item>
                                    <el-tooltip>
                                        <template #content>
                                            复制
                                        </template>
                                        <el-icon>
                                            <CopyDocument @click="copyValue(scope.row.requestParam)" />
                                        </el-icon>
                                    </el-tooltip>

                                </el-form-item>

                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="6">
                                <el-form-item label="状态:">
                                    <el-tag v-if="scope.row.status === '1'"  effect="dark"  type="success">启用</el-tag>
                                    <el-tag v-else type="warning"   effect="dark"  >禁用</el-tag>
                                </el-form-item>
                            </el-col>
                            <el-col :span="6">
                                <el-form-item label="角色:">
                                      <el-text type="primary" truncated>{{ scope.row.role_name }}</el-text>
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="6">
                                <el-form-item label="创建时间:">
                                      <span>{{ scope.row.create_time }}</span>
                                </el-form-item>
                            </el-col>
                        </el-row>
                    </el-form>
                </template>
            </el-table-column>
            <el-table-column type="selection" width="55" />
            <el-table-column prop="username" label="账号" align="center" />
            <el-table-column prop="real_name" label="姓名" align="center" />
            <el-table-column prop="role_name" label="角色" align="center" />
            <el-table-column prop="age" label="年龄" align="center" />
            <el-table-column prop="sex" label="性别" align="center" />
            <el-table-column prop="phone" label="手机号" align="center" />
            <el-table-column prop="email" label="邮箱" align="center" />
            <el-table-column prop="status" label="状态" width="80">
                <template #default="scope">
                    <el-tag v-if="scope.row.status === '1'" effect="dark"  type="success">启用</el-tag>
                    <el-tag v-else type="warning" effect="dark" >禁用</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="create_time" label="创建日期" align="center" :show-overflow-tooltip=true width="180" />
            <el-table-column fixed="right" align="center" label="操作" width="300">
                <template #default="scope">
                    <el-button  v-permission="'user:edit'" plain :icon="Edit" type="warning" @click="openDialog(scope.row.id)"  size="small">编辑</el-button>

                    <el-button  v-permission="'user:resetPwa'" plain :icon="Key" type="primary" @click="resetPasswordDialog(scope.row.id)"  size="small">重置密码</el-button>

                    <el-button  v-permission="'user:delete'"  v-if="scope.row.roleId!=='1879449071886860289'" plain :icon="Delete" type="danger" @click="handleDelete(scope.row.id)"  size="small">删除</el-button>

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
</template>

<script setup>
import { ref, reactive, onMounted} from 'vue'
import {  Delete, Edit, Plus, Refresh, Search, Sort,Key } from '@element-plus/icons-vue';
import { ElMessageBox ,ElNotification } from "element-plus";
import instances from "@/utils/request";
import utils from '@/utils/utils'
import AddOrUpdate from './components/add-or-update.vue';
import SmartExport from "@/common/components/smartExport/smartExport.vue";
//搜索表单组件
const searchFormRef=ref()
//搜索表单数据实体
const searchForm=reactive({
    size: 20,
    current: 1
})
const exportColumns = [
  { prop: 'username', label: '账号' },
  { prop: 'real_name', label: '姓名' },
  { prop: 'role_name', label: '角色' },
  { prop: 'age', label: '年龄' },
  { prop: 'sex', label: '性别' },
  { prop: 'phone', label: '手机号' },
  { prop: 'email', label: '邮箱' },
  { prop: 'create_time', label: '创建日期' },

]
/**
 * 列表选中数据id集合
 */
const selectIds = ref([])
/**
 * 分页列表响应数据
 */
const pageTableData = reactive({
    list: [],
    total: 0
})
/**
 * 新增/编辑表单引用
 */
const addOrUpdatemodal = ref(null)
//点击查询按钮事件
const handleQuery = () => {
    loadData()
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
 * 
 * @param id 新增/更新事件
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
 * 删除用户
 * @param ids 用户id
 */
const openDeleteDialog = (ids) => {
    ElMessageBox.confirm(`确认删除已选中的数据项?`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        //调用删除接口API
        await instances.post("/user/delete/",ids).then((res) => {
        utils.showSucess("删除成功!")   
        loadData()
        }).catch(_err => { })
    });
}
/**
 * 重置密码弹窗警告
 * @param ids 
 */
 const resetPasswordDialog = (id) => {
    ElMessageBox.confirm(`确定重置该用户密码吗?一旦确认用户密码将被重置为默认密码`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        //调用重置密码接口接口API
        await instances.put("/user/resetPwa/",{'id':id}).then(res=>{
            utils.showSucess("重置成功!")
             loadData()  
        }).catch(err=>{
            console.error(err);
        })
    });
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
    await instances.post("/user/users/",searchForm).then(res => {
        pageTableData.list = res.data.list
        pageTableData.total = res.data.total
    }).catch(err => { 
         console.error(err)
    })
}
// 先给要复制的文本或者按钮加上点击事件后，并将要复制的值传过来
const copyValue = async (value) => {
    if (navigator.clipboard && window.isSecureContext) {
        // navigator clipboard 向剪贴板写文本
        ElNotification({
            title: 'Success',
            message: "复制成功",
            type: 'success',
        })
        await navigator.clipboard.writeText(value)
    } else {
        // 创建text area
        const textArea = document.createElement('textarea')
        textArea.value = value
        // 使text area不在viewport，同时设置不可见
        document.body.appendChild(textArea)
        textArea.focus()
        textArea.select()
        ElNotification({
            title: 'Success',
            message: "复制成功",
            type: 'success',
        })
        document.execCommand('copy')
        textArea.remove()

    }
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
