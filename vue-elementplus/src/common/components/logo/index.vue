<template>
    <div v-if="props.showTitle" class="logo-view" :class="{'logo-view-direction-column':props.isCloumn}">
        <div :class="{'title-view-direction-row':props.isCloumn}">{{siteConfig.siteLogo}}</div>
    </div>
    
</template>

<script setup >
import ToolUtils from '@/utils/ToolUtils'
import { defineProps, withDefaults } from 'vue';
import * as siteConfig  from "@/config/index.js";
const props = defineProps({	
    size: {
		type: Number,
		require: false,
        default: 50
	},
	image: {
		type: String,
		require: false,
        default: 'vue.png'
	},
	showTitle: {
		type: Boolean,
		require: true
	},
	isCloumn: {
		type: Boolean,
		require: true
	},			
})
</script>

<style scoped>
.logo-view {
    width: auto;
    height: 70px;
    line-height: 70px;
    display: flex;
    font-size: 16px;
}

.logo-title-hide {
    width: 100% !important;
}

.title-view-direction-row {
    font-size: 18px;
    font-weight: bold;
    background:#0c0c0c;
    background-clip: text;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;

}

.logo-view-direction-column {
    height: 70px !important;
    flex-direction: column;
}
</style>
