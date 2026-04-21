<template>
    <div class="user-info-head" @click="openUploadImage">
        <el-avatar :size="90" :src="options.img">
            <template #default>
                <img src="@/assets/images/default_userimg.png" />
            </template>
        </el-avatar>
    </div>
    <el-dialog title="修改头像" v-model="dialog.visible" @close="closeDialog" destroy-on-close appendToBody
        :close-on-click-modal=false width="720px">
        <el-row>
            <el-col :xs="24" :md="12" :style="{ height: '350px' }">
                <vue-cropper ref="cropper" :img="options.img" :info="true" :autoCrop="options.autoCrop"
                    :autoCropWidth="options.autoCropWidth" :autoCropHeight="options.autoCropHeight"
                    :fixedBox="options.fixedBox" :outputType="options.outputType" @realTime="realTime"
                    v-if="dialog.visible" />
            </el-col>
            <el-col :xs="24" :md="12" :style="{ height: '350px' }">
                <div class="avatar-upload-preview">
                    <img :src="previewData?.url" :style="previewData?.img" />
                </div>
            </el-col>
        </el-row>
        <br />
        <el-row>
            <el-col :lg="2" :sm="3" :xs="3">
                <el-upload :limit="1" :show-file-list="false" :before-upload="beforeUpload">
                    <template #trigger>
                        <el-button size="small">选择</el-button>
                    </template>
                </el-upload>
            </el-col>
            <el-col :lg="{ span: 1, offset: 2 }" :sm="2" :xs="2">
                <el-button :icon="Plus" size="small" @click="changeScale(1)"></el-button>
            </el-col>
            <el-col :lg="{ span: 1, offset: 1 }" :sm="2" :xs="2">
                <el-button :icon="Minus" size="small" @click="changeScale(-1)"></el-button>
            </el-col>
            <el-col :lg="{ span: 1, offset: 1 }" :sm="2" :xs="2">
                <el-button :icon="RefreshLeft" size="small" @click="rotateLeft()"></el-button>
            </el-col>
            <el-col :lg="{ span: 1, offset: 1 }" :sm="2" :xs="2">
                <el-button :icon="RefreshRight" size="small" @click="rotateRight()"></el-button>
            </el-col>
            <el-col :lg="{ span: 2, offset: 6 }" :sm="2" :xs="2">
                <el-button type="primary" size="small" @click="uploadImage">提 交</el-button>
            </el-col>
        </el-row>
    </el-dialog>

</template>

<script setup>
import 'vue-cropper/dist/index.css'
import { useUserStore } from '@/store/userStore';
import { reactive, ref } from 'vue';
import { Minus, Plus, RefreshLeft, RefreshRight } from '@element-plus/icons-vue';
import { VueCropper } from 'vue-cropper';
import ToolUtils from '@/utils/ToolUtils';
import { useUploadAvatarImageApi } from '@/api/index';
import utils from '@/utils/utils'

//用户仓库
const userStore = useUserStore()
const options = ref({
    img: userStore.user.img_url,  //裁剪图片的地址
    autoCrop: true,             // 是否默认生成截图框
    autoCropWidth: 200,         // 默认生成截图框宽度
    autoCropHeight: 200,        // 默认生成截图框高度
    fixedBox: true,             // 固定截图框大小 不允许改变
    outputType: "png",           // 默认生成截图为PNG格式
    filename: 'avatar'          // 文件名称
})
//vue-cropper组件引用
const cropper = ref()
const dialog = reactive({
    visible: false,
    title: ""
})
const openUploadImage = () => {
    dialog.visible = true
}
const closeDialog = () => {
    dialog.visible = false
}

//实时的更新剪裁后的图片数据
const previewData = ref()
const realTime = (data) => {
    previewData.value = data
}
//图片缩放
const changeScale = (num) => {
    num = num || 1
    cropper.value.changeScale(num)
}
//向左旋转
const rotateLeft = () => {
    cropper.value.rotateLeft()
}
//向右旋转
const rotateRight = () => {
    cropper.value.rotateRight()
}
/**
 * 选择文件的时候校验
 * @param file 
 */
const beforeUpload = (file) => {
    if (!ToolUtils.isImageValid(file.name)) {
        ElMessage.error("文件格式错误，请上传图片类型,如：JPG，PNG、JPEG后缀的文件。");
    } else {
        const reader = new FileReader()
        reader.readAsDataURL(file) //将文件转化为base64
        reader.onload = e => {
            let data
            if (!e.target) return
            if (typeof e.target.result === 'object') {
                data = URL.createObjectURL(new Blob([e.target.result ?? '']))
            } else {
                data = e.target.result
            }
            options.value.img = data

        }
    }
}
const uploadImage = () => {
  //获取截图的blob数据
  cropper.value.getCropBlob(async (data) => {
    const formData = new FormData();
    formData.append('file', data, `${options.value.filename}.${options.value.outputType}`);
    await useUploadAvatarImageApi(formData).then(res => {
        utils.showSucess("上传成功!")   
        options.value.img = res.data;
        userStore.user.img_url = res.data
        closeDialog()
    }).catch(_err => {
      console.log(_err);

    })


  })

}
</script>

<style scoped>
.user-info-head {
    position: relative;
    display: inline-block;
    height: 90px;
}

.user-info-head:hover:after {
    content: '+';
    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    color: #eee;
    background: rgba(0, 0, 0, 0.5);
    font-size: 24px;
    font-style: normal;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    cursor: pointer;
    line-height: 90px;
    border-radius: 50%;
}

.avatar-upload-preview {
    position: relative;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 200px;
    height: 200px;
    border-radius: 50%;
    box-shadow: 0 0 4px #ccc;
    overflow: hidden;
}
</style>