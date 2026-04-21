<template>
    <el-dialog :title="dialog.title" v-model="dialog.visible" @close="closeDialog" destroy-on-close appendToBody
        :close-on-click-modal=false width="40%">
        <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
            
            <el-form-item label="计划名称" prop="name">
                <el-input v-model="formData.name" placeholder="请输入计划名称" clearable />
            </el-form-item>
             <el-form-item label="币种" prop="type">
                <el-input v-model="formData.type" placeholder="请输入币种" clearable />
            </el-form-item>
             <el-form-item label="目标金额" prop="price">
                <el-input-number :min="0" v-model="formData.price" style="width: 200px;" placeholder="请输入金额" clearable />
            </el-form-item>
              <el-form-item label="存入金额" prop="hasPrice">
                <el-input-number :min="0" v-model="formData.hasPrice" style="width: 200px;" placeholder="请输入金额" clearable />
            </el-form-item>
            <el-form-item label="起始日期" prop="start_time">
              <el-date-picker v-model="formData.start_time" type="date" 
                    format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD" />
            </el-form-item>
             <el-form-item label="截止日期" prop="end_time">
              <el-date-picker v-model="formData.end_time" type="date" 
                    format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD" />
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
import { useUserStore } from "@/store/userStore";
import UploadImg from "@/common/components/upload/uploadImg.vue";
const userStore = useUserStore();
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
    type:'收入'
})
/**
 * 新增/编辑表单校验
 */
const formRules = reactive({
  
    price: [{ required: true, message: "目标金额不能为空", trigger: 'blur' }],
    hasPrice: [{ required: true, message: "存入金额不能为空", trigger: 'blur' }],
    type: [{ required: true, message: "币种不能为空", trigger: 'blur' }],
    name: [{ required: true, message: "计划名称不能为空", trigger: 'blur' }],
    start_time: [{ required: true, message: "起始日期不能为空", trigger: 'blur' }],
    end_time: [{ required: true, message: "截止日期不能为空", trigger: 'blur' }],
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
        await instances.get(`/plan/plan/${id}`).then((res) => {
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
                instances.put("/plan/plan",formData).then((res)=>{ 
                    utils.showSucess("编辑成功!")  
                    closeDialog()
                    emit('success')
                }).catch(err => {
                    console.error(err);
                })
            } else {
                //做新增操作
                 instances.post("/plan/plan",formData).then((res)=>{ 
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


onMounted(() => {
 
})
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
