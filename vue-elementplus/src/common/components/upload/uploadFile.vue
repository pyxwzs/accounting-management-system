<template>
  <el-upload
    class="avatar-uploader"
    :on-success="handleSuccess"
    :on-error="handleError"
    :before-upload="beforeUpload"
    :limit="1"
    :model-value="modelValue"
    :headers="{ Authorization: userStore.token }"
    action="/api/upload/other">
    <el-button type="primary">上传文件</el-button>
    <template #tip>
      <div class="el-upload__tip">
         文件大小不得超过50M
      </div>
    </template>
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
	}
});
const emits = defineEmits(['update:modelValue']);
/**
 * 选择文件的时候校验
 * @param file 
 */
const beforeUpload = (file) => {
   if (file.size / 1024 / 1024 > 50) {
        ElMessage.error('视频最大为100M!')
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
