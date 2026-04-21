<template>
 <el-dialog :title="dialog.title" v-model="dialog.visible" @close="closeDialog" destroy-on-close appendToBody
        :close-on-click-modal=false width="40%">
        <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
            <el-form-item label="角色名称" prop="name">
                <el-input v-model="formData.name" size="large" placeholder="请输入角色名称" clearable />
            </el-form-item>
            <el-row>
                <el-col :span="12">
                    <el-form-item prop="menuPermission" label="菜单权限">
                        <el-tree-select size="large" v-model="formData.menuPermission" node-key="id" :props="defaultProps" :data="menuPermissionOption" multiple :render-after-expand="false" show-checkbox check-strictly/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item prop="controlsPermission" label="操作权限">
                         <el-select size="large" v-model="formData.controlsPermission" multiple collapse-tags collapse-tags-tooltip :max-collapse-tags="3" placeholder="操作权限可控制用户页面中的具体操作" >
                            <el-option v-for="item in controlsPermissionOption"  :key="item.id" :label="item.name" :value="item.id" />
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item label="备注" prop="description">
                <el-input size="large" v-model="formData.description" maxlength="50" placeholder="请输入描述" show-word-limit
                    type="textarea" />
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button type="primary" @click="submit">确 定</el-button>
            <el-button @click="closeDialog">取 消</el-button>
        </template>
    </el-dialog>
</template>

<script setup>
import { onMounted, ref ,reactive,nextTick} from "vue";
import {  Delete, Edit, Plus, Refresh, Search, Sort } from '@element-plus/icons-vue';
import instances from "@/utils/request";
import utils from '@/utils/utils'
const defaultProps = {
    children: 'children',
    label: 'name',
}
/**
 * 新增/编辑成功后回调事件
 */
const emit = defineEmits(['success'])
/**
 * 新增/编辑form表单 dialog配置
 */
const dialog= reactive({
    visible: false,
    title: ""
})
/**
 * 操作权限选项实例
 */
const controlsPermissionOption=ref([]);
/**
 * 菜单权限选项实例
 */
const menuPermissionOption=ref([]);
/**
 * 新增/编辑form表单引用
 */
const formRef = ref()

/**
 * 新增/编辑请求表单数据
 */
const formData = reactive({})
/**
 * 新增/编辑表单校验
 */
const formRules = reactive({
    name: [{ required: true, message: "角色名称不能为空", trigger: 'blur' }]
})
/**
 * 关闭新增/编辑dialog
 */
const closeDialog = () => {
    dialog.visible = false
    formRef.value.resetFields()
}
/**
 * 打开新增/编辑dialog
 */
const openDialog = async (id) => {
    loadPermission()
    loadMenuPermission()
    console.log(id);
    dialog.title = '新增角色';
    dialog.visible = true;
    formData.id = undefined
    if (id) {
        //如果是编辑，需要查询角色详情回显
        dialog.title = '编辑角色';
        await instances.get(`/role/role/${id}`).then((res) => {
            Object.assign(formData, res.data)
        }).catch(err => {
            console.error(err);
        })
    }

}
const submit = () => {
    formRef.value.validate(async (validate) => {
        if (validate) {
            if (formData.id) {
                //做更新操作
                instances.put("/role/role",formData).then((res)=>{ 
                    utils.showSucess("编辑成功!")  
                    closeDialog()
                    emit('success')
                }).catch(err => {
                    console.error(err);
                })
            } else {
                //做新增操作
                 instances.post("/role/role",formData).then((res)=>{ 
                    utils.showSucess("新增成功!")  
                    closeDialog()
                    emit('success')
                }).catch(err => { 
                    console.error(err);
                })
            }
        }
    })
}
//加载操作权限选项
const loadPermission = () => {
    instances.get("/permission/permissions").then((res) => {
        controlsPermissionOption.value=res.data;
    }).catch(err => {
        console.error(err);
    })
}

//加载菜单权限选项
const loadMenuPermission = () => {
    instances.post("/menu/loadMenus").then((res) => {
        menuPermissionOption.value=res.data;
    }).catch(err => {
        console.error(err);
    })
}
/**
 * 像父组件暴露打开关闭事件
 */
defineExpose({
    closeDialog,
    openDialog
})
</script>

<style scoped>

</style>
