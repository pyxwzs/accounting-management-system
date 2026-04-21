import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/store/userStore";
import { useSettingStore } from "@/store/settingStore";
import { closeNProgress, startNProgress } from "@/utils/Nprogress";
export const modules = import.meta.glob('../views/**/*.vue')
const routes = [
  {
    path: "/",
    name: 'Layout',
    component: () => import("@/layout/index.vue"),
    redirect: "/home",
    meta: { requiresAuth: true },
    children: [
      {
        path: 'user/personal',
        name: 'Personal',
        component: () => import('@/views/personal/index.vue'),
         meta: {
              activeIndex: '/views/personal',
              title: '个人资料',
              isClosable: true,
        }
      }
    ]
  },
  {
    path: "/login",
    name: 'Login',
    component: () => import("@/views/login/login.vue"),
    meta: { requiresAuth: false }
  },
  {
    path: "/register",
    name: 'Register',
    component: () => import("@/views/login/register.vue"),
    meta: { requiresAuth: false }
  },
  {
    path: "/resetPwd",
    name: 'ResetPwd',
    component: () => import("@/views/login/resetPwd.vue"),
    meta: { requiresAuth: false }
  },
  {
    path: "/:pathMatch(.*)*",
    name: '404',
    component: () => import("@/views/error/404.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/frontLogin",
    name: 'FrontLogin',
    component: () => import("@/views/front/login.vue"),
    meta: { requiresAuth: false }
  },
  {
    path: "/frontRegister",
    name: 'FrontRegister',
    component: () => import("@/views/front/register.vue"),
    meta: { requiresAuth: false }
  },
  {
    path: "/front",
    name: 'Front',
    component: () => import("@/views/front/home.vue"),
    meta: { requiresAuth: false }
  },{
    path: "/inbox",
    name: 'Inbox',
    component: () => import("@/views/front/inbox.vue"),
    meta: { requiresAuth: true }
  },{
    path: "/notice/:id",
    name: 'NoticeDetails',
    component: () => import("@/views/front/noticeDetails.vue"),
    meta: { requiresAuth: true }
  },{
    path: "/notice",
    redirect: { path: '/inbox', query: { tab: 'notice' } }
  },{
    path: "/article",
    name: 'Article',
    component: () => import("@/views/front/article.vue"),
    meta: { requiresAuth: true }
  },{
    path: "/articleDetails/:id",
    name: 'ArticleDetails',
    component: () => import("@/views/front/articleDetails.vue"),
    meta: { requiresAuth: true }
  },{
    path: "/leaveword",
    redirect: { path: '/inbox', query: { tab: 'feedback' } }
  },{
    path: "/message",
    name: 'Message',
    component: () => import("@/views/front/message.vue"),
    meta: { requiresAuth: true }
  },{
    path: "/userCenter",
    name: 'UserCenter',
    component: () => import("@/views/front/userCenter.vue"),
    meta: { requiresAuth: true }
  },{
    path: "/changeUserInfo",
    name: 'ChangeUserInfo',
    component: () => import("@/views/front/changeUserInfo.vue"),
    meta: { requiresAuth: true }
  },{
    path: "/changePassword",
    name: 'ChangePassword',
    component: () => import("@/views/front/changePassword.vue"),
    meta: { requiresAuth: true }
  },{
    path: "/info",
    name: 'Info',
    component: () => import("@/views/front/info.vue"),
    meta: { requiresAuth: true }
  },{
    path: "/charts",
    name: 'Charts',
    component: () => import("@/views/front/charts.vue"),
    meta: { requiresAuth: true }
  },{
    path: "/budget",
    name: 'Budget',
    component: () => import("@/views/front/budget.vue"),
    meta: { requiresAuth: true }
  },{
    path: "/expenses",
    name: 'Expenses',
    component: () => import("@/views/front/expenses.vue"),
    meta: { requiresAuth: true }
  },{
    path: "/plan",
    name: 'Plan',
    component: () => import("@/views/front/plan.vue"),
    meta: { requiresAuth: true }
  }
  
]

const router = createRouter({
  history: createWebHistory(),
  routes: routes
})
// 路由拦截
router.beforeEach(async (to, from) => {
  startNProgress()
  const userStore = useUserStore()
  const settingStore = useSettingStore()
  if (!userStore.token) {
    //如果用户没登录,去拦截到登录页面,且需要拦截的页面才做这一步骤
    if (to.meta.requiresAuth) {
      return {
        path: '/frontLogin'

      }
    }
  } else if (to.path === '/login' && userStore.user.roleId!=='1879449283212673025') {
    return {
      path: '/'
    }
  } else if (to.path === '/login' && userStore.user.roleId==='1879449283212673025') {
    return {
      path: '/front'
    }
  } else if (to.path === '/frontLogin') {
    return {
      path: '/front'
    }
  }  else {
    //是否需要加载基础数据
    if (settingStore.isRefresh) {
      try {
        await settingStore.getNavMenusAction()
        await settingStore.dynamicRouterAction()
     
        settingStore.dynamicRouters.forEach(route => {
          router.addRoute('Layout', route)
        })
        await settingStore.getPermsAction()
        settingStore.isRefresh = false
        return {

          path: to.path,
          query: to.query
        }
      } catch (error) {
        userStore.setToken(null);
        userStore.setUser(null);
        console.log(error);
        //重定向到login
        router.push('/login')
      }
    }

  }
})
router.afterEach((to, from) => {
  closeNProgress()
})
export default router