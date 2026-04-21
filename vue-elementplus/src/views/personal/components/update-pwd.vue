<template>
    <el-dialog v-model="updatePwdDialog.visible" :title="updatePwdDialog.title" center width="30%" align-center
        @close="closeUpdatePwdDialog">
        <ElForm ref="updatePwdRef" :rules="updatePwdRules" :model="updatePwdForm">
            <ElFormItem prop="oldPwd">
                <ElInput placeholder="请输入原密码" v-model="updatePwdForm.oldPwd" show-password :prefix-icon="Lock"
                    size="large">
                </ElInput>
            </ElFormItem>
            <ElFormItem prop="newPwd">
                <ElInput placeholder="请输入新密码" show-password :prefix-icon="Lock" v-model="updatePwdForm.newPwd"
                    size="large"></ElInput>
            </ElFormItem>
            <ElFormItem prop="rePwd">
                <ElInput placeholder="请输入确认密码" show-password :prefix-icon="Lock" v-model="updatePwdForm.rePwd"
                    size="large"></ElInput>
            </ElFormItem>
            <ElFormItem>

                <ElButton type="primary" style="width: 100%;" @click="submit()">确定</ElButton>
            </ElFormItem>
        </ElForm>
    </el-dialog>
</template>

<script setup >
import ToolUtils from '@/utils/ToolUtils';
import { reactive, ref } from 'vue';
import { Lock } from '@element-plus/icons-vue';
import instances from "@/utils/request";
import utils from '@/utils/utils'
const updatePwdDialog= reactive({
    title: '修改密码',
    visible: false
})
/**
 * 关闭更新密码组件
 */
const closeUpdatePwdDialog = () => {
    updatePwdDialog.visible = false
    updatePwdRef.value.resetFields()

}
/**
 * 打开更新密码组件
 */
const openUpdatePwdDialog = () => {
    updatePwdDialog.visible = true
}

/**
 * 像父组件暴露弹出事件
 */
defineExpose({
    openUpdatePwdDialog
})
/**
 * 更新密码请求DTO
 */
const updatePwdForm = reactive({
    oldPwd: "",
    newPwd: "",
    rePwd: ""
})
/**
 * 更新密码表单引用
 */
const updatePwdRef = ref()
/**
* 根据账号更新重置密码表单校验
*/
const updatePwdRules = reactive({
    oldPwd: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur'}
    ],
    newPwd: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur'}
    ],
    rePwd: [{
        required: true, message: "确认密码不能为空", trigger: 'blur'
    },
    {
        required: true, validator(_rule, value, callback, _source, _options) {
            if (value !== updatePwdForm.newPwd) {
                callback(new Error("密码输入密码不一致"))
            }
            callback()
        }, trigger: 'blur'
    }]
})
/**
 * 更新成功回调
 */
const emit = defineEmits(['success'])
/**
* 
* @param formEl 确认按钮事件
*/
const submit = () => {
    updatePwdRef.value.validate(async (validate) => {
        if (validate) {
            await instances.put("/user/updatePwd",updatePwdForm).then(res => {
                utils.showSucess("密码修改成功,请重新登录")   
                closeUpdatePwdDialog()
                emit("success")
            }).catch(_error => { 
                console.log(_error)
            })
        }
    })
}
</script>

<style scoped lang="scss"></style>
