<template>
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="720" :close-on-click-modal="false"
        @close="handleCancel()">
        <el-form :model="menuForm" class="menu-detail-container" ref="menuDialogFormRef" :rules="menuDialogRules">
            <el-form-item label="父级菜单" prop="parent_id">
                <el-tree-select size="large" v-model="menuForm.parent_id" :data="menuOptions" check-strictly
                    :render-after-expand="false" style="width: 100%" node-key="id" :props="defaultProps"
                    placeholder="请选择上级菜单" :disabled="treeSelectTag"/>
            </el-form-item>
            <el-form-item label="菜单名称" prop="name">
                <el-input placeholder="请输入菜单名称" v-model="menuForm.name" size="large" clearable></el-input>
            </el-form-item>
            <el-form-item label="菜单图标" prop="icon">
                <SvgSelect ref="svgSelectModal" @select-icon="handleSvgSelect" @clean-select-icon="handleSvgClean" :icon-type="menuForm.iconType" :icon-name="menuForm.icon">
                </SvgSelect>
            </el-form-item>
            <el-row>
                <el-col :span="12">
                    <el-form-item prop="url">
                        <template #label>
                            <el-tooltip class="box-item" effect="dark"
                                content="访问的路由地址，如：`/sys/user`" placement="top">
                                <el-icon>
                                    <QuestionFilled />
                                </el-icon>
                            </el-tooltip>
                            路由地址
                        </template>
                        <el-input size="large" placeholder="请输入路由地址" v-model="menuForm.url" clearable></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item prop="component">
                        <template #label>
                            <el-tooltip class="box-item" effect="dark"
                                content="访问的组件路径，如：`/systems/menu/index`，默认在`views`目录下`" placement="top">
                                <el-icon>
                                    <QuestionFilled />
                                </el-icon>
                            </el-tooltip>
                            组件路径
                        </template>
                        <el-input size="large" placeholder="请输入组件路径" v-model="menuForm.component" clearable></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="排序" prop="num">
                        <el-input-number size="large" v-model="menuForm.num" :min="1" :max="100" controls-position="right" />
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="handleCancel()">取消</el-button>
                <el-button type="primary" @click="submitForm()">
                    确定
                </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup>
import { reactive, ref } from 'vue';
import SvgSelect from '@/common/components/svg-select/index.vue'
import ToolUtils from '@/utils/ToolUtils';
import instances from "@/utils/request";
import utils from '@/utils/utils'
const treeSelectTag=ref(false)
const svgSelectModal = ref(null)
const defaultProps = {
    children: 'children',
    label: 'name',
}
const menuDialogFormRef = ref()
const menuDialogRules = reactive({
    // parent_id: [{
    //     required: true, message: "父级菜单不能为空", trigger: 'blur'
    // }],
    name: [{
        required: true, message: "菜单名称不能为空", trigger: 'blur'
    }],
    
})
const dialog= reactive({
    visible: false,
    title: ''
})
const menuForm = reactive({
    name: '',
    num: 1,
})
const menuOptions = ref([])
/**
 * 打开对话框事件
 */
const openMenuDialog = async (menuId) => {
    dialog.title = '新增菜单'
    menuForm.id=undefined
    await instances.post("/menu/loadMenus").then((res) => {
        menuOptions.value=res.data;
    }).catch((error) => {
        console.error(error);
    });
    dialog.visible = true
    if(menuId){
        dialog.title = '编辑菜单'
        await instances.get(`/menu/menu/${menuId}`).then((res) => {
            Object.assign(menuForm,res.data)
        }).catch((error) => {
            console.error(error);
        });
    }
}
/**
 * 取消新增/编辑菜单
 */
const handleCancel = () => {
    dialog.visible = false
    //重置表单
    menuDialogFormRef.value.resetFields()
    svgSelectModal.value.cleanSelectIcon()
}
/**
 * 确定新增/编辑菜单
 */
const submitForm = async () => {
    menuDialogFormRef.value.validate((valid) => {
        if (valid) {
            if(menuForm.id){
                instances.put("/menu/menu",menuForm).then((res)=>{ 
                    utils.showSucess("编辑成功!")   
                    emit('success')
                    handleCancel()
                }).catch((error)=>{
                    console.log(error);
                })  
            }else{
                instances.post("/menu/menu",menuForm).then((res)=>{ 
                    utils.showSucess("新增成功!")   
                    emit('success')
                    handleCancel()
                }).catch((error)=>{
                    console.log(error);
                })  
            }
            
        } else{
            console.log('error submit!!');
        }
    });
}
/**
 * svg 图标选择器选中事件监听
 */
const handleSvgSelect = (name) => {
    menuForm.icon = name
}
/**
 * svg 清空事件监听
 */
const handleSvgClean = () => {
    menuForm.icon = ''
}
const emit = defineEmits(['success'])
defineExpose({
    openMenuDialog,

})
</script>

<style scoped>
.menu-detail-container:deep(.el-form-item__label) {
    width: 110px !important;
    justify-content: center;
    align-items: center;
}
</style>
