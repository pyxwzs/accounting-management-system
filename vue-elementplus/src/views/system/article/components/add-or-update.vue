<template>
    <el-dialog :title="dialog.title" v-model="dialog.visible" @close="closeDialog" destroy-on-close appendToBody
        :close-on-click-modal=false width="40%">
        <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
            <el-form-item label="标题" prop="name">
                <el-input v-model="formData.name" placeholder="请输入名称" size="large" clearable />
            </el-form-item>
            <el-form-item label="原文链接" prop="source_url">
                <el-input v-model="formData.source_url" placeholder="选填，外链阅读原文" size="large" clearable />
            </el-form-item>
            <!-- <el-form-item label="类别" prop="type">
                <el-select v-model="formData.type" placeholder="请选择" size="large" clearable style="width: 100%;">
                    <el-option label="1" value="1" />
                </el-select>
            </el-form-item> -->
            <el-form-item label="封面图" prop="img">
               <UploadImg v-model="formData.img" :imageUrl="formData.img"></UploadImg>
            </el-form-item>
            <el-form-item label="内容" prop="content">
                <WangEditor v-model="formData.content" 
                    fileUploadUrl="/upload/other" label="内容"></WangEditor>
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
import WangEditor from "@/common/components/editor/wangEditor.vue";
import UploadImg from "@/common/components/upload/uploadImg.vue";
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
    content: [{ required: true, message: "内容不能为空", trigger: 'blur' }],
    name: [{ required: true, message: "标题不能为空", trigger: 'blur' }],
    img: [],
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
    if (id) {
        //如果是编辑，需要查询详情回显
        dialog.title = '编辑';
        await instances.get(`/article/article/${id}`).then((res) => {
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
                instances.put("/article/article",formData).then((res)=>{ 
                    utils.showSucess("编辑成功!")  
                    closeDialog()
                    emit('success')
                }).catch(err => {
                    console.error(err);
                })
            } else {
                //做新增操作
                 instances.post("/article/article",formData).then((res)=>{ 
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

</style>
