<template>
    <el-sub-menu :index="`${props.item.id}`" v-if="props.item.children && props.item.children.length > 0">
        <template #title>
            <el-icon v-if="props.item.icon">
                <component :is="props.item.icon"></component>
            </el-icon>
            <span>{{ props.item.name }}</span>
        </template>
        <menu-item v-for="menu in props.item.children" :item="menu" :key="menu.id" ></menu-item>
    </el-sub-menu>

    <el-menu-item :index="`${props.item.id}`" v-else @click="handleClickMenu(props.item)">
            <el-icon v-if="props.item.icon">
                <component :is="props.item.icon"></component>
            </el-icon>
         
        <template #title>{{ props.item.name }}</template>
    </el-menu-item>
   
</template>

<script setup>
import { ElNotification } from 'element-plus';
import { useRouter } from 'vue-router';
import ToolUtils from '@/utils/ToolUtils'
import { computed ,nextTick} from 'vue';
import { useRoute } from 'vue-router'
const route=useRoute()
const router = useRouter()
const props = defineProps({
    item: {
		type: Object,
		require: true
	},
		
}) 


/**
 * 菜单点击事件
 */
const handleClickMenu = async (param) => {
    console.log(param)
    //判断当前菜单是否配置了路由
    if (!param.url) {
        ElNotification({
            title: 'Warning',
            message: '请在后端菜单管理配置响应的菜单路由',
            type: 'warning',
        })
        return
    }
    if(ToolUtils.isExternalLink(param.url)){
        if(param.openWith===OpenWithEnum.OUTSIDE){
            window.open(param.url,'_blank')
        }else{
            router.push({
            path: '/redirect',
                query:{
                    src:param.url,
                    activeIndex: param.id
                }
            })
        }
    }else{
       await nextTick(() => {
            router.push(param.url);
        });
    }
   
}
</script>
<style scoped>
.el-icon:hover{
    color: var(--ct-menu-item-hover-color);
}
:deep(.el-menu) {
    background-color: var(--ct-menu-bg);  
}
.el-menu-item{
    color:var(--ct-menu-item-color);
    margin: 2px 0;
}
.el-menu-item:hover {
    color: var(--ct-menu-item-hover-color);
    background-color: #fff;  
    border-radius: 5px;
}
 .el-menu-item.is-active {
    background-color: #27b6af !important;  
    border-radius: 5px;
}

</style>
