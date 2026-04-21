<template>
    <el-dialog :title="dialog.title" v-model="dialog.visible" @close="closeDialog" destroy-on-close appendToBody
        :close-on-click-modal=false width="40%">
        <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
            <el-form-item label="回复" prop="content">
                 <WangEditor v-model="formData.content" 
                    fileUploadUrl="/upload/other" label="具体内容"></WangEditor>
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
const formData = reactive({
    content:''
})
/**
 * 新增/编辑表单校验
 */
const formRules = reactive({
    content: [{ required: true, message: "回复内容不能为空", trigger: 'blur' }],
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
const openDialog = async (id,send_id) => {
    dialog.title = '回复';
    dialog.visible = true;
    if (id) {
        formData.item_id=id
        formData.send_id=send_id
    }

}
const submit = () => {
    formRef.value.validate(async (validate) => {
        if (validate) {
            //做新增操作
            instances.post("/reply/replyItem",formData).then((res)=>{ 
                utils.showSucess("回复成功!")  
                closeDialog()
                emit('success')
            }).catch(err => { 
                console.error(err);
            })
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
