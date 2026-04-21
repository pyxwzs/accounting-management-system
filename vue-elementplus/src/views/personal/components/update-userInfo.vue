<template>
    <el-dialog v-model="updateUserInfoDialog.visible" :title="updateUserInfoDialog.title" center width="30%" align-center
        @close="closeUpdateUserInfoDialog">
        <el-form ref="updateUserInfoRef" :rules="updateUserInfoRules" :model="updateUserInfoForm">
             <el-form-item label="昵称" prop="nick_name">
                <el-input v-model="updateUserInfoForm.nick_name" placeholder="请输入" clearable size="large"></el-input>
            </el-form-item>
            <el-form-item label="姓名" prop="real_name">
                <el-input v-model="updateUserInfoForm.real_name" placeholder="请输入" clearable size="large"></el-input>
            </el-form-item>
            <el-form-item label="手机" prop="phone">
                <el-input v-model="updateUserInfoForm.phone" placeholder="请输入" clearable size="large"></el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
                <el-input v-model="updateUserInfoForm.email" placeholder="请输入" clearable size="large"></el-input>
            </el-form-item>
            <el-form-item label="性别" prop="sex">
                <el-radio-group v-model="updateUserInfoForm.sex">
                    <el-radio value="男">男</el-radio>
                    <el-radio value="女">女</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" style="width: 100%;" @click="submit()">确定</el-button>
            </el-form-item>
        </el-form>
    </el-dialog>
</template>

<script setup >
import ToolUtils from '@/utils/ToolUtils';
import { reactive, ref } from 'vue';
import { Lock } from '@element-plus/icons-vue';
import instances from "@/utils/request";
import utils from '@/utils/utils'
import { useUserStore } from '@/store/userStore';
const userStore = useUserStore()
const updateUserInfoDialog= reactive({
    title: '修改个人信息',
    visible: false
})
/**
 * 关闭更新密码组件
 */
const closeUpdateUserInfoDialog = () => {
    updateUserInfoDialog.visible = false
    updateUserInfoRef.value.resetFields()

}
/**
 * 打开更新密码组件
 */
const openUpdateUserInfoDialog = () => {
    updateUserInfoDialog.visible = true
}

/**
 * 像父组件暴露弹出事件
 */
defineExpose({
    openUpdateUserInfoDialog
})
const updateUserInfoForm= reactive({
    nick_name: userStore.user.nick_name,
    real_name: userStore.user.real_name,
    phone: userStore.user.phone,
    email: userStore.user.email,
    sex: userStore.user.sex,
    id: userStore.user.id
})
/**
 * 表单引用
 */
const updateUserInfoRef = ref()
/**
 * 表单校验
 */
const updateUserInfoRules= reactive({
    nick_name: [{ required: true, message: "昵称称长度为1-20", min: 1, max: 20, trigger: 'blur'}],
    real_name:[{ required: true, message: "姓名不能为空", trigger: 'blur' }],
    sex: [{ required: true, message: "性别不能为空", trigger: 'blur' }],
    email:  [{
        required: true, validator(_rule, value, callback, _source, _options) {
            if (ToolUtils.validateEmail(value)) {
                callback()
            }
            callback(new Error("邮箱格式不正确，请输入正确的邮箱"))
        }, trigger: 'blur'
    }],
    phone:   [{
        required: true, validator(_rule, value, callback, _source, _options) {
            if (ToolUtils.validatePhoneNumber(value)) {
                callback()
            }
            callback(new Error("手机号格式不正确，请输入正确的手机号"))
        }, trigger: 'blur'
    }],
})
/**
 * 
 * @param formEl 确认修改按钮事件
 */
 const submit = () => {
    updateUserInfoRef.value.validate(async (validate) => {
        if (validate) {
            instances.put("/user/user",updateUserInfoForm).then(res => {
                utils.showSucess("修改成功!")  
                userStore.user.nick_name = updateUserInfoForm.nick_name
                userStore.user.real_name = updateUserInfoForm.real_name
                userStore.user.sex = updateUserInfoForm.sex
                userStore.user.phone = updateUserInfoForm.phone
                userStore.user.email = updateUserInfoForm.email
                closeUpdateUserInfoDialog()
            }).catch(_err => { })


        }
    })
}
</script>

<style scoped >

</style>
