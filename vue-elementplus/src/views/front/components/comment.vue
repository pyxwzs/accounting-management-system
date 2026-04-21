<template>
    <!-- blog post content end -->
                <hr class="mt-40 mb-40">
                <!-- blog post comments -->
                <div class="box-comment-main">
                    <h4 class="mb-15">{{comments.length}}条评论</h4>
                    <div class="box-comment" v-for="(comment,index) in comments" :key="index">
                        <div class="review-item d-flex align-items-center">
                            <figure>
                                <img :src="comment.user.img_url" alt="">
                                 <a @click="replyComment(comment.id)"><i class="fas fa-reply-all"></i> 回复</a>
                            </figure>
                            <div class="review-text">
                                <h6> {{comment.user.nick_name}} - <span>{{comment.user.create_time}} <span class="current-year"></span></span></h6>
                                <p v-html="comment.content"></p>
                            </div>
                        </div>
                        <div class="box-comment" v-for="(reply,index) in comment.replyList" :key="index">
                            <div class="review-item d-flex align-items-center">
                                <figure>
                                    <img :src="reply.user.img_url" alt=""> 
                                    <a @click="replyComment(reply.item_id,reply.user_id)"><i class="fas fa-reply-all"></i> 回复</a>
                                </figure>
                                <div class="review-text">
                                    <h6>{{reply.user.nick_name}} - <span>{{reply.user.create_time}} <span class="current-year"></span></span></h6>
                                  
                                    <p v-html="'@'+reply.send_name+reply.content"></p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <button v-if="show" class="btn btn-soft-primary btn-hover w-100 mt-2" @click="loadMore">查看更多</button>

                </div>
               
                <hr class="mt-40 mb-40">
              
                <div class="post-comment mb-25">
                    <h4 class="mb-15">我要评论</h4>
                    <el-form ref="formRef" :model="formData" :rules="formRules"  class="post-comment-form">
                        
                        <div class="mb-3">
                            <el-form-item  prop="content">
                                <WangEditor v-model="formData.content"  fileUploadUrl="/upload/other" label="内容"></WangEditor>
                            </el-form-item>
                        </div>
                        <button type="button" class="btn btn-style-1" @click="submit">提交评论 <i class="far fa-envelope"></i>
                        </button>
                         <Reply ref="replymodal" @success="addOrUpdateSuccess"></Reply>
                    </el-form>
                </div>
</template>
<script setup>
import { ref, reactive, onMounted,watch} from 'vue'
import instances from "@/utils/request";
import utils from '@/utils/utils'
import WangEditor from "@/common/components/editor/wangEditor.vue";
import Reply from "../components/reply.vue";
const props = defineProps({	
    item_id: {
		type: String,
		require: true,
	},	
})
const show=ref(true)
//评论form表单引用
const formRef = ref()
//评论请求表单数据
const formData = reactive({})
//评论表单校验
const validateNotEmptyOrWhitespace = (rule, value, callback) => {
  if (!value || value.trim() === '') {
    callback(new Error(rule.message));
  } else {
    // 检查是否只包含空的 HTML 标签（如 <p><br></p> 或 <p></p>）
    const trimmedValue = value.trim();
    // 匹配 <p> 标签内仅包含空白、<br> 或 其他空白标签
    const isEmptyHtml = /^<p>(<br\s*\/?>|\s|&nbsp;)*<\/p>$/i.test(trimmedValue);
    if (isEmptyHtml) {
      callback(new Error(rule.message));
    } else {
      callback();
    }
  }
};
const formRules = reactive({
  content: [
    { required: true, message: "内容不能为空", trigger: 'blur' },
    { validator: validateNotEmptyOrWhitespace, message: "内容不能为空或只包含空的 HTML 标签", trigger: 'blur' }
  ]
});
const comments = ref([])
//搜索表单数据实体
const searchForm=reactive({
    size: 20,
    current: 1,
    item_id:props.item_id
})
const loadMore=()=>{
    searchForm.current++
    loadComments();
}
const replymodal=ref(null)
const replyComment = (id,user_id) => {
 
    replymodal.value.openDialog(id,user_id)
}
const addOrUpdateSuccess = () => {
    comments.value=[]
    searchForm.current = 1
    loadComments();
}
const submit = () => {
    formRef.value.validate(async (validate) => {
        if (validate) {
            formData.item_id=props.item_id
                    //做新增操作
            instances.post("/comment/comment",formData).then((res)=>{ 
                utils.showSucess("留言成功!")
                comments.value=[]
                searchForm.current = 1 
                loadComments();
                formRef.value.resetFields()
            }).catch(err => { 
                console.error(err);
            })
        }
    })
}
watch(
  () => props.item_id,
  (newVal) => {
    searchForm.item_id = newVal;
    loadComments(); // 可选：是否立即重新加载
  }
);
const loadComments = async () => {
    await instances.post("/comment/comments/",searchForm).then((res) => {
        if(res.data.list.length==0){
            show.value=false
        }
        comments.value.push(...res.data.list);
       
    }).catch(err => { 
        console.error(err)
    })
}

</script>
<style src="@/assets/styles/front/bootstrap.min.css" scoped></style>
<style src="@/assets/styles/front/all.min.css" scoped></style>
<style src="@/assets/styles/front/owl.carousel.min.css" scoped></style>
<style src="@/assets/styles/front/owl.theme.default.min.css" scoped></style>
<style src="@/assets/styles/front/venobox.css" scoped></style>
<style src="@/assets/styles/front/custom.css" scoped></style>
<style src="@/assets/styles/front/responsive.css" scoped></style>
<style src="@/assets/styles/front/helper.css" scoped></style>
<style scoped>
.face{
    width: 30px;
    height: 30px;
    border-radius: 50%;
}
figure a{
    color: #fff !important;
}
</style>
