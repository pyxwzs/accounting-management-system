<template>
 <el-dialog :title="dialog.title" v-model="dialog.visible" @close="closeDialog" destroy-on-close appendToBody
        :close-on-click-modal=false width="30%">
        <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
            <el-form-item label="权限名称" prop="name">
                <el-input v-model="formData.name" placeholder="请输入权限名称" size="large" clearable />
            </el-form-item>
             <el-form-item label="权限标识" prop="mark">
                <el-input v-model="formData.mark" placeholder="请输入权限标识" size="large" clearable />
            </el-form-item>
            <el-form-item label="备注" prop="type">
                <el-select v-model="formData.type" placeholder="请输入选择类型" size="large" >
                    <el-option   label="增" value="增"/>
                    <el-option   label="删" value="删"/>
                    <el-option   label="改" value="改"/>
                    <el-option   label="查" value="查"/>
                </el-select>
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
    name: [{ required: true, message: "权限名称不能为空", trigger: 'blur' }],
    mark: [{ required: true, message: "权限标识不能为空", trigger: 'blur' }]
})
const submit = () => {
    formRef.value.validate(async (validate) => {
        if (validate) {
            if (formData.id) {
                //做更新操作
                instances.put("/permission/permission",formData).then((res)=>{ 
                    utils.showSucess("编辑成功!")  
                    closeDialog()
                    emit('success')
                }).catch(err => {
                    console.error(err);
                })
            } else {
                //做新增操作
                 instances.post("/permission/permission",formData).then((res)=>{ 
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
    dialog.title = '新增权限';
    dialog.visible = true;
    formData.id = undefined
    if (id) {
        //如果是编辑，需要查询角色详情回显
        dialog.title = '编辑权限';
        console.log(id);
        await instances.get(`/permission/permission/${id}`).then((res) => {
            Object.assign(formData, res.data)
        }).catch(err => {
            console.error(err);
        })
    }
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
