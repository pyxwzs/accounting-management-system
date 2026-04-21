<template>
  <el-tabs
    v-model="activeName"
    class="tabs-container"
    @tab-remove="removeTab"
    @tab-click="clickTab"
  >
    <el-tab-pane
      v-for="item in settingStore.tabList"
      :key="item.activeIndex"
      :label="item.title"
      :closable="item.isClosable"
      :name="item.activeIndex"
    >
    </el-tab-pane>
  </el-tabs>
</template>

<script setup>
import { useSettingStore } from "@/store/settingStore";

import { ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();
const settingStore = useSettingStore();
const activeName = ref();

/**
 * 移除标签事件
 */
const removeTab = (targetName) => {
  //判断是否是移除当前菜单
  if (activeName.value === targetName) {
    settingStore.tabList.forEach((tab, index) => {
      if (tab.activeIndex === targetName) {
        const nextTab =
          settingStore.tabList[index + 1] || settingStore.tabList[index - 1];
        if (nextTab) {
          activeName.value = nextTab.activeIndex;
          router.push(nextTab.path);
        }
      }
    });
  }

  settingStore.tabList = settingStore.tabList.filter(
    (tab) => tab.activeIndex !== targetName
  );
};
/**
 * 标签点击事件
 */
const clickTab = (pane, _ev) => {
  const tab = settingStore.tabList.find((tabNode) => {
    return tabNode.activeIndex === pane.props.name;
  });
  if (tab) {
    router.push(tab.path);
  }
};
watch(
  route,
  () => {
    const tab = {
      title: route.meta.title,
      path: route.path,
      isClosable: route.meta.isClosable,
      activeIndex: route.meta.activeIndex,
    };
    settingStore.addTabAction(tab);
    activeName.value = tab.activeIndex;
  },
  {
    immediate: true,
  }
);
</script>

<style scoped>
.tabs-container {
  height:50px;
  margin-bottom: 10px;
 
}
.tabs-container :deep(.el-tabs__header) {
  margin-bottom: -5px;
}
:deep(.el-tabs__nav-wrap:after){
  height: 0px;
}
.tabs-container :deep(.el-tabs__nav-prev) {
  padding: 0 10px;
  border-right: #f2f6fc 1px solid;
}

.tabs-container :deep(.el-tabs__nav-next) {
  padding: 0 10px;
  border-left: #f2f6fc 1px solid;
}

.tabs-container :deep(.is-scrollable) {
  padding: 0 32px;
}

.tabs-container :deep(.el-tabs__active-bar) {
  height: 0;
}

.tabs-container :deep(.el-tabs__item .is-icon-close) {
  transition: none !important;
}

.tabs-container :deep(.el-tabs__item .is-icon-close:hover) {
  color: var(--el-color-primary-light-9);
  background-color: var(--el-color-primary);
  border-radius: 50%;
  
}

.tabs-container :deep(.el-tabs__item) {
  padding: 0 15px !important;
  height: 35px;
  background-color: #00000017;
  user-select: none;
  color: #8c8c8c;
  margin-right: 5px;
  font-size: 14px;
  border-radius: 5px;
}

.tabs-container :deep(.el-tabs__item:hover) {
  color: #444;
  background: rgba(0, 0, 0, 0.02)!important;
}

.tabs-container :deep(.el-tabs__item.is-active) {
  color: #fff!important;
  background-color: var(--el-color-primary);

}

.tabs-container :deep(.el-tabs__item.is-active:before) {
  background-color: #fff;
}

.tabs-container :deep(.el-tabs__item:before) {
  content: "";
  width: 4px;
  height: 4px;
  margin-right: 8px;
  display: inline-block;
  background-color: #000000;
  border-radius: 50%;
}
</style>
