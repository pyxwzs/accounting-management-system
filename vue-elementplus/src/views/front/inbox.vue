<template>
  <Header></Header>
  <div class="inner-page-header">
    <div class="container">
      <h1 class="mb-8">收件箱</h1>
      <ol class="breadcrumb">
        <li class="breadcrumb-item"><router-link to="/front"><i class="fas fa-home"></i> 首页</router-link></li>
        <li class="breadcrumb-item active" aria-current="page">收件箱</li>
      </ol>
    </div>
  </div>
  <div class="services-page pt-80 pb-55">
    <div class="container">
      <p class="inbox-intro text-muted mb-4">
        系统公告与您的意见反馈统一在此查看：在「系统公告」浏览平台通知；在「意见反馈」查看您提交的记录及管理员回复，并可继续提交反馈。
      </p>

      <el-tabs v-model="activeTab" class="inbox-tabs" @tab-change="onTabChange">
        <el-tab-pane label="系统公告" name="notice">
          <div class="row row-cols-lg-3 row-cols-md-2 row-cols-sm-1 row-cols-1">
            <div class="col mb-25" v-for="(item, index) in noticeList.list" :key="index">
              <div class="icon-box-1">
                <div class="icon"><i class="fas fa-bullhorn"></i></div>
                <h3 class="title mb-20">
                  <router-link :to="{ path: '/notice/' + item.id }">{{ item.title }}</router-link>
                </h3>
                <p>{{ item.create_time }}</p>
              </div>
            </div>
          </div>
          <div class="pagination-container" v-if="noticeList.total > noticeSearch.size">
            <el-pagination
              v-model:current-page="noticeSearch.current"
              v-model:page-size="noticeSearch.size"
              :page-sizes="[12, 24, 36]"
              layout="total, sizes, prev, pager, next"
              :total="noticeList.total"
              @size-change="loadNotices"
              @current-change="loadNotices"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="意见反馈" name="feedback">
          <div class="box-comment-main">
            <h4 class="mb-15">我的反馈（{{ feedbackList.length }} 条）</h4>
            <div v-if="!feedbackList.length" class="text-muted mb-3">暂无反馈记录，请在下方提交。</div>
            <div class="box-comment" v-for="(comment, index) in feedbackList" :key="comment.id || index">
              <div class="review-item d-flex align-items-center">
                <figure>
                  <img :src="comment.user?.img_url" alt="" />
                </figure>
                <div class="review-text">
                  <h6>
                    {{ comment.type }} · {{ comment.user?.nick_name }}
                    <span class="text-muted small"> — {{ comment.create_time }}</span>
                  </h6>
                  <p v-html="comment.content"></p>
                </div>
              </div>
              <div class="box-comment ps-4" v-for="(reply, ri) in comment.replyList" :key="ri">
                <div class="review-item d-flex align-items-center">
                  <figure>
                    <img :src="reply.user?.img_url" alt="" />
                  </figure>
                  <div class="review-text">
                    <h6>{{ reply.user?.nick_name }} <span class="text-muted small"> — {{ reply.user?.create_time }}</span></h6>
                    <p v-html="'@' + reply.send_name + reply.content"></p>
                  </div>
                </div>
              </div>
            </div>
            <button v-if="feedbackShowMore" class="btn btn-soft-primary btn-hover w-100 mt-2" type="button" @click="loadMoreFeedback">
              加载更多
            </button>
          </div>

          <hr class="mt-40 mb-40" />

          <div class="post-comment mb-25">
            <h4 class="mb-15">我要反馈</h4>
            <el-form ref="formRef" :model="formData" :rules="formRules" class="post-comment-form">
              <div class="mb-3">
                <el-form-item prop="type">
                  <el-select v-model="formData.type" placeholder="请选择类型" clearable style="width: 100%">
                    <el-option label="功能问题" value="功能问题" />
                    <el-option label="体验与交互" value="体验与交互" />
                    <el-option label="内容问题" value="内容问题" />
                    <el-option label="性能与网络" value="性能与网络" />
                    <el-option label="其他反馈" value="其他反馈" />
                  </el-select>
                </el-form-item>
                <el-form-item prop="content">
                  <WangEditor v-model="formData.content" fileUploadUrl="/upload/other" label="内容"></WangEditor>
                </el-form-item>
              </div>
              <button type="button" class="btn btn-style-1" @click="submitFeedback">
                提交反馈 <i class="far fa-envelope"></i>
              </button>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
  <Footer></Footer>
</template>

<script setup>
import Header from '@/views/front/components/header.vue'
import Footer from '@/views/front/components/footer.vue'
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import instances from '@/utils/request'
import utils from '@/utils/utils'
import WangEditor from '@/common/components/editor/wangEditor.vue'
import { useUserStore } from '@/store/userStore'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeTab = ref(route.query.tab === 'feedback' ? 'feedback' : 'notice')

const noticeList = reactive({ list: [], total: 0 })
const noticeSearch = reactive({ size: 12, current: 1 })

const feedbackList = ref([])
const feedbackSearch = reactive({ size: 20, current: 1 })
const feedbackShowMore = ref(true)

const formRef = ref()
const formData = reactive({})

const validateNotEmptyOrWhitespace = (rule, value, callback) => {
  if (!value || value.trim() === '') {
    callback(new Error(rule.message))
  } else {
    const trimmedValue = value.trim()
    const isEmptyHtml = /^<p>(<br\s*\/?>|\s|&nbsp;)*<\/p>$/i.test(trimmedValue)
    if (isEmptyHtml) {
      callback(new Error(rule.message))
    } else {
      callback()
    }
  }
}

const formRules = reactive({
  content: [
    { required: true, message: '内容不能为空', trigger: 'blur' },
    { validator: validateNotEmptyOrWhitespace, message: '内容不能为空或只包含空的 HTML 标签', trigger: 'blur' }
  ],
  type: [{ required: true, message: '请选择类型', trigger: 'blur' }]
})

const applyTabFromRoute = () => {
  const t = route.query.tab
  if (t === 'feedback') {
    activeTab.value = 'feedback'
  } else {
    activeTab.value = 'notice'
  }
}

const onTabChange = (name) => {
  router.replace({ path: '/inbox', query: { tab: name } })
}

watch(
  () => route.query.tab,
  () => {
    applyTabFromRoute()
  }
)

watch(
  activeTab,
  (name) => {
    if (name === 'feedback') {
      feedbackSearch.current = 1
      feedbackShowMore.value = true
      loadFeedbackPage(false)
    }
  },
  { immediate: true }
)

const loadNotices = async () => {
  await instances
    .post('/notice/notices/', noticeSearch)
    .then((res) => {
      noticeList.list = res.data.list
      noticeList.total = res.data.total
    })
    .catch((err) => console.error(err))
}

const loadFeedbackPage = async (append) => {
  if (!userStore.user?.id) return
  const body = { ...feedbackSearch, user_id: userStore.user.id }
  await instances
    .post('/leaveword/leavewords/', body)
    .then((res) => {
      const chunk = res.data.list || []
      const total = res.data.total ?? 0
      if (append) {
        feedbackList.value.push(...chunk)
      } else {
        feedbackList.value = chunk
      }
      feedbackShowMore.value = feedbackList.value.length < total && chunk.length > 0
    })
    .catch((err) => console.error(err))
}

const loadMoreFeedback = () => {
  feedbackSearch.current++
  loadFeedbackPage(true)
}

const submitFeedback = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    instances
      .post('/leaveword/leaveword', formData)
      .then(() => {
        utils.showSucess('提交成功!')
        formRef.value.resetFields()
        feedbackSearch.current = 1
        feedbackShowMore.value = true
        feedbackList.value = []
        loadFeedbackPage(false)
      })
      .catch((err) => console.error(err))
  })
}

onMounted(() => {
  applyTabFromRoute()
  loadNotices()
})
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
.services-page {
  min-height: 58%;
}
.inbox-intro {
  font-size: 14px;
  line-height: 1.6;
}
.inbox-tabs :deep(.el-tabs__item.is-active) {
  color: #c9a227;
}
.pagination-container {
  padding: 16px 0 0;
  justify-content: center;
  display: flex;
}
figure a {
  color: #fff !important;
}
</style>
