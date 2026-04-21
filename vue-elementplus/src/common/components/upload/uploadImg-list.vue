<template>
  <el-upload
    class="avatar-uploader"
    :before-upload="beforeUpload"
    :on-success="handleSuccess"
    :on-error="handleError"
    list-type="picture-card"
    :model-value="modelValue"
    
     :on-preview="handlePictureCardPreview"
    :headers="{ Authorization: userStore.token }"
    action="/api/upload/other">

    <el-icon  class="avatar-uploader-icon"><Plus /></el-icon>
  </el-upload>
   <el-dialog v-model="dialogVisible">
    <img w-full :src="dialogImageUrl" alt="Preview Image" />
  </el-dialog>
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
		type: Array,
		require: true
	}
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
        option.modelValue.push(response.data);
        emits('update:modelValue',  option.modelValue);
        // imageUrl.value = URL.createObjectURL(uploadFile.raw);
    }
}
const handleError = (error, uploadFile, uploadFiles) => {
    ElMessage.error("上传失败:"+error.message);
}
const dialogImageUrl = ref('')
const dialogVisible = ref(false)
const handlePictureCardPreview = (uploadFile) => {
  dialogImageUrl.value = uploadFile.url
  dialogVisible.value = true
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
