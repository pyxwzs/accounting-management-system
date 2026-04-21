<template>
 <el-dialog :title="dialog.title" v-model="dialog.visible" @close="closeDialog" destroy-on-close appendToBody
        :close-on-click-modal=false width="40%">
         
        <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
            <el-form-item label="用户名" prop="username">
                <el-input v-model="formData.username" :disabled="formData.id" placeholder="请输入用户名" size="large" clearable />
            </el-form-item>
            <el-form-item label="姓名" prop="real_name">
                <el-input v-model="formData.real_name" placeholder="请输入姓名" size="large" clearable />
            </el-form-item>
             <el-form-item label="性别" prop="sex">
                <el-select v-model="formData.sex" placeholder="请选择" size="large" clearable style="width: 100%;">
                    <el-option label="男" value="男" />
                    <el-option label="女" value="女" />
                </el-select>
            </el-form-item>
             <el-form-item label="年龄" prop="age">
                <el-input type="number" v-model="formData.age" placeholder="请输入年龄" size="large" clearable />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
                <el-input v-model="formData.email" placeholder="请输入邮箱" size="large" clearable />
            </el-form-item>
            <el-form-item label="密码" prop="password" v-if="!formData.id">
                 <el-text class="mx-1" type="info" size="small">默认为123456</el-text>
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
                <el-input v-model="formData.phone" placeholder="请输入手机号" size="large" clearable />
            </el-form-item>
            <el-form-item label="状态" prop="status">
                <el-switch v-model="formData.status" inline-prompt active-text="启用" inactive-text="禁用"
                    active-value="1" inactive-value="0" />
            </el-form-item>
            <el-form-item label="用户角色" prop="roleId">
                <el-select v-model="formData.roleId" placeholder="请选择" size="large" style="width: 100%;">
                    <el-option v-for="item in roleOptions" :key="item.id" :label="item.name" :value="item.id" />
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
import ToolUtils from '@/utils/ToolUtils';
/**
 * 角色下拉选择框
 */
 const roleOptions = ref([])
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
    email: [{
        required: false, validator(_rule, value, callback, _source, _options) {
            if (!formData.phone) {
                if(!value){
                    callback(new Error('手机号/邮箱必填一个'))
                }
            }
            if(value &&!ToolUtils.validateEmail(value)){
                    callback(new Error("邮箱格式不正确"))
                }
            callback()
        }, trigger: 'blur'
    }],
    phone: [{
        required: false, validator(_rule, value, callback, _source, _options) {
            if (value && !ToolUtils.validatePhoneNumber(value)) {
                callback(new Error("手机格式不正确"))
            }
            callback()
        }, trigger: 'blur'
    }],
    username: [
        { required: true, message: '请输入账号', trigger: 'blur' },
        { min: 5, max: 10, message: '长度在 5 到 10 个字符', trigger: 'blur' }
    ],
   
    status: [{ required: true, message: "状态不能为空", trigger: 'blur' }],
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
    loadRoleOptions()
    dialog.title = '新增用户';
    dialog.visible = true;
    formData.id = undefined
    if (id) {
        //如果是编辑，需要查询角色详情回显
        dialog.title = '编辑用户';
        await instances.get(`/user/user/${id}`).then((res) => {
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
                instances.put("/user/user",formData).then((res)=>{ 
                    utils.showSucess("编辑成功!")  
                    closeDialog()
                    emit('success')
                }).catch(err => {
                    console.error(err);
                })
            } else {
                //做新增操作
                 instances.post("/user/user",formData).then((res)=>{ 
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

//加载角色选项
const loadRoleOptions = () => {
    instances.post("/role/roleList").then((res) => {
        roleOptions.value=res.data;
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
.my-header {
	
    color: #000;
}
</style>
