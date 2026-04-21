import { pa } from "element-plus/es/locales.mjs";
import { defineStore } from "pinia";
import { ref } from "vue";
import { modules } from "@/router"
import { useGetNavMenusApi,useGetPermsApi } from "../api/index"
export const useSettingStore = defineStore('settingStoreId', () => {
    /**
     * 是否需要加载基础数据状态
     */
    const isRefresh = ref(true)
    /**
     * 菜单状态
     * 默认展开
     */

    const isCollapse = ref(false)
    /**
     * 左侧菜单数据状态
     */
    const menus = ref()
    /**
    * 请求加载左侧菜单接口action
    */
    const getNavMenusAction = async () => {
        const { data } = await useGetNavMenusApi()
        menus.value = data
        return data
    }
    /**
    * 动态路由状态
    */
    const dynamicRouters = ref([])
    /**
     * 构建动态路由action
     */
    const dynamicRouterAction = async () => {
        //父节点crumbs+自己的name当作自己crumbs
        const tree = (menuList, crumbs) => {
            menuList.forEach(menu => {
                menu.crumbs = [...crumbs]
                menu.crumbs.push(menu.name)
                if (menu.children && menu.children.length > 0) {
                    tree(menu.children, menu.crumbs)
                }
                if (menu.url) {
                  
                    dynamicRouters.value.push(formatRoute(menu))
                }
            })
        }
        tree(menus.value ?? [], [])
    }
    /**
     * 组装路由
     */
    const formatRoute = (menu) => {
        const route = {
            path: menu.url,
            name: menu.name,
            component: modules[`../views${menu.component}.vue`],
            meta: {
                activeIndex: `${menu.id}`,
                title: menu.name,
                isClosable: true
            }
        }
        return route
    }
    /**
    * tabs 列表状态
    */
    const tabList = ref([])

    /**
     * tabs列表新增数据
     */
    const addTabAction = (tabNode) => {
      
        //判断新增标签数据是否存在
        //如果存在直接return
        if (tabList.value.some((tab) => {
            return tab.activeIndex === tabNode.activeIndex
        })) {
            return
        }
        //我们必须要保证首页标签放在在第一位
        if (tabList.value.length === 0 ) {
            tabList.value.push({
                title: '首页',
                isClosable: false,
                path: '/home',
                activeIndex: '1879445379477471234'
            })

          
        }
        if(tabNode.activeIndex !== '1879445379477471234'){
            tabList.value.push(tabNode)
        } 

    }
    /**
     * 用户拥有的权限编码集合状态
     */
    const perms=ref([])
    /**
     * 加载用户拥有的权限编码集合action
     */
    const getPermsAction=async()=>{
        const {data}=await useGetPermsApi()
        perms.value=data
        return data
    }
    return {
        isRefresh,
        isCollapse,
        menus,
        dynamicRouters,
        dynamicRouterAction,
        getNavMenusAction,
        tabList,
        addTabAction,
        perms,
        getPermsAction
    }
})