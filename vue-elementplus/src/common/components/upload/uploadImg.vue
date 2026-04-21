<template>
  <el-upload
    class="avatar-uploader"
    :show-file-list="false"
    :before-upload="beforeUpload"
    :on-success="handleSuccess"
    :on-error="handleError"
    :model-value="modelValue"
    :headers="{ Authorization: userStore.token }"
    action="/api/upload/other">
    <img v-if="imageUrl" :src="imageUrl" class="avatar" />
    <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
  </el-upload>
</template>

<script setup>
import utils from '@/utils/utils'
import { reactive, ref } from 'vue';
import ToolUtils from '@/utils/ToolUtils';
import { useUserStore } from '@/store/userStore'
const userStore = useUserStore()
const headers=ref({})
const option = defineProps({
	modelValue: {
		type: String,
		require: true
	},
    imageUrl: {
		type: String,
		require: true
	},
});
const emits = defineEmits(['update:modelValue']);
/**
 * 选择文件的时候校验
 * @param file 
 */
const beforeUpload = (file) => {
    if (!ToolUtils.isImageValid(file.name)) {
        ElMessage.error("文件格式错误，请上传图片类型,如：JPG，PNG、JPEG后缀的文件。");
        return false
    } else if (file.size / 1024 / 1024 > 10) {
        ElMessage.error('图片最大为10M!')
        return false
    }
}
const handleSuccess = (response,uploadFile) => {
    if(response.code !==0){
        utils.showError(response.message)
        return false
    }
    if (uploadFile && uploadFile.raw) {
        emits('update:modelValue', response.data);
        // imageUrl.value = URL.createObjectURL(uploadFile.raw);
    }
}
const handleError = (error, uploadFile, uploadFiles) => {
    ElMessage.error("上传失败:"+error.message);
}
</script>

<style scoped>

.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}
.avatar-uploader :deep(.el-upload) {
  border: 1px dashed  #dcdfe6;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: .2s;
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}
</style>
