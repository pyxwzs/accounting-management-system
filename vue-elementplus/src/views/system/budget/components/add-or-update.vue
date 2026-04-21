<template>
    <el-dialog :title="dialog.title" v-model="dialog.visible" @close="closeDialog" destroy-on-close appendToBody
        :close-on-click-modal=false width="40%">
        <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
            <el-form-item label="名称" prop="name">
                <el-input v-model="formData.name" placeholder="请输入名称" clearable />
            </el-form-item>
             <el-form-item label="金额" prop="price">
                <el-input-number :min="0" v-model="formData.price" style="width: 200px;" placeholder="请输入金额" clearable />
            </el-form-item>
            <el-form-item label="统计周期" prop="cycle_type">
                <el-select v-model="formData.cycle_type" placeholder="请选择" style="width: 100%">
                    <el-option label="按月（如月可支配 6000）" value="month" />
                    <el-option label="按季" value="quarter" />
                    <el-option label="按年" value="year" />
                    <el-option label="不按周期（累计全部关联支出）" value="none" />
                </el-select>
            </el-form-item>
            <el-form-item label="预警比例" prop="warn_ratio">
                <el-slider v-model="formData.warn_ratio" :min="0.5" :max="0.99" :step="0.01" show-input :format-tooltip="(v) => Math.round(v * 100) + '%'" />
                <span class="hint">达到该比例时页面提醒；开启「按月/季/年」时，每日 9 点可能对注册邮箱发送预警邮件。</span>
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
 * 新增/编辑dialog配置
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
   
    name: [{ required: true, message: "名称不能为空", trigger: 'blur' }],
    price: [{ required: true, message: "金额不能为空", trigger: 'blur' }],
})
/**
 * 新增/编辑成功后回调事件
 */
const emit = defineEmits(['success'])
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
    dialog.title = '新增';
    dialog.visible = true;
    formData.id = undefined
    formData.cycle_type = 'month'
    formData.warn_ratio = 0.85
    if (id) {
        //如果是编辑，需要查询详情回显
        dialog.title = '编辑';
        await instances.get(`/budget/budget/${id}`).then((res) => {
            Object.assign(formData, res.data)
            if (formData.warn_ratio == null) formData.warn_ratio = 0.85
            if (!formData.cycle_type) formData.cycle_type = 'month'
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
                instances.put("/budget/budget",formData).then((res)=>{ 
                    utils.showSucess("编辑成功!")  
                    closeDialog()
                    emit('success')
                }).catch(err => {
                    console.error(err);
                })
            } else {
                //做新增操作
                 instances.post("/budget/budget",formData).then((res)=>{ 
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
 * 像父组件暴露打开关闭事件
 */
defineExpose({
    closeDialog,
    openDialog
})
</script>

<style scoped>
.hint {
    display: block;
    font-size: 12px;
    color: #909399;
    margin-top: 6px;
    line-height: 1.4;
}
</style>
