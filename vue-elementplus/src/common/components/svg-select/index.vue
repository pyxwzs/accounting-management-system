<template>
    <el-popover :width="600" trigger="click" :visible="visable">
        <template #reference>
            <ElInput size="large" placeholder="点击选择图标" @click="visable = !visable" v-model="inputValue" readonly>
                <template #prepend v-if="inputValue !== ''">
                    <ElIcon>
                        <component :is="inputValue"></component>
                    </ElIcon>
                </template>
            </ElInput>
        </template>
        <ElCard>
            <div style="display: flex;">
                <ElInput size="large" v-model="searchValue" clearable @input="filterIcon" @clear="filterIcon"
                    style="width: 0; flex: 1; margin-right: 5px;"></ElInput>
                <ElButton @click="cleanSelectIcon">清空</ElButton>
            </div>
            <el-tabs v-model="activeName">
                <el-tab-pane label="element-icons" name="element-icons">
                    <div style="display: flex; flex-wrap: wrap; overflow: scroll; height: 300px">
                        <div v-for="(name, index) in icons" :key="index" :index="index" @click="selectSvg(name)"
                            style="margin:  5px 5px;">
                            <el-tooltip :content="name" placement="bottom" effect="light">
                                <ElIcon size="20">
                                    <component :is="name"></component>
                                </ElIcon>
                            </el-tooltip>
                        </div>
                    </div>
                </el-tab-pane>
            </el-tabs>
        </ElCard>
    </el-popover>
</template>

<script setup>

import { ref, watch } from 'vue';
import * as Icons from '@element-plus/icons-vue'
import ToolUtils from '@/utils/ToolUtils';
const props = defineProps({	
    iconName: {
		type: String,
		require: false,
	},	
})
/**
 * elememt plus 自带svg集合
 */
const sources = Object.keys(Icons)
const icons =(sources)
const activeName = ref('element-icons')
const visable = ref(false)
const inputValue = ref('')
const searchValue = ref('')
/**
 * svg 图标模糊查询事件
 */
const filterIcon = () => {
    icons.value = sources.filter((svgName) => {
        return svgName.toLocaleLowerCase().includes(searchValue.value.toLocaleLowerCase())
    })
}
/**
 * 选择svg事件
 */
const selectSvg = (name) => {
    inputValue.value = name
    visable.value = false
    emit('selectIcon',name)
}
/**
 * 清空当前的选择
 */
const cleanSelectIcon = () => {
    inputValue.value = ''
    visable.value = false
    searchValue.value = ''
    emit('cleanSelectIcon')
    filterIcon()
}
const emit=defineEmits(['selectIcon','cleanSelectIcon'])
defineExpose({
    cleanSelectIcon
})
watch(()=>props.iconName,()=>{
    inputValue.value=props.iconName
})
</script>

<style scoped></style>
