<template>
	<div class="rich-form-box">
		<Toolbar style="border-bottom: 1px solid var(--el-border-color)" :editor="edit" :defaultConfig="toolbarConfig"
			mode="default" />
		<Editor style="height: 500px;" :model-value="modelValue" :defaultConfig="editorConfig" mode="default"
			@onCreated="richFormEditorCreated" @onChange="handleChange" />
	</div>

	<!-- 文件上传的进度提示 -->
	<el-dialog v-model="fileUploadProgressVisable" :close-on-click-modal="false" :close-on-press-escape="false"
		:show-close="false" title="" fullscreen width="200px" modal append-to-body lock-scroll center align-center>
		<el-progress :percentage="curFileUploadProgress" />
	</el-dialog>
</template>


<script setup>
	import {
		ref,
		shallowRef,
		reactive,
		nextTick,
		onMounted,
		onBeforeUnmount
	} from "vue";
	//引入工具方法
	import utils from "@/utils/utils.js";
	import '@wangeditor/editor/dist/css/style.css' // 引入 css
    import { useUserStore } from '@/store/userStore'
	import {
		Editor,
		Toolbar
	} from '@wangeditor/editor-for-vue'
    const userStore = useUserStore()
	//外部参数
	const option = defineProps({
		//加载详情数据的url地址
		modelValue: {
			type: String,
			require: true
		},
		//展示的字段
		fileUploadUrl: {
			type: String,
			require: true
		},
		//展示的字段
		label: {
			type: String,
			require: true
		},
		//轻量级配置
		config: {
			type: Boolean,
			require: false
		}
	});
   
	//自定义事件
	const emits = defineEmits(['update:modelValue','change']);

	//是否展示进度条
	const fileUploadProgressVisable = ref(false);
	//当前的进度
	const curFileUploadProgress = ref(0);

	//富文本编辑器的实例
	const editorRef = reactive({});
	
	
	const toolbarConfig = reactive({});
	if (option.config) {
		toolbarConfig.excludeKeys=['headerSelect','blockquote','bold',
		'underline','italic','group-more-style','color','bgColor','fontSize','fontFamily',
		'lineHeight','bulletedList','numberedList','todo','group-justify','group-indent'
		,'insertLink','group-image','group-video','insertTable','codeBlock','divider','undo','redo','fullScreen'];
		
	}
	const edit = shallowRef();
	
	// let meta = {};
	// meta[config.Authorization] = userStore.token;
	const editorConfig = reactive({
		MENU_CONF: {
			uploadVideo: {
				server: '/api' + option.fileUploadUrl,
				fieldName: 'file',
				maxFileSize: 20 * 1024 * 1024,
				allowedFileTypes: ['video/*'],
				headers: {
                    'Authorization':userStore.token
                },
                
                customInsert(res, insertFn) {
                    // res 即服务端的返回结果
                    // 从 res 中找到 url alt href ，然后插入图片
                    console.log('customInsert',res);
                    insertFn(res.data,"","")
                },
				// meta: meta,
				// 上传之前触发
				onBeforeUpload(file) { // TS 语法
					fileUploadProgressVisable.value = true;
				},
				// 上传进度的回调函数
				onProgress(progress) { // TS 语法
					curFileUploadProgress.value = progress;
				},
				// 单个文件上传失败
				onFailed(file, err, res) { // TS 语法
					fileUploadProgressVisable.value = false;
					utils.showError("文件上传失败:" + err);
				},
				// 上传错误，或者触发 timeout 超时
				onError(file, err, res) { // TS 语法
					fileUploadProgressVisable.value = false;
					utils.showError("文件上传失败:" + err);
				},
                onSuccess(file, res) {
                    fileUploadProgressVisable.value = false;
                },
			},
			uploadImage: {
				server: '/api' + option.fileUploadUrl,
				fieldName: 'file',
				maxFileSize: 1 * 1024 * 1024,
				allowedFileTypes: ['image/*'],
                 // 自定义增加 http  header
                headers: {
                    'Authorization':userStore.token
                },
                customInsert(res, insertFn) {
                    // res 即服务端的返回结果
                    // 从 res 中找到 url alt href ，然后插入图片
                    console.log('customInsert',res);
                    insertFn(res.data,"","")
                },
				// meta: meta,
				// 上传之前触发
				onBeforeUpload(file) { // TS 语法
					fileUploadProgressVisable.value = true;
				},
				// 上传进度的回调函数
				onProgress(progress) { // TS 语法
					curFileUploadProgress.value = progress;
				},
				// 单个文件上传失败
				onFailed(file, err, res) { // TS 语法
					fileUploadProgressVisable.value = false;
					utils.showError("文件上传失败" , err);
				},
				// 上传错误，或者触发 timeout 超时
				onError(file, err, res) { // TS 语法
					fileUploadProgressVisable.value = false;
					utils.showError("文件上传失败:" , err);
				},
                onSuccess(file, res) {
                    fileUploadProgressVisable.value = false;
                },
            }
		},
		placeholder: '请输入' + option.label + '...',
		autoFocus : false
	});

	//富文本赋值
	const richFormEditorCreated = (editor) => {
		edit.value = editor;
	}

	onMounted(() => {

	});
	// 组件销毁时，也及时销毁编辑器
	onBeforeUnmount(() => {
		//销毁编辑器
		edit.value.destroy()
	})

	const handleChange = (editor) => {
		emits('update:modelValue', edit.value.getHtml());
		emits('change', edit.value.getHtml());
	}
	//文件上传之前
	const fileBeforeUpload = (rawFile) => {
		curFileUploadProgress.value = 0;
		fileUploadProgressVisable.value = true;
	}
	const fileUploadSuccess = (response, uploadFile, uploadFiles) => {
		fileUploadProgressVisable.value = false;
	}
	const fileUploadError = (error, uploadFile, uploadFiles) => {
		fileUploadProgressVisable.value = false;
		utils.showError("文件上传失败:" + error);
	}
	const fileUploadProgress = (evt, uploadFile, uploadFiles) => {
		curFileUploadProgress.value = evt.percent
	}
</script>

<style scoped>
	.rich-form-box {
		width: 100%;
		border: 1px solid var(--el-border-color);
		border-radius: var(--el-border-radius-base);
	}

	.rich-form-box:deep(.w-e-text-placeholder) {
		top: 11px
	}
</style>
