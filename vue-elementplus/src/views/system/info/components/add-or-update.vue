<template>
    <el-dialog :title="dialog.title" v-model="dialog.visible" @close="closeDialog" destroy-on-close appendToBody
        :close-on-click-modal=false width="40%">
        <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
            <el-form-item label="名称" prop="name">
                <el-input v-model="formData.name" placeholder="请输入名称" clearable />
            </el-form-item>
            <el-form-item label="类型" prop="type">
                <el-select v-model="formData.type" placeholder="请选择" clearable style="width: 100%;">
                    <el-option label="收入" value="收入" />
                    <el-option label="支出" value="支出" />
                </el-select>
            </el-form-item>
             <el-form-item label="分类" prop="classify">
                <el-select v-model="formData.classify" placeholder="请选择" clearable style="width: 100%;" v-if="formData.type==='支出'">
                    <el-option label="衣服" value="衣服" />
                    <el-option label="零食" value="零食" />
                    <el-option label="三餐" value="三餐" />
                    <el-option label="住房" value="住房" />
                    <el-option label="日用品" value="日用品" />
                    <el-option label="烟酒" value="烟酒" />
                    <el-option label="娱乐" value="娱乐" />
                    <el-option label="其他" value="其他" />
                </el-select>
                 <el-select v-model="formData.classify" placeholder="请选择" clearable style="width: 100%;" v-if="formData.type==='收入'">
                    <el-option label="工资" value="工资" />
                    <el-option label="生活费" value="生活费" />
                    <el-option label="红包" value="红包" />
                    <el-option label="外快" value="外快" />
                    <el-option label="理财投资" value="理财投资" />
                    <el-option label="其他" value="其他" />
                </el-select>
            </el-form-item>
             <el-form-item label="预算" prop="budget_id" v-if="formData.type==='支出'">
                <el-select v-model="formData.budget_id" placeholder="请选择" clearable style="width: 100%;">
                    <el-option v-for="(item,index) in options" :key="index" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>
             <el-form-item label="金额" prop="price">
                <el-input-number :min="0" v-model="formData.price" style="width: 200px;" placeholder="请输入金额" clearable />
            </el-form-item>
              <el-form-item label="日期" prop="day">
              <el-date-picker v-model="formData.day" type="date" 
                    format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD" />
            </el-form-item>
             <el-form-item label="备注" prop="content">
                <el-input v-model="formData.content" placeholder="请输入备注" clearable />
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
    
    name: [{ required: true, message: "名称不能为空", trigger: 'blur' }],
    day: [{ required: true, message: "日期不能为空", trigger: 'blur' }],
    price: [{ required: true, message: "金额不能为空", trigger: 'blur' }],
    type: [{ required: true, message: "类型不能为空", trigger: 'blur' }],
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
        await instances.get(`/info/info/${id}`).then((res) => {
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
                instances.put("/info/info",formData).then((res)=>{ 
                    utils.showSucess("编辑成功!")  
                    closeDialog()
                    emit('success')
                }).catch(err => {
                    console.error(err);
                })
            } else {
                //做新增操作
                 instances.post("/info/info",formData).then((res)=>{ 
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
const options=ref([])
const loadOptions=()=>{
     instances.post("/budget/list",{'user_id':userStore.user.id}).then((res)=>{ 
          options.value=res.data 
    }).catch(err => { 
        console.error(err);
    })
}
onMounted(() => {
    loadOptions()
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
